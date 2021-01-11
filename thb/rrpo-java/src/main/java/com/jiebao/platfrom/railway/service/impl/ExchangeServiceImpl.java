package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.ExchangeFileMapper;
import com.jiebao.platfrom.railway.dao.ExchangeMapper;
import com.jiebao.platfrom.railway.dao.ExchangeUserMapper;
import com.jiebao.platfrom.railway.domain.*;
import com.jiebao.platfrom.railway.service.ExchangeFileService;
import com.jiebao.platfrom.railway.service.ExchangeService;
import com.jiebao.platfrom.railway.service.ExchangeUserService;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Service("ExchangeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExchangeServiceImpl extends ServiceImpl<ExchangeMapper, Exchange> implements ExchangeService {


    @Autowired
    ExchangeUserMapper exchangeUserMapper;
    @Autowired
    UserService userService;

    @Override
    public IPage<Exchange> getExchangeList(QueryRequest request, Exchange exchange, String startTime, String endTime) {
        QueryWrapper<Exchange> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().ne(Exchange::getStatus, 4);
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.lambda().eq(Exchange::getCreatUser, username);
        }
        if (StringUtils.isNotBlank(exchange.getTitle())) {
            queryWrapper.lambda().like(Exchange::getTitle, exchange.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Exchange::getCreatTime, startTime).le(Exchange::getCreatTime, endTime);
        }
        if (StringUtils.isNotBlank(exchange.getStatus())) {
            queryWrapper.lambda().eq(Exchange::getStatus, exchange.getStatus());
        }
        if (StringUtils.isNotBlank(exchange.getIsCheck())){
            queryWrapper.lambda().eq(Exchange::getIsCheck, exchange.getIsCheck());
        }
        Page<Exchange> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Exchange> getExchangeListForCheck(QueryRequest request, Exchange exchange,String id, String startTime, String endTime) {
        QueryWrapper<Exchange> queryWrapper = new QueryWrapper();
            queryWrapper.lambda().eq(Exchange::getId,id);
        queryWrapper.lambda().ne(Exchange::getStatus, 4);
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.lambda().eq(Exchange::getCreatUser, username);
        }
        if (StringUtils.isNotBlank(exchange.getTitle())) {
            queryWrapper.lambda().like(Exchange::getTitle, exchange.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Exchange::getCreatTime, startTime).le(Exchange::getCreatTime, endTime);
        }
        if (StringUtils.isNotBlank(exchange.getStatus())) {
            queryWrapper.lambda().eq(Exchange::getStatus, exchange.getStatus());
        }
        Page<Exchange> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }



    @Override
    public IPage<Exchange> getExchangeInboxList(QueryRequest request, Exchange exchange, String startTime, String endTime) {
        QueryWrapper<Exchange> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().and(wrapper -> wrapper.eq(Exchange::getStatus, 3).or().eq(Exchange::getStatus, 4));
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        if (StringUtils.isNotBlank(username)) {
            User byName = userService.findWxByName(username);
            List<ExchangeUser> bySendUserId = exchangeUserMapper.getBySendUserId(byName.getUserId());
            ArrayList<String> exchangeUserIds = new ArrayList<>();
            for (ExchangeUser exchangeUser : bySendUserId
            ) {
                String exchangeId = exchangeUser.getExchangeId();
                exchangeUserIds.add(exchangeId);
            }
            if (exchangeUserIds.size() > 0) {
                queryWrapper.lambda().in(Exchange::getId, exchangeUserIds);
            }
            else {
                queryWrapper.lambda().in(Exchange::getId, "111111111111111111111111111111111");
            }
        }
        if (StringUtils.isNotBlank(exchange.getTitle())) {
            queryWrapper.lambda().like(Exchange::getTitle, exchange.getTitle());
        }
        if (StringUtils.isNotBlank(exchange.getCreatUser())){
            queryWrapper.lambda().like(Exchange::getCreatUser,exchange.getCreatUser());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Exchange::getReleaseTime, startTime).le(Exchange::getReleaseTime, endTime);
        }
        if (StringUtils.isNotBlank(exchange.getId())){
            queryWrapper.lambda().eq(Exchange::getId,exchange.getId());
        }
        Page<Exchange> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "releaseTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }
}
