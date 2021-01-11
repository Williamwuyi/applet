package com.jiebao.platfrom.wx.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.wx.domain.DeptLine;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-10-21
 */
public interface DeptLineMapper extends BaseMapper<DeptLine> {

    @Select("select dept_line_id,dept_id,line_id,(select field_name from sys_dict_new where dict_id=line_id) as lineName," +
            "(select nature from sys_dict_new where dict_id=line_id) as nature," +
            "(select revenue from sys_dict_new where dict_id=line_id) as revenue" +
            " from wx_dept_line ${ew.customSqlSegment} ")
    List<DeptLine> queryList(@Param("ew") QueryWrapper<DeptLine> queryWrapper);

}
