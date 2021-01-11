package com.jiebao.platfrom.check.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.check.domain.Year;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.check.domain.YearBindMenus;
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
public interface YearMapper extends BaseMapper<Year> {
    @Select("select * from check_year order by  year_date desc")
    List<Year> yearStringList();

    @Select("select * from check_year ${ew.customSqlSegment}")
//    @Results({
//            @Result(property = "jcNumber", column = "year_id", many = @Many(select = "com.jiebao.platfrom.check.dao.MenusYearMapper.jcNumber")),
//            @Result(property = "xgNumber", column = "year_id", many = @Many(select = "com.jiebao.platfrom.check.dao.MenusYearMapper.xgNumber")),
//    })
    IPage<Year> pageYear(Page<Year> page, QueryWrapper<Year> ew);


    @Select("select * from check_year ${ew1.customSqlSegment}")
    List<Year> list(@Param("ew1") QueryWrapper<Year> ew1);

    @Select("select menus_id from check_year_bind_menus where year_id=#{yearId}")
    List<String> listYB(@Param("yearId") String yearId);// 年度绑定考核项

    @Select("select year_id from check_year where year_date=#{yearDate}")
    String selectYearId(String yearDate);
}
