package com.jiebao.platfrom.common.GuideData.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.common.GuideData.domain.Unituser;
import com.jiebao.platfrom.common.dataImport.domain.Info;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface UnituserMapper extends BaseMapper<Unituser> {

    @Select("SELECT * FROM `unituser` WHERE unit =\"省护路办\"")
   List<Unituser> selectNew();

    @Select("SELECT * from unituser WHERE unit_user = #{userName}  limit 0,1 ")
    Unituser selectName(String userName);

    /**
     * 长铁用户
     * @return
     */
    @Select("SELECT * FROM `unituser` WHERE pcode BETWEEN 11601 AND 11632 ")
    List<Unituser> selectCT();


    /**
     * 衡铁用户
     * @return
     */
    @Select("SELECT * FROM `unituser` WHERE pcode BETWEEN 11701 AND 11725 ")
    List<Unituser> selectHT();


    /**
     * 怀化铁路用户
     * @return
     */
    @Select("SELECT * FROM `unituser` WHERE pcode BETWEEN 11801 AND 11826 ")
    List<Unituser> selectHHT();


}