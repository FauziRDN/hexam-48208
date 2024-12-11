package com.hand.demo.domain.repository;

import com.hand.demo.api.dto.InvoiceHeaderDTO;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * (InvoiceApplyHeader)资源库
 *
 * @author Fauzi
 * @since 2024-12-03 09:34:44
 */
public interface InvoiceApplyHeaderRepository extends BaseRepository<InvoiceApplyHeader> {
    static Page<InvoiceApplyHeader> listInvoiceHeaders(String searchQuery, InvoiceHeaderDTO invoiceHeaderDTO, PageRequest pageRequest) {
        return listInvoiceHeaders(searchQuery, invoiceHeaderDTO, pageRequest);
    }
    List<InvoiceApplyLine> findLinesByHeaderId(@Param("headerId") Long headerId);
    boolean isValidValue(String value, String lovCode);
    /**
     * 查询
     *
     * @param invoiceApplyHeader 查询条件
     * @return 返回值
     */
    List<InvoiceApplyHeader> selectList(InvoiceApplyHeader invoiceApplyHeader);

    /**
     * 根据主键查询（可关联表）
     *
     * @param applyHeaderId 主键
     * @return 返回值
     */
    InvoiceApplyHeader selectByPrimary(Long applyHeaderId);

}
