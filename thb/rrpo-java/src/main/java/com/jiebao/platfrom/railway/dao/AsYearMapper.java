package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.AsYear;
import com.jiebao.platfrom.system.domain.File;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author yf
 */
public interface AsYearMapper extends BaseMapper<AsYear> {

    @Select("select 1 from rail_as_year where grade_id=#{gradeId} and file_id=#{fileId} limit 1")
    Integer exist(String gradeId, String fileId);

    @Select("select * from sys_files ${ew.customSqlSegment}")
    List<File> getFile(@Param("ew") QueryWrapper<File> ew);

    @Select("select id,file_id from rail_as_year where grade_id=#{gradeId}")
    @Results(
            @Result(property = "file", column = "file_id", one = @One(select = "com.jiebao.platfrom.system.dao.FileMapper.selectById"))
    )
    List<AsYear> getFiles(@Param("gradeId") String gradeId);

    @Select("select file_id from rail_as_year where dept_id=#{deptId} and year_id=#{yearId}")
    List<String> getFileSByYear(String yearId, String deptId);

}
