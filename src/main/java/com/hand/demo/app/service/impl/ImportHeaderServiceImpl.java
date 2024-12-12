package com.hand.demo.app.service.impl;


import com.alibaba.fastjson.JSON;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.app.service.InvoiceApplyLineService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import io.choerodon.core.oauth.DetailsHelper;
import io.micrometer.core.instrument.util.StringUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ImportService(templateCode = "IMPORT.HEADER.48208")
public class ImportHeaderServiceImpl implements IBatchImportService {
    @Autowired
    private InvoiceApplyHeaderService invoiceApplyHeaderService;
    @Autowired
    private InvoiceApplyLineService invoiceApplyLineService;

    @Autowired
    InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Override
    public Boolean doImport(List<String> data) {
        List<InvoiceApplyHeader> invoiceApplyHeaders = new ArrayList<>();
        for (String header : data) {
            InvoiceApplyHeader importedHeader = JSON.parseObject(header, InvoiceApplyHeader.class);
            InvoiceApplyHeader queryParam = new InvoiceApplyHeader();
            queryParam.setApplyHeaderNumber(importedHeader.getApplyHeaderNumber());
            InvoiceApplyHeader existingHeader = null;
            if (StringUtils.isNotBlank(importedHeader.getApplyHeaderNumber())) {
                existingHeader = invoiceApplyHeaderRepository.selectOne(queryParam);
            }
            if (existingHeader != null) {
                importedHeader.setApplyHeaderId(existingHeader.getApplyHeaderId());
                importedHeader.setObjectVersionNumber(existingHeader.getObjectVersionNumber());
                invoiceApplyHeaders.add(importedHeader);

            } else {
                Long tenantId = DetailsHelper.getUserDetails().getTenantId();
                importedHeader.setTenantId(tenantId);
                importedHeader.setTaxAmount(BigDecimal.ZERO);
                importedHeader.setExcludeTaxAmount(BigDecimal.ZERO);
                importedHeader.setTotalAmount(BigDecimal.ZERO);
                invoiceApplyHeaders.add(importedHeader);
            }
        }
        if (!invoiceApplyHeaders.isEmpty()) {
            invoiceApplyHeaderService.saveData(invoiceApplyHeaders);
        }
        return true;

    }

}
