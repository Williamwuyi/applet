package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.BriefingCountMapper;
import com.jiebao.platfrom.railway.dao.BriefingUserMapper;
import com.jiebao.platfrom.railway.domain.BriefingCount;
import com.jiebao.platfrom.railway.domain.BriefingUser;
import com.jiebao.platfrom.railway.service.BriefingCountService;
import com.jiebao.platfrom.railway.service.BriefingUserService;
import com.jiebao.platfrom.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service("BriefingCountService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BriefingCountServiceImpl extends ServiceImpl<BriefingCountMapper, BriefingCount> implements BriefingCountService {

}
