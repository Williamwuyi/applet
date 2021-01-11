package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.railway.dao.ExchangeGroupMapper;
import com.jiebao.platfrom.railway.dao.PrizeIdentityMapper;
import com.jiebao.platfrom.railway.domain.ExchangeGroup;
import com.jiebao.platfrom.railway.domain.PrizeIdentity;
import com.jiebao.platfrom.railway.service.ExchangeGroupService;
import com.jiebao.platfrom.railway.service.PrizeIdentityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;


@Slf4j
@Service("PrizeIdentityService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PrizeIdentityServiceImpl extends ServiceImpl<PrizeIdentityMapper, PrizeIdentity> implements PrizeIdentityService {



}
