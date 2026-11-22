package com.fqq.futurecommunity.system.service;

import com.fqq.futurecommunity.system.domain.SysDictData;
import com.fqq.futurecommunity.system.domain.SysDictType;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface SysDictTypeService {

    Page<SysDictType> selectDictTypeList(Page<SysDictType> page, SysDictType dictType);

    List<SysDictType> selectDictTypeAll();

    List<SysDictData> selectDictDataByType(String dictType);

    SysDictType selectDictTypeById(Long dictId);

    SysDictType selectDictTypeByType(String dictType);

    int deleteDictTypeByIds(Long[] dictIds);

    int insertDictType(SysDictType dictType);

    int updateDictType(SysDictType dictType);

    String checkDictTypeUnique(SysDictType dictType);

    void clearCache();
}
//
//public interface SysDictTypeService {
//    /**
//     * 根据条件分页查询字典类型
//     *
//     * @param dictType 字典类型信息
//     * @return 字典类型集合信息
//     */
//    public List<SysDictType> selectDictTypeList(SysDictType dictType);
//
//    /**
//     * 根据所有字典类型
//     *
//     * @return 字典类型集合信息
//     */
//    public List<SysDictType> selectDictTypeAll();
//
//    /**
//     * 根据字典类型查询字典数据
//     *
//     * @param dictType 字典类型
//     * @return 字典数据集合信息
//     */
//    public List<SysDictData> selectDictDataByType(String dictType);
//
//    /**
//     * 根据字典类型ID查询信息
//     *
//     * @param dictId 字典类型ID
//     * @return 字典类型
//     */
//    public SysDictType selectDictTypeById(Long dictId);
//
//    /**
//     * 根据字典类型查询信息
//     *
//     * @param dictType 字典类型
//     * @return 字典类型
//     */
//    public SysDictType selectDictTypeByType(String dictType);
//
//    /**
//     * 批量删除字典信息
//     *
//     * @param dictIds 需要删除的字典ID
//     * @return 结果
//     */
//    public int deleteDictTypeByIds(Long[] dictIds);
//
//    /**
//     * 新增保存字典类型信息
//     *
//     * @param dictType 字典类型信息
//     * @return 结果
//     */
//    public int insertDictType(SysDictType dictType);
//
//    /**
//     * 修改保存字典类型信息
//     *
//     * @param dictType 字典类型信息
//     * @return 结果
//     */
//    public int updateDictType(SysDictType dictType);
//
//    /**
//     * 校验字典类型是否唯一
//     * @param dictType  字典类型
//     * @return: com.fqq.futurecommunity.system.domain.SysDictType
//     */
//    public String checkDictTypeUnique(SysDictType dictType);
//
//
//    /**
//     * 清空缓存
//     */
//    public void clearCache();
//}
