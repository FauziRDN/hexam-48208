package com.hand.demo.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * (InvoiceApplyLine)应用服务
 *
 * @author Fauzi
 * @since 2024-12-03 11:03:34
 */
public interface InvoiceApplyLineService {

    /**
     * 查询数据
     *
     * @param pageRequest       分页参数
     * @param invoiceApplyLines 查询条件
     * @return 返回值
     */
    Page<InvoiceApplyLine> selectList(PageRequest pageRequest, InvoiceApplyLine invoiceApplyLines);

    /**
     * 保存数据
     *
     * @param invoiceApplyLines 数据
     */
    void saveData(List<InvoiceApplyLine> invoiceApplyLines);

    @Transactional
    void saveInvoiceLine(Long organizationId, Long headerId, InvoiceApplyLine invoiceApplyLine);
    void updateRedisCache(Long applyHeaderId);
}

