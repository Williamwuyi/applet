package com.jiebao.platfrom.check.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.check.domain.GradeZz;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.Inform;
import com.jiebao.platfrom.railway.domain.Prize;
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
 * @since 2020-08-11
 */
public interface GradeZzMapper extends BaseMapper<GradeZz> {
    @Select("select zz_id from check_grade_zz where type=#{type} and grade_id=#{gradeId}")
    List<String> getZzId(Integer type, String gradeId);

    @Update("update check_grade_zz set status=#{status} ${ew.customSqlSegment}")
    int updateByGrade(Integer status, @Param("ew") QueryWrapper<GradeZz> ew);


    @Select("select * from check_grade_zz where grade_id=#{gradeId} and type=#{type}")
    @Results({
            @Result(property = "exchange", column = "zz_id", one = @One(select = "com.jiebao.platfrom.railway.dao.ExchangeMapper.selectById"))
    })
    List<GradeZz> ListXXHD(String gradeId, Integer type, Page<Exchange> page);  //信息互递

    @Select("select * from check_grade_zz where grade_id=#{gradeId} and type=#{type}}")
    @Results({
            @Result(property = "prize", column = "zz_id", one = @One(select = "com.jiebao.platfrom.railway.dao.PrizeMapper.selectById"))
    })
    List<GradeZz> ListYSYJ(String gradeID, Integer type, Page<Prize> page); //一事一奖


    @Select("select * from check_grade_zz where grade_id=#{gradeId} and type=#{type}")
    @Results({
            @Result(property = "inform", column = "zz_id", one = @One(select = "com.jiebao.platfrom.railway.dao.InformMapper.selectById"))
    })
    List<GradeZz> ListTZGG(String gradeID, Integer type, Page<Inform> page);  //通知公告

    @Select("select * from check_grade_zz where grade_id=#{gradeId} and type=#{type}")
    @Results({
            @Result(property = "file", column = "zz_id", one = @One(select = "com.jiebao.platfrom.system.dao.FileMapper.selectById"))
    })
    List<GradeZz> ListGGXX(String gradeID, Integer type, Page<File> page);  //公共信息


    @Select("select 1 from check_grade_zz where grade_id=#{gradeId} and zz_id=#{zzId} limit 1")
    Integer ExIstGradeZz(String gradeId,String zzId);
}
