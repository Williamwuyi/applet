package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.railway.dao.ExchangeGroupMapper;
import com.jiebao.platfrom.railway.dao.ExchangeUserGroupMapper;
import com.jiebao.platfrom.railway.domain.ExchangeGroup;
import com.jiebao.platfrom.railway.domain.ExchangeUserGroup;
import com.jiebao.platfrom.railway.service.ExchangeGroupService;
import com.jiebao.platfrom.railway.service.ExchangeUserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service("ExchangeUserGroupService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExchangeUserGroupServiceImpl extends ServiceImpl<ExchangeUserGroupMapper, ExchangeUserGroup> implements ExchangeUserGroupService {


}
