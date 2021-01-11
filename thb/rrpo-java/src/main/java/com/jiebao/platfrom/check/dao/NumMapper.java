package com.jiebao.platfrom.check.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.check.domain.Num;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-07-28
 */
public interface NumMapper extends BaseMapper<Num> {
    @Select("select * from check_num ${ew.customSqlSegment}")
    @Results({
            @Result(property = "dept", column = "dept_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById"))
    })
    IPage<Num> pageList(Page<Num> page, @Param("ew") QueryWrapper wrapper);

    @Select("select * from check_num where num_id=#{numId}")
    @Results({
            @Result(property = "dept", column = "dept_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById"))
    })
    Num selectByNumId(String numId);

    @Select("select * from check_num where year_id=#{yearId} and dept_id=#{deptId}")
    Num getNum(String yearId, String deptId);


    @Select("select 1 from check_num where year_id=#{yearId} and dept_id=#{deptId} limit 1")
    Integer exist(String yearId, String deptId);

    @Delete("delete from check_num ${ew.customSqlSegment}")
    boolean deleteByYearId(@Param("ew") LambdaQueryWrapper<Num> ew);



}
