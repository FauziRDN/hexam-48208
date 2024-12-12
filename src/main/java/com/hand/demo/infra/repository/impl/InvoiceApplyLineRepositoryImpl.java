package com.hand.demo.infra.repository.impl;

import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import com.hand.demo.infra.mapper.InvoiceApplyLineMapper;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * (InvoiceApplyLine)资源库
 *
 * @author Fauzi
 * @since 2024-12-03 11:03:34
 */
@Component
public class InvoiceApplyLineRepositoryImpl extends BaseRepositoryImpl<InvoiceApplyLine> implements InvoiceApplyLineRepository {
    @Resource
    private InvoiceApplyLineMapper invoiceApplyLineMapper;
    @Resource
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Override
    public List<InvoiceApplyLine> selectList(InvoiceApplyLine invoiceApplyLine) {
        return invoiceApplyLineMapper.selectList(invoiceApplyLine);
    }

    @Override
    public List<InvoiceApplyLine> selectListRD(InvoiceApplyLine invoiceApplyLine) {
        return invoiceApplyLineMapper.selectListRD(invoiceApplyLine);
    }

    @Override
    public InvoiceApplyLine selectByPrimary(Long applyLineId) {
        InvoiceApplyLine invoiceApplyLine = new InvoiceApplyLine();
        invoiceApplyLine.setApplyLineId(applyLineId);
        List<InvoiceApplyLine> invoiceApplyLines = invoiceApplyLineMapper.selectList(invoiceApplyLine);
        if (invoiceApplyLines.size() == 0) {
            return null;
        }
        return invoiceApplyLines.get(0);
    }

    @Override
    public List<InvoiceApplyLine> selectByHeaderId(Long applyHeaderId) {
        return Collections.emptyList();
    }
    @Override
    public boolean isValidHeaderId(Long applyHeaderId) {
        if (applyHeaderId == null) {
            return false;
        }
        InvoiceApplyHeader header = invoiceApplyHeaderRepository.selectByPrimary(applyHeaderId);
        return header != null && header.getDelFlag() == 0;
    }
}

