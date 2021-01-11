package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.railway.dao.PrizeOpinionMapper;
import com.jiebao.platfrom.railway.dao.PrizeTypeMapper;
import com.jiebao.platfrom.railway.domain.PrizeOpinion;
import com.jiebao.platfrom.railway.domain.PrizeType;
import com.jiebao.platfrom.railway.service.PrizeOpinionService;
import com.jiebao.platfrom.railway.service.PrizeTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author yf
 */
@Slf4j
@Service("PrizeOpinionService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PrizeOpinionServiceImpl extends ServiceImpl<PrizeOpinionMapper, PrizeOpinion> implements PrizeOpinionService {

    @Autowired
    private PrizeOpinionMapper prizeOpinionMapper;

    @Override
    public boolean saveByPrizeId(String prizeId, String opinionMoney,Integer rank) {
        return prizeOpinionMapper.saveByPrizeId(prizeId,opinionMoney,rank);
    }
}
