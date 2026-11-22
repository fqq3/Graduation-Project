package com.fqq.futurecommunity.web.controller.system;

import com.fqq.futurecommunity.common.constant.UserConstants;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.domain.TreeSelect;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.system.domain.SysDept;
import com.fqq.futurecommunity.system.service.SysDeptService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    @Resource
    private SysDeptService deptService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('system:dept:list')")
    public BaseResponse findDeptList(SysDept sysDept){
        List<SysDept> sysDepts = deptService.selectDeptList(sysDept);
        return BaseResponse.success(sysDepts);
    }

    /**
     * 根据部门编号获取详细信息
     * @param deptId
     * @return: com.fqq.futurecommunity.common.core.domain.BaseResponse
     */
    @GetMapping(value = "/{deptId}")
    public BaseResponse getInfo(@PathVariable Long deptId){

        return BaseResponse.success(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     * @param sysDept
     * @return: com.fqq.futurecommunity.common.core.domain.BaseResponse
     */
    @PostMapping
    public BaseResponse add(@RequestBody  SysDept sysDept){
        if(UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(sysDept))){
            return BaseResponse.fail("新增部门" + sysDept.getDeptName() + "失败,部门名称已经存在");
        }
        sysDept.setCreateBy(SecurityUtils.getUserName());
        return toAjax(deptService.insertDept(sysDept));
    }

    /**
     * 修改部门
     */
    @PutMapping
    public BaseResponse edit(@RequestBody SysDept sysDept){
        if(UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(sysDept))){
            return BaseResponse.fail("修改部门" + sysDept.getDeptName() + "失败,部门名称已经存在");
        }
        else if(sysDept.getParentId().equals(sysDept.getDeptId())){
            return BaseResponse.fail("修改部门" + sysDept.getDeptName() + "失败,上级部门不能是自己");
        }

        sysDept.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(deptService.updateDept(sysDept));
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    public BaseResponse remove(@PathVariable Long deptId){
        if(deptService.hasChildByDeptId(deptId)){
            return BaseResponse.fail("存在下级部门,不允许删除");
        }
        if(deptService.checkDeptExistUser(deptId)){
            return BaseResponse.fail("部门存在用户,不允许删除");
        }

        return toAjax(deptService.deleteDeptById(deptId));
    }

    /**
     * 获取部门下拉列表
     */
    @GetMapping("/treeselect")
    public BaseResponse treeSelect(SysDept sysDept){
        List<SysDept> deptList = deptService.selectDeptList(sysDept);
        List<TreeSelect> treeSelects = deptService.buildDeptTreeSelect(deptList);
        return BaseResponse.success(treeSelects);
    }

    /**
     * 查询部门列表 (排除当前节点)
     * @param
     * @return: com.fqq.futurecommunity.common.core.domain.BaseResponse
     */
    @GetMapping("/list/exclude/{deptId}")
    public BaseResponse excludeChild(@PathVariable Long deptId){

        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Iterator<SysDept> iterator = deptList.iterator();
        while (iterator.hasNext()){
            SysDept sysDept = (SysDept) iterator.next();
            if(sysDept.getDeptId().intValue() == deptId.intValue()
                || ArrayUtils.contains(StringUtils.split(sysDept.getAncestors(),","),deptId + "")){

                iterator.remove();
            }
        }

        return BaseResponse.success(deptList);
    }

}
