package com.jiebao.platfrom.accident.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.accident.daomain.Case;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
public interface CaseMapper extends BaseMapper<Case> {
    @Select("select * from accident_case ${ew.customSqlSegment}")
    @Results({
            @Result(property = "deptCs", column = "city_cs_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "deptQx", column = "city_qx_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "deptXc", column = "city_xc", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "deptPolice", column = "police_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "dictXl" ,column = "line_id",one = @One(select = "com.jiebao.platfrom.system.dao.DictMapper.selectById"))
    })
    IPage<Case> ListPage(Page<Case> page, @Param("ew") QueryWrapper<Case> queryWrapper);


    @Select("select nature from accident_case group by nature")
    List<String> sgXz();  //事故性质类别

    @Select("select count(*) from accident_case ${ew.customSqlSegment}")
    Integer count(@Param("ew") QueryWrapper<Case> ew);  //数量




}
