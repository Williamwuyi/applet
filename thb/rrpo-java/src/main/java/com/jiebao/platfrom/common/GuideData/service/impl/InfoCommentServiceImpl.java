package com.jiebao.platfrom.common.GuideData.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.GuideData.dao.InfoCommentMapper;
import com.jiebao.platfrom.common.GuideData.dao.UnituserMapper;
import com.jiebao.platfrom.common.GuideData.domain.InfoComment;
import com.jiebao.platfrom.common.GuideData.domain.Unituser;
import com.jiebao.platfrom.common.GuideData.service.InfoCommentService;
import com.jiebao.platfrom.common.GuideData.service.UnitUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("InfoCommentService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@DS("old")
public class InfoCommentServiceImpl extends ServiceImpl<InfoCommentMapper, InfoComment> implements InfoCommentService {

    @Autowired
    private InfoCommentMapper infoCommentMapper;


    @Override
    public List<InfoComment> selectId(String infoSeq, String title) {
        return infoCommentMapper.selectId(infoSeq, title);
    }
}
