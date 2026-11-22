package com.fqq.futurecommunity.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fqq.futurecommunity.system.domain.SysDictData;
import com.fqq.futurecommunity.system.domain.SysDictType;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    Page<SysDictType> selectDictTypeList(Page<SysDictType> page, @Param("dictType") SysDictType dictType);

    List<SysDictType> selectDictTypeAll();

    SysDictType selectDictTypeById(Long dictId);

    SysDictType selectDictTypeByType(String dictType);

    int deleteDictTypeById(Long dictId);

    int deleteDictTypeByIds(Long[] dictIds);

    int insertDictType(SysDictType dictType);

    int updateDictType(SysDictType dictType);

    SysDictType checkDictTypeUnique(String dictType);
}
//public interface SysDictTypeMapper  extends BaseMapper<SysDictType> {
//
//    /**
//     * 根据条件分页查询字典类型
//     *
//     * @param dictType 字典类型信息
//     * @return 字典类型集合信息
//     */
//    public List<SysDictType> selectDictTypeList(SysDictType dictType);
//
//    /**
//     * 查询所有字典类型
//     *
//     * @return 字典类型集合信息
//     */
//    public List<SysDictType> selectDictTypeAll();
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
//     * 通过字典ID删除字典信息
//     *
//     * @param dictId 字典ID
//     * @return 结果
//     */
//    public int deleteDictTypeById(Long dictId);
//
//    /**
//     * 批量删除字典类型信息
//     *
//     * @param dictIds 需要删除的字典ID
//     * @return 结果
//     */
//    public int deleteDictTypeByIds(Long[] dictIds);
//
//    /**
//     * 新增字典类型信息
//     * @param dictType 字典类型信息
//     * @return 结果
//     */
//    public int insertDictType(SysDictType dictType);
//
//    /**
//     * 修改字典类型信息
//     *
//     * @param dictType 字典类型信息
//     * @return 结果
//     */
//    public int updateDictType(SysDictType dictType);
//
//
//    /**
//     * 校验字典类型是否唯一
//     * @param dictType  字典类型
//     * @return: com.fqq.futurecommunity.system.domain.SysDictType
//     */
//    public SysDictType checkDictTypeUnique(String dictType);
//
//}
