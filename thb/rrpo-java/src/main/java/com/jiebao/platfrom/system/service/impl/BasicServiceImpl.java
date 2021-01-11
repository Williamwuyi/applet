package com.jiebao.platfrom.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.system.dao.BasicMapper;
import com.jiebao.platfrom.system.domain.Basic;
import com.jiebao.platfrom.system.service.BasicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("basicService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BasicServiceImpl extends ServiceImpl<BasicMapper, Basic> implements BasicService {
}
