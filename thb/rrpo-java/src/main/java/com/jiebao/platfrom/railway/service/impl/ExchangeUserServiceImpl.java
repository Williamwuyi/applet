package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.ExchangeMapper;
import com.jiebao.platfrom.railway.dao.ExchangeUserMapper;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.ExchangeUser;
import com.jiebao.platfrom.railway.service.ExchangeService;
import com.jiebao.platfrom.railway.service.ExchangeUserService;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.UserService;
import com.mchange.lang.IntegerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service("ExchangeUserService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExchangeUserServiceImpl extends ServiceImpl<ExchangeUserMapper, ExchangeUser> implements ExchangeUserService {

    @Autowired
    ExchangeUserMapper exchangeUserMapper;

    @Autowired
    UserService userService;

    @Override
    public boolean saveByUserId(String exchangeId, String sendUserId, String sendUserName) {
        return exchangeUserMapper.saveByUserId(exchangeId, sendUserId, sendUserName);
    }

    @Override
    public boolean deleteByExchangeId(String exchangeId) {
        return exchangeUserMapper.deleteByExchangeId(exchangeId);
    }

    @Override
    public boolean removeBySendUserId(String sendUserId, String exchangeId) {
        return exchangeUserMapper.removeBySendUserId(sendUserId, exchangeId);
    }

    @Override
    public IPage<ExchangeUser> getExchangeUserList(QueryRequest request, ExchangeUser exchangeUser) {
        QueryWrapper<ExchangeUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(ExchangeUser::getExchangeId, exchangeUser.getExchangeId());
        if (StringUtils.isNotBlank(exchangeUser.getSendUserName())) {
            queryWrapper.lambda().like(ExchangeUser::getSendUserName, exchangeUser.getSendUserName());
        }
        if (exchangeUser.getIsRead() != null) {
            queryWrapper.lambda().eq(ExchangeUser::getIsRead, exchangeUser.getIsRead());
        }
        Page<ExchangeUser> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean updateByExchangeId(String SendUserId, String exchangeId,String opinion) {
        return exchangeUserMapper.updateByExchangeId(SendUserId,exchangeId,opinion);
    }

    @Override
    public boolean upDateBySendUserId(String sendUserId, String exchangeId) {
        return exchangeUserMapper.upDateBySendUserId(sendUserId,exchangeId);
    }
}
