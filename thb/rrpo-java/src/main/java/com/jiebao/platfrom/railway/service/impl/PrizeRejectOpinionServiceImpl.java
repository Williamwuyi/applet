package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.railway.dao.PrizeOpinionMapper;
import com.jiebao.platfrom.railway.dao.PrizeRejectOpinionMapper;
import com.jiebao.platfrom.railway.domain.PrizeOpinion;
import com.jiebao.platfrom.railway.domain.PrizeRejectOpinion;
import com.jiebao.platfrom.railway.service.PrizeOpinionService;
import com.jiebao.platfrom.railway.service.PrizeRejectOpinionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author yf
 */
@Slf4j
@Service("PrizeRejectOpinionService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PrizeRejectOpinionServiceImpl extends ServiceImpl<PrizeRejectOpinionMapper, PrizeRejectOpinion> implements PrizeRejectOpinionService {



}
