package com.hand.demo.api.controller.v1;

import com.hand.demo.api.dto.PrefixDTO;
import com.hand.demo.app.service.ExampleService;
import com.hand.demo.domain.repository.ExampleRepository;
import io.swagger.annotations.*;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import com.hand.demo.config.SwaggerTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import java.util.List;

/**
 * API接口
 */
@Api(tags = SwaggerTags.EXAMPLE)
@RestController("exampleController.v1")
@RequestMapping("/v1/example")
public class ExampleController extends BaseController {
    @Autowired
    private ExampleService exampleService;

    @ApiOperation(value = "Upload Config")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", paramType = "path")
    })
    @GetMapping("/upload-config")
    public ResponseEntity<List<PrefixDTO>> uploadconfig(PrefixDTO prefixDTO) {
        return Results.success(exampleService.selectList(prefixDTO));
    }


//    @Autowired
//    private ExampleService exampleService;
//    @Autowired
//    private ExampleRepository exampleRepository;
//    @Autowired
//    private RedisQueueHelper redisQueueHelper;
//    @Autowired
//    private Util util;
//
//    @ApiOperation(value = "根据ID获取")
//    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "ID", paramType = "path")
//    })
//    @GetMapping("/{id}")
//    public ResponseEntity<Example> hello(@PathVariable Long id) {
////        return Results.success(exampleRepository.selectByPrimaryKey(id));
//        List<String> massageList = new ArrayList<String>();
//        InvoiceInfoQueue infoQueue = new InvoiceInfoQueue();
//        infoQueue.setTenantId(0L);
//        infoQueue.setContent("test");
//        infoQueue.setEmployeeId("48208");
//        massageList.add(JSON.toJSONString(infoQueue));
//        redisQueueHelper.pushAll("HEXAM-INV-HEADER-QUEUE", massageList);
//        return Results.success();
//    }



}


