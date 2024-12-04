package com.hand.demo.app.service.impl;

import com.hand.demo.api.dto.InvoiceHeaderDTO;
import com.hand.demo.infra.mapper.InvoiceApplyHeaderMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    private InvoiceApplyHeaderMapper invoiceApplyHeaderMapper;

    @Autowired
    private LovAdapter lovAdapter;

    @Override
    public Page<InvoiceHeaderDTO> selectList(PageRequest pageRequest, InvoiceApplyHeader invoiceApplyHeader) {
        Page<InvoiceHeaderDTO> page = PageHelper.doPageAndSort(pageRequest,
                () -> invoiceApplyHeaderRepository.selectList(invoiceApplyHeader));

        // Iterate over each header in the page and translate necessary fields
        page.stream().map(header -> {
            InvoiceHeaderDTO invoiceHeaderDTO = new InvoiceHeaderDTO();
            BeanUtils.copyProperties(header, invoiceHeaderDTO);

            // Translate the values using LOV (Value Set) adapter
            invoiceHeaderDTO.setInvoiceColorMeaning(lovAdapter.queryLovMeaning("HEXAM-INV-HEADER-COLOR-48208", header.getTenantId(), header.getInvoiceColor()));
            invoiceHeaderDTO.setInvoiceTypeMeaning(lovAdapter.queryLovMeaning("HEXAM-INV-HEADER-TYPE-48208", header.getTenantId(), header.getInvoiceType()));
            invoiceHeaderDTO.setApplyStatusMeaning(lovAdapter.queryLovMeaning("HEXAM-INV-HEADER-STATUS-48208", header.getTenantId(), header.getApplyStatus()));

            return invoiceHeaderDTO;
        }).collect(Collectors.toList());


        return page;
    }

    @Override
    public void saveData(List<InvoiceApplyHeader> invoiceApplyHeaders) {
        List<InvoiceApplyHeader> insertList = invoiceApplyHeaders.stream().filter(line -> line.getApplyHeaderId() == null).collect(Collectors.toList());
        List<InvoiceApplyHeader> updateList = invoiceApplyHeaders.stream().filter(line -> line.getApplyHeaderId() != null).collect(Collectors.toList());
        invoiceApplyHeaderRepository.batchInsertSelective(insertList);
        invoiceApplyHeaderRepository.batchUpdateByPrimaryKeySelective(updateList);
    }
}

