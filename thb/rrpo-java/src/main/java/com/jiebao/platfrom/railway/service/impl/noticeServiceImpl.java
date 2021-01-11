package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.SortUtil;
import com.jiebao.platfrom.railway.dao.NoticeMapper;
import com.jiebao.platfrom.railway.domain.Notice;
import com.jiebao.platfrom.railway.service.NoticeService;
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

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service("NoticeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class noticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    NoticeMapper noticeMapper;

    @Autowired
    UserService userService;
    @Autowired
    DeptService deptService;


    @Override
    public IPage<Notice> getNoticeList(QueryRequest request, Notice notice, String startTime, String endTime) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper();
        //查询状态不为4，4：假删除状态
        queryWrapper.lambda().ne(Notice::getStatus, 4);
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        User byName = userService.findWxByName(username);

        queryWrapper.lambda().eq(Notice::getUserId,byName.getUserId());
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.lambda().eq(Notice::getCreateUser, username);
        }
        if (StringUtils.isNotBlank(notice.getTitle())) {
            queryWrapper.lambda().like(Notice::getTitle, notice.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Notice::getCreateTime, startTime).le(Notice::getCreateTime, endTime);
        }
        if (StringUtils.isNotBlank(notice.getStatus())) {
            queryWrapper.lambda().eq(Notice::getStatus, notice.getStatus());
        }
        Page<Notice> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "createTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }


    @Override
    public IPage<Notice> getNoticeInboxList(QueryRequest request, Notice notice, String startTime, String endTime) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper();
        //查询状态已发布的3
        queryWrapper.lambda().and(wrapper -> wrapper.eq(Notice::getStatus, 3).or().eq(Notice::getStatus, 4));
        if (StringUtils.isNotBlank(notice.getTitle())) {
            queryWrapper.lambda().like(Notice::getTitle, notice.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Notice::getReleaseTime, startTime).le(Notice::getReleaseTime, endTime);
        }
        if (StringUtils.isNotBlank(notice.getId())){
            queryWrapper.lambda().eq(Notice::getId,notice.getId());
        }
        Page<Notice> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "releaseTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }



    @Override
    public IPage<Notice> getNoticeInboxListByParent(QueryRequest request, Notice notice, String startTime, String endTime) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper();
        //查询状态已发布的3
        queryWrapper.lambda().and(wrapper -> wrapper.eq(Notice::getStatus, 3).or().eq(Notice::getStatus, 4));
        Dept dept = deptService.getDept();
        queryWrapper.lambda().eq(Notice::getDeptId,dept.getParentId());
        if (StringUtils.isNotBlank(notice.getTitle())) {
            queryWrapper.lambda().like(Notice::getTitle, notice.getTitle());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            queryWrapper.lambda().ge(Notice::getReleaseTime, startTime).le(Notice::getReleaseTime, endTime);
        }
        if (StringUtils.isNotBlank(notice.getId())){
            queryWrapper.lambda().eq(Notice::getId,notice.getId());
        }
        Page<Notice> page = new Page<>(request.getPageNum(), request.getPageSize());
        SortUtil.handleWrapperSort(request, queryWrapper, "releaseTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }



    @Override
    @Transactional
    public void updateByKey(Notice notice) {
        this.noticeMapper.updateById(notice);
    }

    @Override
    public List<Notice> getNoticeLists(Notice notice, QueryRequest request) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper();
        if (StringUtils.isNotBlank(notice.getTitle())) {
            queryWrapper.lambda().eq(Notice::getTitle, notice.getTitle());
        }
        SortUtil.handleWrapperSort(request, queryWrapper, "createTime", JiebaoConstant.ORDER_DESC, true);
        return this.baseMapper.selectList(queryWrapper);
    }


    @Override
    public boolean revokeNotice(String noticeId) {
        return noticeMapper.revokeNotice(noticeId);
    }

    @Override
    public boolean release(String noticeId) {
        return noticeMapper.release(noticeId);
    }
}
