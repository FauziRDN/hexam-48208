package com.hand.demo.api.dto;


import com.hand.demo.domain.entity.InvoiceApplyHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ExcelSheet(en = "ReportDTO")
public class ReportDTO extends InvoiceApplyHeader {
//    @ExcelColumn(en = "Invoice Color Meaning", order = 7, lovCode = "HEXAM-INV-HEADER-STATUS-48208")
//    private String applyStatusMeaning;
//
//    @ExcelColumn(en = "Invoice Type", order = 19, lovCode = "HEXAM-INV-HEADER-TYPE-48208")

}
