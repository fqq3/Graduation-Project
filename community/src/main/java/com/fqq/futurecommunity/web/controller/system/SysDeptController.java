package com.fqq.futurecommunity.web.controller.system;

import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.system.domain.SysDept;
import com.fqq.futurecommunity.system.service.SysDeptService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fqq
 * @Date: 2026/3/30 - 03 - 30 - 3:20
 * @Description: com.fqq.futurecommunity.web.controller.system
 * @version: 1.0
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    @Resource
    private SysDeptService deptService;


    /**
     * 获取部门列表
     * @param sysDept
     * @return: com.fqq.commuinty.common.core.domain.BaseResponse
     */
    @GetMapping("/list")
    public BaseResponse list(SysDept sysDept){

        List<SysDept> sysDepts = deptService.selectDeptList(sysDept);
        return BaseResponse.success(sysDepts);
    }

}

