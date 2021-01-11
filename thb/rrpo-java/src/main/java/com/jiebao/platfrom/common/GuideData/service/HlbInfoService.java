package com.jiebao.platfrom.common.GuideData.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.GuideData.domain.HlbInfo;
import com.jiebao.platfrom.common.GuideData.domain.InfoComment;

import java.util.List;

public interface HlbInfoService extends IService<HlbInfo> {

    List<HlbInfo>  selectOnes(String infoSeq);

}
