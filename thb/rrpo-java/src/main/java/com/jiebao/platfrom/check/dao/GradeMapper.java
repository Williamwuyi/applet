package com.jiebao.platfrom.check.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiebao.platfrom.check.domain.Grade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.check.domain.GradeZz;
import com.jiebao.platfrom.system.domain.File;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-07-28
 */
public interface GradeMapper extends BaseMapper<Grade> {
    @Select("select 1 from check_grade_zz where grade_id=#{gradeId} and status=1 limit 1")
    Integer gradeZzExistStatus(String gradeId);//通过gradeId  查询  此扣分项下面是否有   非自定义文件

    @Select("select 1 from check_grade_zz where grade_id=#{gradeId} and status!=NULL limit 1")
    Integer gradeZzExistStatusNull(String gradeId);//通过gradeId  查询  此扣分项下面是否有   非自定义文件

    @Update("update check_grade_zz set status=#{status} ${ew.customSqlSegment}")
    Integer updateGradeZZ(Integer status, QueryWrapper<GradeZz> ew); //条件 修改状态


    @Select("select 1 from sys_files where ref_id=#{gradeId} and zz_status=1 limit 1")
    Integer fileExistStatus(String gradeId);//通过gradeId  查询  此扣分项下面是否有查询的状态   自定义文件

    @Select("select 1 from sys_files where ref_id=#{gradeId} and zz_status!=NULL limit 1")
    Integer fileExistStatusNull(String gradeId);//通过gradeId  查询  此扣分项下面是否有查询的状态   自定义文件

    @Update("update sys_files set zz_status=#{status} ${ew.customSqlSegment}")
    Integer updateFile(Integer status, QueryWrapper<File> ew); //条件 修改状态


    @Select("select 1 from check_grade where year_id=#{yearId} and dept_id=#{deptId} and status is null limit 1")
    Integer gradeExIs(String yearId, String deptId);//通过gradeId  查询  此扣分项下面是否有查询的状态   自定义文件

    @Update("update check_num set audit=#{status} where year_id=#{yearId} and dept_id=#{deptId}")
    Integer updateNum(String yearId, String deptId, Integer status); //条件 修改状态


    @Select("select * from check_grade  ${ew.customSqlSegment}")
    @Results({
            @Result(property = "menusYear", column = "check_id", one = @One(select = "com.jiebao.platfrom.check.dao.MenusYearMapper.selectById"))
    })
    List<Grade> queryList(@Param("ew") QueryWrapper<Grade> ew);

    @Select("select 1 from check_grade where year_id=#{yearId} and dept_id=#{deptId} limit 1")
    Integer exists(String yearId, String deptId);  //是否产生 对应年份试题

    @Select("select 1 from check_grade where year_id=#{yearId} and dept_id=#{deptId} and check_id=#{menusId} limit 1")
    Integer exist(String yearId, String deptId, String menusId);  //试题中的具体某一题

    @Update("update check_grade set status=#{status} where grade_id=#{gradeId}")
    Integer updateStatus(Integer status, String gradeId); //条件 修改状态

    @Delete("delete from check_grade ${ew.customSqlSegment}")
    Integer deleteByCheckId(@Param("ew") QueryWrapper<Grade> ew);
}
