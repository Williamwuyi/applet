package com.jiebao.platfrom.common.GuideData.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.GuideData.dao.HlbInfoMapper;
import com.jiebao.platfrom.common.GuideData.dao.InfoCommentMapper;
import com.jiebao.platfrom.common.GuideData.domain.HlbInfo;
import com.jiebao.platfrom.common.GuideData.domain.InfoComment;
import com.jiebao.platfrom.common.GuideData.service.HlbInfoService;
import com.jiebao.platfrom.common.GuideData.service.InfoCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("HlbInfoService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@DS("old")
public class HlbInfoServiceImpl extends ServiceImpl<HlbInfoMapper, HlbInfo> implements HlbInfoService {

    @Autowired
    private HlbInfoMapper hlbInfoMapper;

    @Override
    public List<HlbInfo>  selectOnes(String infoSeq) {
        return hlbInfoMapper.selectOnes(infoSeq);
    }
}
