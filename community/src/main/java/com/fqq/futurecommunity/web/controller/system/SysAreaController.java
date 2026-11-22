package com.fqq.futurecommunity.web.controller.system;

import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.system.service.SysAreaService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fqq
 * @Date: 2026/3/10 - 03 - 10 - 23:57
 * @Description: com.fqq.futurecommunity.web.controller.system
 * @version: 1.0
 */

@RestController
@RequestMapping("/system/area")
public class SysAreaController extends BaseController {

        @Resource
        private SysAreaService sysAreaService;

        @RequestMapping("/tree")
        public BaseResponse getAreaTree(){

            return BaseResponse.success(sysAreaService.findAreaAsTree());
        }



}
