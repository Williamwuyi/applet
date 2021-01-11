package com.jiebao.platfrom.common.GuideData.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.common.GuideData.domain.InfoComment;
import com.jiebao.platfrom.common.GuideData.domain.Unituser;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface InfoCommentMapper extends BaseMapper<InfoComment> {

    @Select("SELECT * FROM `hlb_info_comment` where  title= #{title}   and info_seq =#{infoSeq}")
    List<InfoComment> selectId(String infoSeq , String title);

}