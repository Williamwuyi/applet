package com.jiebao.platfrom.accident.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.accident.daomain.CountTable;
import com.jiebao.platfrom.accident.daomain.Jk;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qta
 * @since 2020-11-07
 */
public interface JkMapper extends BaseMapper<Jk> {
    @Select(" select ID as idF,ID,PK as pk,gac,dzs,xsq,xzjd,xlmc,tllc,azfw,tljl,dlmc,dllc,azbw,sl,jkqlx,jkfw,jksjzl,jkysgn,jkgd,blts,lwdw,jsdw,azsjn,azsj,gldw,sydw,pcs,sdpcs,bz,delflag,username,datalock,creat_time from accident_jk ${ew.customSqlSegment}")
    @Results({
            @Result(property = "tpFile", column = "idF", one = @One(select = "com.jiebao.platfrom.system.dao.FileMapper.fileList"))
    })
    IPage<Jk> pageList(Page<Jk> page, @Param("ew") LambdaQueryWrapper<Jk> ew);

    /**
     * columnName==>数据库名   deptName==>参数名
     *
     * @param columnName
     * @param deptName
     * @return
     */
    //
    @Select("<script>select ${columnName} as name,count(1) as count from accident_jk   " +
            "<where> 1=1 " +
            "<if test=\"deptName != null\"> and gac=#{deptName}</if>" +
            "and ${columnName} is not null " +
            "and ${columnName} != '' " +
            "</where>" +
            "GROUP BY ${columnName}" +
            "</script>")
    List<CountTable> countTable(String columnName, @Param("deptName") String deptName);

    @Select("<script>select gac as name,count(1) as count from accident_jk where gac=#{deptName} <if test='year!=null'> and azsjn=#{year} </if>  <if test='xlmc!=null'> and xlmc=#{xlmc}</if></script>")
    CountTable ByDeptNameGa(@Param("deptName")String deptName,@Param("year") String year,String xlmc);

    @Select("<script>select dzs as name,count(1) as count from accident_jk where dzs=#{deptName}  <if test='year!=null'> and azsjn=#{year} </if>  <if test='xlmc!=null'> and xlmc=#{xlmc}</if></script>")
    CountTable ByDeptNameDzs(@Param("deptName") String deptName,@Param("year")String year,String xlmc);


    @Select("<script>select xsq as name,count(1) as count from accident_jk where xsq=#{deptName}  <if test='year!=null'> and azsjn=#{year} </if>  <if test='xlmc!=null'> and xlmc=#{xlmc}</if></script>")
    CountTable ByDeptNameXq(@Param("deptName") String deptName,@Param("year")String year,String xlmc);

}
