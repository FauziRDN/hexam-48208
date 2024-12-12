package com.hand.demo.app.service.impl;


import com.alibaba.fastjson.JSON;
import com.hand.demo.app.service.InvoiceApplyLineService;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@ImportService(templateCode = "IMPORT.LINE.48208")
public class ImportLineServiceImpl implements IBatchImportService {
    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;
    @Autowired
    private InvoiceApplyLineService invoiceApplyLineService;

    @Override
    public Boolean doImport(List<String> data) {
        List<InvoiceApplyLine> invoiceApplyLinesImport = new ArrayList<>();

        for (String line : data) {
            InvoiceApplyLine invoiceApplyLine = JSON.parseObject(line, InvoiceApplyLine.class);
            InvoiceApplyLine queryParam = new InvoiceApplyLine();
            queryParam.setApplyLineId(invoiceApplyLine.getApplyLineId());
            InvoiceApplyLine invoiceApplyLines = null;
            if (invoiceApplyLine.getApplyLineId() != null) {
                invoiceApplyLines = invoiceApplyLineRepository.selectOne(queryParam);
            }
            if (invoiceApplyLines == null) {
                Long tenantId = DetailsHelper.getUserDetails().getTenantId();
                invoiceApplyLine.setTenantId(tenantId);
                invoiceApplyLinesImport.add(invoiceApplyLine);
            } else {
                invoiceApplyLine.setApplyLineId(invoiceApplyLines.getApplyLineId());
                invoiceApplyLine.setObjectVersionNumber(invoiceApplyLines.getObjectVersionNumber());
                invoiceApplyLinesImport.add(invoiceApplyLine);
            }
        }
        if (!invoiceApplyLinesImport.isEmpty()) {
            invoiceApplyLineService.saveData(invoiceApplyLinesImport);
        }

        return true;
    }
}
