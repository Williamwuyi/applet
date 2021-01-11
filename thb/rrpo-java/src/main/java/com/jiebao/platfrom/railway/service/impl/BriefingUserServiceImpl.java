package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.BriefingUserMapper;
import com.jiebao.platfrom.railway.dao.BriefingUserMapper;
import com.jiebao.platfrom.railway.domain.Briefing;
import com.jiebao.platfrom.railway.domain.BriefingUser;
import com.jiebao.platfrom.railway.domain.BriefingUser;
import com.jiebao.platfrom.railway.service.BriefingUserService;
import com.jiebao.platfrom.railway.service.BriefingUserService;
import com.jiebao.platfrom.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service("BriefingUserService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BriefingUserServiceImpl extends ServiceImpl<BriefingUserMapper, BriefingUser> implements BriefingUserService {

    @Autowired
    BriefingUserMapper briefingUserMapper;

    @Autowired
    UserService userService;

    @Override
    public boolean saveByUserId(String briefingId, String sendUserId, String sendUserName) {
        return briefingUserMapper.saveByUserId(briefingId, sendUserId, sendUserName);
    }

    @Override
    public boolean deleteByBriefingId(String briefingId) {
        return briefingUserMapper.deleteByBriefingId(briefingId);
    }

    @Override
    public boolean removeBySendUserId(String sendUserId, String briefingId) {
        return briefingUserMapper.removeBySendUserId(sendUserId, briefingId);
    }

    @Override
    public IPage<BriefingUser> getBriefingUserList(QueryRequest request, BriefingUser briefingUser) {
        QueryWrapper<BriefingUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BriefingUser::getBriefingId, briefingUser.getBriefingId());
        if (StringUtils.isNotBlank(briefingUser.getSendUserName())) {
            queryWrapper.lambda().like(BriefingUser::getSendUserName, briefingUser.getSendUserName());
        }
        if (briefingUser.getIsRead() != null) {
            queryWrapper.lambda().eq(BriefingUser::getIsRead, briefingUser.getIsRead());
        }
        Page<BriefingUser> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }


    @Override
    public boolean upDateBySendUserId(String sendUserId, String briefingId) {
        return briefingUserMapper.upDateBySendUserId(sendUserId,briefingId);
    }
}
