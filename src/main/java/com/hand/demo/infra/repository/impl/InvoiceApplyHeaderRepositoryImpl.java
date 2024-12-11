package com.hand.demo.infra.repository.impl;

import com.hand.demo.domain.entity.InvoiceApplyLine;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.infra.mapper.InvoiceApplyHeaderMapper;

import javax.annotation.Resource;
import java.util.*;

/**
 * (InvoiceApplyHeader)资源库
 *
 * @author Fauzi
 * @since 2024-12-03 09:34:45
 */
@Component
public class InvoiceApplyHeaderRepositoryImpl extends BaseRepositoryImpl<InvoiceApplyHeader> implements InvoiceApplyHeaderRepository {
    @Resource
    private InvoiceApplyHeaderMapper invoiceApplyHeaderMapper;

    @Override
    public List<InvoiceApplyLine> findLinesByHeaderId(Long headerId) {
        return Collections.emptyList();
    }

    @Override
    public List<InvoiceApplyHeader> selectList(InvoiceApplyHeader invoiceApplyHeader) {
        return invoiceApplyHeaderMapper.selectList(invoiceApplyHeader);
    }

    @Override
    public InvoiceApplyHeader selectByPrimary(Long applyHeaderId) {
        InvoiceApplyHeader invoiceApplyHeader = new InvoiceApplyHeader();
        invoiceApplyHeader.setApplyHeaderId(applyHeaderId);
        List<InvoiceApplyHeader> invoiceApplyHeaders = invoiceApplyHeaderMapper.selectList(invoiceApplyHeader);
        if (invoiceApplyHeaders.size() == 0) {
            return null;
        }
        return invoiceApplyHeaders.get(0);
    }
    @Override
    public boolean isValidValue(String value, String lovCode) {
        Map<String, List<String>> lovValues = new HashMap<>();
        lovValues.put("HEXAM-INV-HEADER-STATUS-48208", Arrays.asList("D", "S", "F", "C"));
        lovValues.put("HEXAM-INV-HEADER-COLOR-48208", Arrays.asList("R", "B"));
        lovValues.put("HEXAM-INV-HEADER-TYPE-48208", Arrays.asList("P", "E"));

        return lovValues.get(lovCode).contains(value);
    }

}

