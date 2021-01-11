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
import com.jiebao.platfrom.railway.dao.InformMapper;
import com.jiebao.platfrom.railway.dao.InformUserMapper;
import com.jiebao.platfrom.railway.domain.Inform;
import com.jiebao.platfrom.railway.domain.InformUser;
import com.jiebao.platfrom.railway.service.InformService;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service("InformService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class InformServiceImpl extends ServiceImpl<InformMapper, Inform> implements InformService {

    @Autowired
    InformMapper informMapper;

    @Autowired
    UserService userService;

    @Autowired
    InformUserMapper informUserMapper;

    @Override
    public IPage<Inform> getInformList(QueryRequest request, Inform inform, String startTime, String endTime) {
        QueryWrapper<Inform> queryWrapper = new QueryWrapper();
        //查询状态不为4，4：假删除状态
        queryWrapper.lambda().ne(Inform::getStatus, 4);
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.lambda().eq(Inform::getCreateUser, username);
        }
        if (StringUtils.isNotBlank(inform.getTitle())) {
            queryWrapper.lambda().like(Inform::getTitle, inform.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Inform::getCreateTime, startTime).le(Inform::getCreateTime, endTime);
        }
        if (StringUtils.isNotBlank(inform.getStatus())) {
            queryWrapper.lambda().eq(Inform::getStatus, inform.getStatus());
        }
        Page<Inform> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "createTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }


    @Override
    public IPage<Inform> getInformInboxList(QueryRequest request, Inform inform, String startTime, String endTime) {
        QueryWrapper<Inform> queryWrapper = new QueryWrapper();
        //查询状态已发布的3
        queryWrapper.lambda().and(wrapper -> wrapper.eq(Inform::getStatus, 3).or().eq(Inform::getStatus, 4));
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        if (StringUtils.isNotBlank(username)) {
            User byName = userService.findByName(username);
            List<InformUser> bySendUserId = informUserMapper.getByInformSendUserId(byName.getUserId());
            ArrayList<String> informUserIds = new ArrayList<>();
            for (InformUser informUser : bySendUserId
            ) {
                String informId = informUser.getInformId();
                informUserIds.add(informId);
            }
            if (informUserIds.size() > 0) {
                queryWrapper.lambda().in(Inform::getId, informUserIds);
            }
        }
        if (StringUtils.isNotBlank(inform.getTitle())) {
            queryWrapper.lambda().like(Inform::getTitle, inform.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Inform::getReleaseTime, startTime).le(Inform::getReleaseTime, endTime);
        }
        Page<Inform> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "releaseTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }


    @Override
    @Transactional
    public void updateByKey(Inform inform) {
        this.informMapper.updateById(inform);
    }

    @Override
    public List<Inform> getInformLists(Inform inform, QueryRequest request) {
        QueryWrapper<Inform> queryWrapper = new QueryWrapper();
        if (StringUtils.isNotBlank(inform.getTitle())) {
            queryWrapper.lambda().eq(Inform::getTitle, inform.getTitle());
        }
        SortUtil.handleWrapperSort(request, queryWrapper, "createTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectList(queryWrapper);
    }


    @Override
    public boolean revoke(String informId) {
        return informMapper.revoke(informId);
    }

    @Override
    public boolean release(String informId) {
        return informMapper.release(informId);
    }
}
