package com.hand.demo.app.handler;

import com.hand.demo.app.service.InvoiceInfoQueueService;
import com.hand.demo.domain.entity.InvoiceInfoQueue;
import org.hzero.core.redis.handler.IQueueHandler;
import org.hzero.core.redis.handler.QueueHandler;
import com.hand.demo.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@QueueHandler("HEXAM-INV-HEADER-QUEUE")
public class InvoiceHeaderListener implements IQueueHandler {

    private final InvoiceInfoQueueService queueService;

    @Autowired
    public InvoiceHeaderListener(InvoiceInfoQueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public void process(String message) {
        // Parse the message and create an InvoiceInfoQueue object to process it.
        InvoiceInfoQueue queue = new InvoiceInfoQueue();
        queue.setContent(message);
        queue.setEmployeeId(Constants.EMPLOYEE_ID);
        queue.setTenantId(Constants.ORGANIZATION_ID);
        // Save the list of processed queue data
        List<InvoiceInfoQueue> invoiceInfoQueues = new ArrayList<>();
        invoiceInfoQueues.add(queue);
        queueService.saveData(invoiceInfoQueues);
    }
}
