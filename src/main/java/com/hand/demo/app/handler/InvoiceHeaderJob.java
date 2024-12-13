package com.hand.demo.app.handler;

import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import org.hzero.boot.scheduler.infra.annotation.JobHandler;
import org.hzero.boot.scheduler.infra.enums.ReturnT;
import org.hzero.boot.scheduler.infra.handler.IJobHandler;
import org.hzero.boot.scheduler.infra.tool.SchedulerTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@JobHandler("Hexam-48208:Exam")
public class InvoiceHeaderJob implements IJobHandler {

    private final InvoiceApplyHeaderService headerService;

    @Autowired
    private InvoiceHeaderJob(InvoiceApplyHeaderService headerService) {
        this.headerService = headerService;
    }

    @Override
    public ReturnT execute(Map<String, String> map, SchedulerTool tool) {
//        System.out.println("HEXAM-INV-HEADER-JOB");
        // Get parameter from scheduling task
        headerService.invoiceSchedulingTask(map.get("delFlag"), map.get("applyStatus"), map.get("invoiceColor"),
                map.get("invoiceType"));
        return ReturnT.SUCCESS;

    }
}
