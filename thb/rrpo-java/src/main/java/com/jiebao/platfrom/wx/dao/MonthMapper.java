package com.jiebao.platfrom.wx.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.wx.domain.Month;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.wx.domain.MonthMap;
import com.jiebao.platfrom.wx.domain.Qun;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-08-22
 */
public interface MonthMapper extends BaseMapper<Month> {
    @Select("select count(*) from  wx_month where month=#{month} and sh_dept_id=#{deptId} and status=1")
    Integer count(String month, String deptId);   //查看  此市级部门 上传的阅读  评选有几条

    @Select("select * from wx_month ${ew.customSqlSegment}")
    @Results({
            @Result(property = "qun", column = "qun_id", one = @One(select = "com.jiebao.platfrom.wx.dao.QunMapper.selectById")),
            @Result(property = "deptJc", column = "jc_dept_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "deptSh", column = "sh_dept_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "user", column = "user_id", one = @One(select = "com.jiebao.platfrom.system.dao.UserMapper.selectById")),
    })
    IPage<Month> list(Page<Month> page, QueryWrapper<Month> ew);


    @Select("select * from wx_month ${ew.customSqlSegment}")
    @Results({
            @Result(property = "qun", column = "qun_id", one = @One(select = "com.jiebao.platfrom.wx.dao.QunMapper.selectById")),
            @Result(property = "deptJc", column = "jc_dept_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "deptSh", column = "sh_dept_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById")),
            @Result(property = "user", column = "user_id", one = @One(select = "com.jiebao.platfrom.system.dao.UserMapper.selectById")),
    })
    List<Month> listWord(@Param("ew") QueryWrapper<Month> ew);  //导出word专用

    @Select("select sz_dept_name as deptName,count(1) as number from wx_month ${ew.customSqlSegment}")
    List<MonthMap> selectMap(@Param("ew") QueryWrapper<Month> ew);
}
