package com.jiebao.platfrom.accident.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.accident.daomain.Dept;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-09-29
 */
public interface DeptSMapper extends BaseMapper<Dept> {
    @Select("select * from accident_dept ${ew.customSqlsegment}")
    @Results({
            @Result(property = "dept", column = "dept_id", one = @One(select = "com.jiebao.platfrom.system.dao.DeptMapper.selectById"))
    })
    List<Dept> listDept(@Param("ew") QueryWrapper<Dept> queryWrapper);


    @Select("select dept_id from accident_dept where id=#{id}")
    String selectDeptId(String id); //获得组织机构对应的真实  id


    @Select("select dept_id from accident_dept where parent_id=#{parentId}")
    List<String> selectDeptIds(String parentId);  //通过父类获得

    @Select(" select id from accident_dept where dept_id=#{deptId} limit 1")
    String selectIdByDeptId(String deptID);
}
