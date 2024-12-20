package com.hand.demo.app.service;

import com.hand.demo.api.dto.InvoiceHeaderDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.api.dto.InvoiceHeaderDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * (InvoiceApplyHeader)应用服务
 *
 * @author Fauzi
 * @since 2024-12-03 09:34:45
 */
public interface InvoiceApplyHeaderService {

    /**
     * 查询数据
     *
     * @param pageRequest         分页参数
     * @param invoiceApplyHeaders 查询条件
     * @return 返回值
     */
    Page<InvoiceApplyHeader> selectList(PageRequest pageRequest, InvoiceApplyHeader invoiceApplyHeaders);

    //nomor empat buat munculin redis
    ResponseEntity<InvoiceApplyHeader> detailWithLine(Long applyHeaderId);

    /**
     * 保存数据
     *
     * @param invoiceApplyHeaders 数据
     */
    void saveData(List<InvoiceApplyHeader> invoiceApplyHeaders);
    void deleteRedisCache(InvoiceApplyHeader header);
    ResponseEntity<InvoiceApplyHeader> deleteById(Long applyHeaderId);
    void updateHeaderAmounts(Long applyHeaderId);


    void invoiceSchedulingTask(String delFlag, String applyStatus, String invoiceColor, String invoiceType);
    Page<InvoiceApplyHeader> selectListTenant(PageRequest pageRequest, InvoiceApplyHeader invoiceApplyHeader);
}

