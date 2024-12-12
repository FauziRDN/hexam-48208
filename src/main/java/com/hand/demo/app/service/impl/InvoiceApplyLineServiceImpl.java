package com.hand.demo.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import com.hand.demo.app.service.InvoiceApplyLineService;
import org.springframework.stereotype.Service;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * (InvoiceApplyLine)应用服务
 *
 * @author Fauzi
 * @since 2024-12-03 11:03:34
 */
@Service
public class InvoiceApplyLineServiceImpl implements InvoiceApplyLineService {
    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;
    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private InvoiceApplyHeaderService invoiceApplyHeaderService;

    @Override
    public Page<InvoiceApplyLine> selectList(PageRequest pageRequest, InvoiceApplyLine invoiceApplyLine) {
        return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyLineRepository.selectList(invoiceApplyLine));
    }

    @Override
    public Page<InvoiceApplyLine> selectListRD(PageRequest pageRequest, InvoiceApplyLine invoiceApplyLine) {
        return PageHelper.doPageAndSort(pageRequest, () -> invoiceApplyLineRepository.selectListRD(invoiceApplyLine));
    }

    //nomor tujuh insert update invoice line
    @Override
    public void saveData(List<InvoiceApplyLine> invoiceApplyLines) {
        List<InvoiceApplyLine> validLines = invoiceApplyLines.stream()
                .filter(line -> line.getApplyLineId() == null)
                .filter(line -> invoiceApplyLineRepository.isValidHeaderId(line.getApplyHeaderId()))
                .collect(Collectors.toList());

        for (InvoiceApplyLine line : validLines) {
            BigDecimal totalAmount = line.getUnitPrice().multiply(line.getQuantity());
            BigDecimal taxAmount = totalAmount.multiply(line.getTaxRate());
            BigDecimal excludeTaxAmount = totalAmount.subtract(taxAmount);

            line.setTotalAmount(totalAmount);
            line.setTaxAmount(taxAmount);
            line.setExcludeTaxAmount(excludeTaxAmount);
        }
        List<InvoiceApplyLine> updateList = invoiceApplyLines.stream().filter(line -> line.getApplyLineId() != null).map(line -> {
            InvoiceApplyLine currVer = invoiceApplyLineRepository.selectByPrimaryKey(line.getApplyLineId());
            if (currVer != null) {
                line.setObjectVersionNumber(currVer.getObjectVersionNumber());
            }
            return  line;
        }).collect(Collectors.toList());


        invoiceApplyLineRepository.batchInsertSelective(validLines);
        invoiceApplyLineRepository.batchUpdateByPrimaryKeySelective(updateList);


        Set<Long> headerIds = invoiceApplyLines.stream()
                .map(InvoiceApplyLine::getApplyHeaderId)
                .collect(Collectors.toSet());
        for (Long headerId : headerIds) {
            invoiceApplyHeaderService.updateHeaderAmounts(headerId);
            if (invoiceApplyLines != null) {
                updateRedisCache(headerId);
            }
        }
    }

    @Transactional
    @Override
    public void saveInvoiceLine(Long organizationId, Long headerId, InvoiceApplyLine invoiceApplyLine) {
        // Validate if apply_header_id exists and is not deleted
        InvoiceApplyHeader header = invoiceApplyHeaderRepository.selectByPrimary(headerId);
    
        if (header == null) {
            throw new IllegalArgumentException("Invoice header with ID " + headerId + " does not exist.");
        }
    
        if (header.getDelFlag() == 1) {
            throw new IllegalArgumentException("Invoice header with ID " + headerId + " is deleted.");
        }
    
        // Hitung nilai dan simpan
        List<InvoiceApplyLine> singleLineList = Collections.singletonList(invoiceApplyLine);
        calculateAndSaveLines(singleLineList);
    
        // Simpan line setelah perhitungan selesai
        invoiceApplyLineRepository.insertSelective(invoiceApplyLine);
    }

    //nomor tujuh untuk calkulasi data
    @Transactional
    public void calculateAndSaveLines(List<InvoiceApplyLine> invoiceApplyLines) {
        for (InvoiceApplyLine line : invoiceApplyLines) {
            // Hitung nilai berdasarkan baris
            BigDecimal unitPrice = new BigDecimal(line.getUnitPrice().toString());
            BigDecimal quantity = new BigDecimal(line.getQuantity().toString());
            BigDecimal totalAmount = unitPrice.multiply(quantity);
            BigDecimal taxAmount = totalAmount.multiply(line.getTaxRate());
            BigDecimal excludeTaxAmount = totalAmount.subtract(taxAmount);

            // Set nilai ke line
            line.setTotalAmount(totalAmount);
            line.setTaxAmount(taxAmount);
            line.setExcludeTaxAmount(excludeTaxAmount);
        }
    }

    //update data redist chance
    @Override
    public void updateRedisCache(Long applyHeaderId) {
        String headerCacheKey = "Hexam-48208:Exam" + applyHeaderId;
        InvoiceApplyHeader invoiceApplyHeader = invoiceApplyHeaderRepository.selectByPrimary(applyHeaderId);
        List<InvoiceApplyLine> invoiceApplyLines = invoiceApplyLineRepository.selectByHeaderId(applyHeaderId);
        invoiceApplyHeader.setInvoiceApplyLines(invoiceApplyLines);
        redisHelper.strSet(headerCacheKey, JSON.toJSONString(invoiceApplyHeader), 6, TimeUnit.MINUTES);
    }



}

