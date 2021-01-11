package com.jiebao.platfrom.check.dao;

import com.jiebao.platfrom.check.domain.YearBindMenus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-09-03
 */
public interface YearBindMenusMapper extends BaseMapper<YearBindMenus> {
    @Select("select 1 from check_year_bind_menus where menus_id=#{mid} and year_id=#{yearId} limit 1")
    Integer exist(String yearId, String mid);  //查询  此年度 跟  模块是否绑定

    @Select("select menus_id from check_year_bind_menus where  year_id=#{yearId} ")
    List<String> listMenusId(String yearId);

    @Select("select 1 from check_year_bind_menus where  menus_id=#{mid} limit 1")
    Integer existByMenusId(String mid);  //查看某模块是否有绑定


}
