package com.fqq.futurecommunity.web.controller.system;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.system.domain.SysDictData;
import com.fqq.futurecommunity.system.service.SysDictDataService;
import com.fqq.futurecommunity.system.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

/**
 * 数据字典信息
 * @author spikeCong
 * @date 2023/5/22
 **/
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {

    @Autowired
    private SysDictDataService dictDataService;

    @Autowired
    private SysDictTypeService dictTypeService;

    /**
     * 查询字典数据列表
     */
    @RequestMapping("/list")
    public PageResult list(SysDictData sysDictData){
        Page<SysDictData> page = startPage();
        Page<SysDictData> resultPage =
                dictDataService.selectDictDataPage(page, sysDictData);

        return getData(resultPage);
    }

    /**
     * 根据Id查询字典详细信息
     */
    @GetMapping(value = "/{dictCode}")
    public BaseResponse getInfo(@PathVariable Long dictCode){

        return BaseResponse.success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public BaseResponse getInfo(@PathVariable String dictType){

        return BaseResponse.success(dictTypeService.selectDictDataByType(dictType));
    }

    /**
     * 新增字典数据
     */
    @PostMapping
    public BaseResponse add(@RequestBody SysDictData sysDictData){
        sysDictData.setCreateBy(SecurityUtils.getUserName());
        return toAjax(dictDataService.insertDictData(sysDictData));
    }

    /**
     * 修改字典数据
     */
    @PutMapping
    public BaseResponse edit(@RequestBody SysDictData sysDictData){
        sysDictData.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(dictDataService.updateDictData(sysDictData));
    }

    /**
     *删除字典数据
     */
    @DeleteMapping("/{dictCodes}")
    public BaseResponse remove(@PathVariable Long[] dictCodes){

        return toAjax(dictDataService.deleteDictDataByIds(dictCodes));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('system:dict:export')")
    public ChainedMap export(SysDictData dictData) {
        Page<SysDictData> page = new Page<>(1, 100000);
        List<SysDictData> dictDataList = dictDataService.selectDictDataPage(page, dictData).getRecords();
        String fileName = FileStorageUtils.generateFileName("dict_data", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(dictDataList, SysDictData.class, filePath, "DictData");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }

}
