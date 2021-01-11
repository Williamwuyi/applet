package com.jiebao.platfrom.common.GuideData.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.GuideData.domain.InfoComment;
import com.jiebao.platfrom.common.GuideData.domain.Unituser;

import java.util.List;

public interface InfoCommentService extends IService<InfoComment> {

    List<InfoComment> selectId(String infoSeq ,String title);

}
