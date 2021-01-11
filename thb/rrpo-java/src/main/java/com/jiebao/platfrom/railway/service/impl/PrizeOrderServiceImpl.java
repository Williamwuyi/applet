package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.railway.dao.PrizeOrderMapper;
import com.jiebao.platfrom.railway.dao.PrizeTypeMapper;
import com.jiebao.platfrom.railway.domain.Prize;
import com.jiebao.platfrom.railway.domain.PrizeOrder;
import com.jiebao.platfrom.railway.domain.PrizeType;
import com.jiebao.platfrom.railway.service.PrizeOrderService;
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
@Service("PrizeOrderService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PrizeOrderServiceImpl extends ServiceImpl<PrizeOrderMapper, PrizeOrder> implements PrizeOrderService {

    @Autowired
    PrizeOrderMapper prizeOrderMapper;

    @Override
    public PrizeOrder selectRecent(String deptId) {
        return prizeOrderMapper.selectRecent(deptId);
    }

    @Override
    public Integer getCountByDept(String deptId) {
        return prizeOrderMapper.getCountByDept(deptId);
    }
}
