package com.hand.demo.domain.dto;

import com.hand.demo.domain.entity.InvoiceApplyLine;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("Invoice Header")
@ModifyAudit
@VersionAudit
@Table(name = "invoice_header")
@Data // Menggunakan Lombok untuk otomatis generate getter/setter, equals, hashcode, toString
public class InvoiceHeaderDTO  {

    @ApiModelProperty("ID")
    @Id
    @GeneratedValue
    private Long id;

    @ApiModelProperty(value = "Invoice Number")
    @NotNull
    private String invoiceNumber;

    @ApiModelProperty(value = "Invoice Type")
    @NotNull
    @LovValue(lovCode = "INVOICE_TYPE", meaningField = "invoiceTypeMeaning")
    private String invoiceType;

    @ApiModelProperty(value = "Invoice Type Meaning")
    private String invoiceTypeMeaning;

    @ApiModelProperty(value = "Deleted Flag")
    private Integer deletedFlag;

    @ApiModelProperty(value = "Line Details")
    private List<InvoiceHeaderDTO> lines;

}
