package com.jiebao.platfrom.common.GuideData.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.common.GuideData.domain.HlbInfo;
import com.jiebao.platfrom.common.GuideData.domain.InfoComment;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface HlbInfoMapper extends BaseMapper<HlbInfo> {

    @Select("SELECT * FROM hlb_info WHERE info_seq = #{infoSeq} ")
    List<HlbInfo> selectOnes(String infoSeq);

}