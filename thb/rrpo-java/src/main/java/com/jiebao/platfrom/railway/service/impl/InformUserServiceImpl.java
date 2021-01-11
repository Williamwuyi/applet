package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.railway.dao.ExchangeUserMapper;
import com.jiebao.platfrom.railway.dao.InformUserMapper;
import com.jiebao.platfrom.railway.domain.ExchangeUser;
import com.jiebao.platfrom.railway.domain.InformUser;
import com.jiebao.platfrom.railway.service.ExchangeUserService;
import com.jiebao.platfrom.railway.service.InformUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service("InformUserService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class InformUserServiceImpl extends ServiceImpl<InformUserMapper, InformUser> implements InformUserService {

    @Autowired
    InformUserMapper informUserMapper;

    @Override
    public boolean removeBySendUserId(String sendUserId, String informId) {
        return informUserMapper.removeBySendUserId(sendUserId,informId);
    }
}
