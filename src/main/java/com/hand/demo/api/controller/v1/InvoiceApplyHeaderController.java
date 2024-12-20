package com.hand.demo.api.controller.v1;

import com.hand.demo.api.dto.InvoiceHeaderDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * (InvoiceApplyHeader)表控制层
 *
 * @author Fauzi
 * @since 2024-12-03 09:34:45
 */

@RestController("invoiceApplyHeaderController.v1")
@RequestMapping("/v1/{organizationId}/invoice-apply-headers")
public class InvoiceApplyHeaderController extends BaseController {

    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Autowired
    private InvoiceApplyHeaderService invoiceApplyHeaderService;

    //nomor tiga buat muncilin list
    @ApiOperation(value = "List")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    public ResponseEntity<Page<InvoiceApplyHeader>> list(InvoiceApplyHeader invoiceApplyHeader, @PathVariable Long organizationId, @ApiIgnore @SortDefault(value = InvoiceApplyHeader.FIELD_APPLY_HEADER_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<InvoiceApplyHeader> list = invoiceApplyHeaderService.selectList(pageRequest, invoiceApplyHeader);
        return Results.success(list);
    }
    @ApiOperation(value = "List task")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/task")
    public ResponseEntity<Page<InvoiceApplyHeader>> selectListTenant(InvoiceApplyHeader invoiceApplyHeader, @PathVariable Long organizationId, @ApiIgnore @SortDefault(value = InvoiceApplyHeader.FIELD_APPLY_HEADER_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<InvoiceApplyHeader> list = invoiceApplyHeaderService.selectList(pageRequest, invoiceApplyHeader);
        return Results.success(list);
    }

    //nomor empat buat munculin detail dengan line
    @ApiOperation(value = "Details")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{applyHeaderId}/detail")
    public ResponseEntity<InvoiceApplyHeader> detail(@PathVariable Long applyHeaderId) {
        InvoiceApplyHeader invoiceApplyHeader = invoiceApplyHeaderService.detailWithLine(applyHeaderId).getBody();
        return Results.success(invoiceApplyHeader);
    }

    //nomor lima insert update
    @ApiOperation(value = "Create or update")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping
    public ResponseEntity<List<InvoiceApplyHeader>> save(@PathVariable Long organizationId, @RequestBody List<InvoiceApplyHeader> invoiceApplyHeaders) {
        validObject(invoiceApplyHeaders);
        SecurityTokenHelper.validTokenIgnoreInsert(invoiceApplyHeaders);
        invoiceApplyHeaders.forEach(item -> item.setTenantId(organizationId));
        invoiceApplyHeaderService.saveData(invoiceApplyHeaders);
        return Results.success(invoiceApplyHeaders);
    }

    //nomor enamm delete redist
    @ApiOperation(value = "删除")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @DeleteMapping
    public ResponseEntity<InvoiceApplyHeader> remove(@RequestBody List<InvoiceApplyHeader> invoiceApplyHeaders) {
        SecurityTokenHelper.validToken(invoiceApplyHeaders);
        invoiceApplyHeaders.forEach(item -> item.setDelFlag(1));
        for(InvoiceApplyHeader header: invoiceApplyHeaders){
            invoiceApplyHeaderService.deleteRedisCache(header);
            invoiceApplyHeaderRepository.updateByPrimaryKeySelective(header);
        }
        return Results.success();
    }

    //nomor delapan
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @DeleteMapping("/{applyHeaderId}")
    public ResponseEntity<InvoiceApplyHeader> deleteById(@PathVariable Long applyHeaderId) {
        return invoiceApplyHeaderService.deleteById(applyHeaderId);
    }



    @ApiOperation(value = "Export")
    @GetMapping("/export")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ExcelExport(InvoiceApplyHeader.class)
    public ResponseEntity<List<InvoiceApplyHeader>> export(
            @RequestParam("exportType") String exportType,
            ExportParam exportParam, HttpServletResponse response, InvoiceApplyHeader invoiceApplyHeader, @PathVariable Long organizationId, @ApiIgnore @SortDefault(value = InvoiceApplyHeader.FIELD_APPLY_HEADER_ID,
            direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<InvoiceApplyHeader> page = invoiceApplyHeaderService.selectList(pageRequest, invoiceApplyHeader);
        return ResponseEntity.ok(page);
    }





}

