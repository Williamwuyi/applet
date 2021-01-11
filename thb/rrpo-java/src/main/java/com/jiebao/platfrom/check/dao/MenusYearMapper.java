package com.jiebao.platfrom.check.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiebao.platfrom.check.domain.MenusYear;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.check.domain.Year;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
public interface MenusYearMapper extends BaseMapper<MenusYear> {
    @Select("select menus_year_id from check_menus_year ${ew.customSqlSegment} ")
    List<String> getMenusIdList(@Param("ew") LambdaQueryWrapper<MenusYear> queryWrapper);


    @Select("select count(*) from check_menus_year where year_id=#{yearId} and parent_id=(select standard_id from check_menus where content='基础工作')")
    Integer jcNumber(String yearId);

    @Select("select count(*) from check_menus_year where year_id=#{yearId} and parent_id=(select standard_id from check_menus where content='工作效果')")
    Integer xgNumber(String yearId);


    @Select("select count(*) from check_menus_year where year_id=#{yearId} and parent_id=#{parentId}")
    Integer countNumber(String yearId, String parentId);

    @Select("select 1 from check_menus_year where content=#{content} and year_id=#{yearId} limit 1")
    Integer exSit(String content, String yearId);



}
