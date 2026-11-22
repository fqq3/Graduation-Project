package com.fqq.futurecommunity.web.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.UserConstants;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.system.domain.SysDictType;
import com.fqq.futurecommunity.system.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

/**
 * @author spikeCong
 * @date 2023/5/22
 **/
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    /**
     * 多条件分页查询字典类型数据
     */
    @GetMapping("/list")
    public PageResult list(SysDictType dictType) {
        Page<SysDictType> page = startPage();
        Page<SysDictType> result = sysDictTypeService.selectDictTypeList(page, dictType);
        return getData(result);
    }

    /**
     * 根据Id查询字典类型详细信息
     */
    @GetMapping(value = "/{dictId}")
    public BaseResponse getInfo(@PathVariable Long dictId){
        return BaseResponse.success(sysDictTypeService.selectDictTypeById(dictId));
    }

/*
     * 新增字典类型
     */
    @PostMapping
    public BaseResponse add(@RequestBody SysDictType sysDictType){

        if(UserConstants.NOT_UNIQUE.equals(sysDictTypeService.checkDictTypeUnique(sysDictType))){
            return BaseResponse.fail("新增字典" + sysDictType.getDictName() + "失败,字典类型已经存在");
        }
        sysDictType.setCreateBy(SecurityUtils.getUserName());
        return toAjax(sysDictTypeService.insertDictType(sysDictType));
    }

    /*
     * 修改字典类型
     */
    @PutMapping
    public BaseResponse edit(@RequestBody SysDictType sysDictType){
        if(UserConstants.NOT_UNIQUE.equals(sysDictTypeService.checkDictTypeUnique(sysDictType))){
            return BaseResponse.fail("修改字典" + sysDictType.getDictName() + "失败,字典类型已经存在");
        }
        sysDictType.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(sysDictTypeService.updateDictType(sysDictType));
    }

    /*
     * 删除字典类型
     */
    @DeleteMapping("/{dictIds}")
    public BaseResponse remove(@PathVariable Long[] dictIds){
        return toAjax(sysDictTypeService.deleteDictTypeByIds(dictIds));
    }

    /**
     * 清空缓存
     */
    @DeleteMapping("/clearCache")
    public BaseResponse clearCache(){
        sysDictTypeService.clearCache();
        return BaseResponse.success("清除缓存成功");
    }
    @GetMapping("/optionselect")
    public BaseResponse optionselect() {
        return BaseResponse.success(sysDictTypeService.selectDictTypeAll());
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('system:dict:export')")
    public ChainedMap export(SysDictType dictType) {
        Page<SysDictType> page = new Page<>(1, 100000);
        List<SysDictType> dictTypeList = sysDictTypeService.selectDictTypeList(page, dictType).getRecords();
        String fileName = FileStorageUtils.generateFileName("dict_type", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(dictTypeList, SysDictType.class, filePath, "DictType");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
