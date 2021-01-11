package com.jiebao.platfrom.common.dataImport.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.common.dataImport.domain.Info;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InfoMapper extends BaseMapper<Info> {

    @Select("SELECT * FROM `hlb_info` WHERE upTime BETWEEN '2020-01-1' AND '2020-10-14' ")
    List<Info> listForYear();
}
