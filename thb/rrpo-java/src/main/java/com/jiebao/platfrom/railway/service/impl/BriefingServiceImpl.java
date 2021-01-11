package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.BriefingMapper;
import com.jiebao.platfrom.railway.dao.BriefingMapper;
import com.jiebao.platfrom.railway.dao.BriefingUserMapper;
import com.jiebao.platfrom.railway.domain.Briefing;
import com.jiebao.platfrom.railway.domain.Briefing;
import com.jiebao.platfrom.railway.domain.BriefingUser;
import com.jiebao.platfrom.railway.domain.Prize;
import com.jiebao.platfrom.railway.service.BriefingService;
import com.jiebao.platfrom.railway.service.BriefingService;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Service("BriefingService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BriefingServiceImpl extends ServiceImpl<BriefingMapper, Briefing> implements BriefingService {

    @Resource
    BriefingUserMapper briefingUserMapper;
    @Autowired
    UserService userService;
    @Autowired
    DeptService deptService;
    @Autowired
    BriefingMapper briefingMapper;

    @Override
    public IPage<Briefing> getBriefingList(QueryRequest request, Briefing briefing, String startTime, String endTime) {
        QueryWrapper<Briefing> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().ne(Briefing::getStatus, 4);
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.lambda().eq(Briefing::getCreatUser, username);
        }
        if (StringUtils.isNotBlank(briefing.getTitle())) {
            queryWrapper.lambda().like(Briefing::getTitle, briefing.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Briefing::getCreatTime, startTime).le(Briefing::getCreatTime, endTime);
        }
        if (briefing.getStatus() != null) {
            queryWrapper.lambda().eq(Briefing::getStatus, briefing.getStatus());
        }
        if (StringUtils.isNotBlank(briefing.getIsCheck())) {
            queryWrapper.lambda().eq(Briefing::getIsCheck, briefing.getIsCheck());
        }
        Page<Briefing> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Briefing> getBriefingListForCheck(QueryRequest request, Briefing briefing, String id, String startTime, String endTime) {
        QueryWrapper<Briefing> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(Briefing::getId, id);
        queryWrapper.lambda().ne(Briefing::getStatus, 4);
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.lambda().eq(Briefing::getCreatUser, username);
        }
        if (StringUtils.isNotBlank(briefing.getTitle())) {
            queryWrapper.lambda().like(Briefing::getTitle, briefing.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Briefing::getCreatTime, startTime).le(Briefing::getCreatTime, endTime);
        }
        if (briefing.getStatus() != null) {
            queryWrapper.lambda().eq(Briefing::getStatus, briefing.getStatus());
        }
        Page<Briefing> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "creatTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }


    @Override
    public IPage<Briefing> getBriefingInboxList(QueryRequest request, Briefing briefing, String startTime, String endTime) {
        QueryWrapper<Briefing> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().and(wrapper -> wrapper.eq(Briefing::getStatus, 3).or().eq(Briefing::getStatus, 4));
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        if (StringUtils.isNotBlank(username)) {
            User byName = userService.findByName(username);
            List<BriefingUser> bySendUserId = briefingUserMapper.getBySendUserId(byName.getUserId());
            ArrayList<String> briefingUserIds = new ArrayList<>();
            for (BriefingUser briefingUser : bySendUserId
            ) {
                String briefingId = briefingUser.getBriefingId();
                briefingUserIds.add(briefingId);
            }
            if (briefingUserIds.size() > 0) {
                queryWrapper.lambda().in(Briefing::getId, briefingUserIds);
            } else {
                queryWrapper.lambda().in(Briefing::getId, "111111111111111111111111111111111");
            }
        }
        if (StringUtils.isNotBlank(briefing.getTitle())) {
            queryWrapper.lambda().like(Briefing::getTitle, briefing.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Briefing::getReleaseTime, startTime).le(Briefing::getReleaseTime, endTime);
        }
        if (StringUtils.isNotBlank(briefing.getId())) {
            queryWrapper.lambda().eq(Briefing::getId, briefing.getId());
        }
        Page<Briefing> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "releaseTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }


    @Override
    public IPage<Briefing> countList(QueryRequest request, Briefing briefing, String startTime, String endTime) {
        QueryWrapper<Briefing> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Briefing::getStatus, 3);
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        Dept dept = deptService.getDept();
        if (dept.getRank() != 0) {
            queryWrapper.lambda().eq(Briefing::getCreatUser, username);
        }
        if (StringUtils.isNotBlank(briefing.getTitle())) {
            queryWrapper.lambda().like(Briefing::getTitle, briefing.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Briefing::getReleaseTime, startTime).le(Briefing::getReleaseTime, endTime);
        }
        if (briefing.getStatus() != null) {
            queryWrapper.lambda().eq(Briefing::getStatus, briefing.getStatus());
        }
        if (StringUtils.isNotBlank(briefing.getIsCheck())) {
            queryWrapper.lambda().eq(Briefing::getIsCheck, briefing.getIsCheck());
        }
        Page<Briefing> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "releaseTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Briefing> countListByCity(Integer pageNum,Integer pageSize,String startTime,String endTime,String deptName,String title) {
        List<Dept> deptId = deptService.getDeptByName(deptName);
        Page<Briefing> page = new Page<>(pageNum, pageSize);
        if (deptId.size() == 0) {
            IPage<Briefing> briefingIPage = briefingMapper.countListByCity(page, startTime, endTime, null, title);
            return briefingIPage;
        } else {
            IPage<Briefing> briefingIPage = briefingMapper.countListByCity(page, startTime, endTime, deptId.get(0).getDeptId(), title);
            return briefingIPage;
        }
    }
}
