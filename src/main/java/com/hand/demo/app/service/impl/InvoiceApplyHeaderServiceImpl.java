package com.hand.demo.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.app.service.InvoiceApplyLineService;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import com.hand.demo.infra.mapper.InvoiceApplyHeaderMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.github.resilience4j.core.StringUtils;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.RedisQueueHelper;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.hzero.core.util.Results.success;


/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author Fauzi
 * @since 2024-12-03 09:34:45
 */
@Service
public class InvoiceApplyHeaderServiceImpl implements InvoiceApplyHeaderService {
    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;
    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;
    @Autowired
    private InvoiceApplyLineService invoiceApplyLineService;

    @Autowired
    private InvoiceApplyHeaderMapper invoiceApplyHeaderMapper;

    @Autowired
    private LovAdapter lovAdapter;

    @Autowired
    private RedisHelper redisHelper;
    private RedisQueueHelper redisQueueHelper;
    @Autowired
    private CodeRuleBuilder codeRuleBuilder;
    private static final Logger logger = LoggerFactory.getLogger(InvoiceApplyHeaderServiceImpl.class);

    //nomor tiga buat muncilin list
    @Override
    public Page<InvoiceApplyHeader> selectList(PageRequest pageRequest, InvoiceApplyHeader invoiceApplyHeader) {
        return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyHeaderRepository.selectList(invoiceApplyHeader));
    }

    //nomor empat buat munculin redis
    @Override
    public ResponseEntity<InvoiceApplyHeader> detailWithLine(Long applyHeaderId) {
        String redisKey = "Hexam-48208:Exam" + applyHeaderId;
        String redisValue = redisHelper.strGet(redisKey);
        InvoiceApplyHeader invoiceApplyHeader;
        List<InvoiceApplyLine> invoiceApplyLines;
        InvoiceApplyHeader invoice= invoiceApplyHeaderMapper.selectByPrimaryKey(applyHeaderId);
        CustomUserDetails customerDetails = DetailsHelper.getUserDetails();
        invoice.setRequester(customerDetails.getRealName());
        if (StringUtils.isNotEmpty(redisValue)) {
            invoiceApplyHeader = JSON.parseObject(redisValue, InvoiceApplyHeader.class);
        } else {
            invoiceApplyHeader = invoiceApplyHeaderRepository.selectByPrimaryKey(applyHeaderId);
            InvoiceApplyLine invoiceApplyLine = new InvoiceApplyLine();
            invoiceApplyLine.setApplyHeaderId(invoiceApplyHeader.getApplyHeaderId());
            invoiceApplyLines = invoiceApplyLineRepository.selectList(invoiceApplyLine);
            //invoiceApplyLines = invoiceApplyLineRepository.selectByHeaderId(applyHeaderId);
            invoiceApplyHeader.setInvoiceApplyLines(invoiceApplyLines);
            redisHelper.strSet(redisKey, JSON.toJSONString(invoiceApplyHeader));
        }
        return success(invoiceApplyHeader);
    }

    //nomor lima
    @Override
    public void saveData(List<InvoiceApplyHeader> invoiceApplyHeaders) {
        List<InvoiceApplyHeader> validHeaders = invoiceApplyHeaders.stream()
                .filter(header -> invoiceApplyHeaderRepository.isValidValue(header.getApplyStatus(), "HEXAM-INV-HEADER-STATUS-48208"))
                .filter(header -> invoiceApplyHeaderRepository.isValidValue(header.getInvoiceColor(), "HEXAM-INV-HEADER-COLOR-48208"))
                .filter(header -> invoiceApplyHeaderRepository.isValidValue(header.getInvoiceType(), "HEXAM-INV-HEADER-TYPE-48208"))
                .collect(Collectors.toList());

        if (validHeaders.size() != invoiceApplyHeaders.size()) {
            throw new CommonException("Some invoice apply headers have invalid values for apply_status, invoice_color, or invoice_type");
        }

        List<InvoiceApplyHeader> insertList = invoiceApplyHeaders.stream().filter(line -> line.getApplyHeaderId() == null)
//                .peek(header -> header.setApplyHeaderNumber(codeRuleBuilder.generateCode("HEXAM-48208-EXAM", new HashMap<>())))
                .peek(header -> header.setApplyHeaderNumber(generateApplyHeaderNumber()))
                .collect(Collectors.toList());
        List<InvoiceApplyHeader> updateList = invoiceApplyHeaders.stream().filter(line -> line.getApplyHeaderId() != null)
                .peek(header -> {
                    InvoiceApplyHeader currentVersion = invoiceApplyHeaderRepository.selectByPrimaryKey(header.getApplyHeaderId());
                    if (currentVersion != null) {
                        header.setObjectVersionNumber(header.getObjectVersionNumber());
                    }
                }).collect(Collectors.toList());


        invoiceApplyHeaderRepository.batchInsertSelective(insertList);
        invoiceApplyHeaderRepository.batchUpdateByPrimaryKeySelective(updateList);

        Set<Long> headerIds = invoiceApplyHeaders.stream()
                .map(InvoiceApplyHeader::getApplyHeaderId)
                .collect(Collectors.toSet());
        for (Long headerId : headerIds) {
            updateHeaderAmounts(headerId);
            if (invoiceApplyHeaders != null) {
                invoiceApplyLineService.updateRedisCache(headerId);
            }
        }


    }

    @Override
    @Transactional
    public void updateHeaderAmounts(Long applyHeaderId) {
        List<InvoiceApplyLine> lines = invoiceApplyLineRepository.selectByHeaderId(applyHeaderId);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal excludeTaxAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        for (InvoiceApplyLine line : lines) {
            totalAmount = totalAmount.add(line.getTotalAmount());
            excludeTaxAmount = excludeTaxAmount.add(line.getExcludeTaxAmount());
            taxAmount = taxAmount.add(line.getTaxAmount());
        }

        InvoiceApplyHeader header = invoiceApplyHeaderRepository.selectByPrimary(applyHeaderId);
        if (header != null){
            header.setTotalAmount(totalAmount);
            header.setExcludeTaxAmount(excludeTaxAmount);
            header.setTaxAmount(taxAmount);
            header.setObjectVersionNumber(header.getObjectVersionNumber());
        }

        invoiceApplyHeaderRepository.updateByPrimaryKeySelective(header);
    }

    @Override
    public void invoiceSchedulingTask(String delFlag, String applyStatus, String invoiceColor, String invoiceType) {
        List<InvoiceApplyHeader> invoiceApplyHeaders = invoiceApplyHeaderRepository.selectList(new InvoiceApplyHeader() {{
            setDelFlag(Integer.parseInt(delFlag));
            setApplyStatus(applyStatus);
            setInvoiceColor(invoiceColor);
            setInvoiceType(invoiceType);
        }});
        if (invoiceApplyHeaders.isEmpty()) {
            logger.info("InvoiceApplyHeaders is empty for scheduling task");
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // Convert each InvoiceApplyHeader to JSON string and collect into a List
        try {
            // Convert list to JSON string
            String jsonString = objectMapper.writeValueAsString(invoiceApplyHeaders);
            // Save to Redis Message Queue
            String redisKey = "Hexam-48208:Exam";
            redisQueueHelper.push(redisKey, jsonString);
        } catch (JsonProcessingException e) {
            throw new CommonException("Error converting list to JSON");
        }
    }


    private String generateApplyHeaderNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date()); // Format tanggal menjadi yyyyMMdd

        // Menyusun map dengan variabel yang diperlukan
        Map<String, String> variableMap = new HashMap<>();
        variableMap.put("datePart", datePart);

        // Gunakan CodeRuleBuilder untuk menghasilkan urutan kode
        // Misalnya, "Invoice-" + datePart + "-" adalah format yang diinginkan
        String ruleCode = "HEXAM-48208-EXAM";

        // Menggunakan generateCode untuk menghasilkan kode berdasarkan aturan

        return codeRuleBuilder.generateCode(ruleCode, variableMap);
    }

    public boolean validastor(String value) {
        List<String> validApplyStatus = Arrays.asList("D", "S", "F", "C");
        List<String> validInvoiceColor = Arrays.asList("R", "B");
        List<String> validInvoiceType = Arrays.asList("P", "E");

        return validApplyStatus.contains(value) || validInvoiceColor.contains(value) || validInvoiceType.contains(value);
    }

    //nomor enam
    @Override
    public ResponseEntity<InvoiceApplyHeader> deleteById(Long applyHeaderId) {
        InvoiceApplyHeader invoiceApplyHeader = invoiceApplyHeaderRepository.selectByPrimaryKey(applyHeaderId);
        if (invoiceApplyHeader != null) {
            invoiceApplyHeader.setDelFlag(1);
            invoiceApplyHeaderRepository.updateByPrimaryKeySelective(invoiceApplyHeader);
            deleteRedisCache(invoiceApplyHeader);
        }
        return Results.success(invoiceApplyHeader);
    }
    //nomor enam untuk manggil redis
    @Override
    public void deleteRedisCache(InvoiceApplyHeader header){
        String cacheKey = "Hexam-48208:Exam" + header.getApplyHeaderId();
        redisHelper.delKey(cacheKey);
    }
}

