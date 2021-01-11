package com.jiebao.platfrom.check.dao;

import com.jiebao.platfrom.check.domain.Menus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-07-28
 */
@Resource
public interface MenusMapper extends BaseMapper<Menus> {
    @Select("select standard_id from check_menus where name=#{name}")
    String getMenusIdByName(String name);//通过name   也就是 content内容获得对应主键id

    @Select("select * from check_menus order by date asc")
    List<Menus> getMenus();

    @Select("select 1 from check_menus where name=#{name} limit 1")
    Integer countName(String name);

    @Select("select standard_id from check_menus where name=#{name} ")
    String  getId(String name);


}
