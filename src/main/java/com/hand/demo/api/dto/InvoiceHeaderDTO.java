package com.hand.demo.api.dto;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("Invoice Header")
@Setter
@Getter
public class InvoiceHeaderDTO extends InvoiceApplyHeader {

    @ApiModelProperty(value = "Apply Status Meaning")
    private String applyStatusMeaning;

    @ApiModelProperty(value = "Invoice Color Meaning")
    private String invoiceColorMeaning;

    @ApiModelProperty(value = "Invoice Type Meaning")
    private String invoiceTypeMeaning;

//    public void setInvoiceStatus(String s) {
//    }
}
