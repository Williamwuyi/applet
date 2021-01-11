package com.jiebao.platfrom.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.wx.dao.QunMapper;
import com.jiebao.platfrom.wx.domain.Qun;
import com.jiebao.platfrom.wx.domain.UserI;
import com.jiebao.platfrom.wx.dao.UserIMapper;
import com.jiebao.platfrom.wx.service.IQunService;
import com.jiebao.platfrom.wx.service.IUserIService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-20
 */
@Service
public class UserIServiceImpl extends ServiceImpl<UserIMapper, UserI> implements IUserIService {
    @Autowired
    IQunService qunService;
    @Autowired
    DeptService deptService;
    @Autowired
    QunMapper qunMapper;
    @Autowired
    DeptMapper deptMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(UserI entity) {
        if (entity.getWxUserId() == null) {
            entity.setDate(new Date());
            Integer rank = qunMapper.getRankByQunId(entity.getQunId());
            if (rank == 3)
                diZuiXz(entity);
            if (rank == 2)
                diZuiQx(entity);
            if (rank == 1)
                diZuiSz(entity);
        }
        boolean b = super.saveOrUpdate(entity);
        return b;
    }

    private void diZuiXz(UserI userI) {
        userI.setDeptXz(qunMapper.getIdByQunId(userI.getQunId()));
        diZuiQx(userI);
    }

    private void diZuiQx(UserI userI) {
        if (userI.getDeptXz() == null)
            userI.setDeptQx(qunMapper.getIdByQunId(userI.getQunId()));
        else {
            Dept dept = deptService.getById(userI.getDeptXz());
            String idById = deptMapper.getIdById(dept.getParentId());
            userI.setDeptQx(idById);
        }
        diZuiSz(userI);
    }

    private void diZuiSz(UserI userI) {
        if (userI.getDeptQx() == null)
            userI.setDeptSz(qunMapper.getIdByQunId(userI.getQunId()));
        else {
            Dept dept = deptService.getById(userI.getDeptQx());
            String idById = deptMapper.getIdById(dept.getParentId());
            userI.setDeptSz(idById);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse deleteS(String[] wxUserIdS, String qunId) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        boolean b = removeByIds(Arrays.asList(wxUserIdS));
        jiebaoResponse = b ? jiebaoResponse.okMessage("删除成功") : jiebaoResponse.failMessage("删除失败");
        return jiebaoResponse;
    }

    @Override
    public JiebaoResponse list(QueryRequest queryRequest, String deptId, String name, String wxQunId) {
        QueryWrapper<UserI> queryWrapper = new QueryWrapper<>();
        if (wxQunId != null) {
            queryWrapper.eq("qun_id", wxQunId);
        }
        if (name != null) {
            queryWrapper.like("name", name);
        }
        if (deptId != null) {
            List<String> prentIdS = new ArrayList<>();
            List<String> deptIds = new ArrayList<>(); //存储id
            prentIdS.add(deptId);
            deptIds.add(deptId);
            deptService.getAllIds(prentIdS, deptIds);
            QueryWrapper<Qun> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.in("cj_dept_id", deptIds);
            List<String> list = this.baseMapper.listWxId(queryWrapper1);//所有的群的id
            queryWrapper.in("qun_id", list);
        }
        queryWrapper.orderByDesc("date");
        Page<UserI> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return new JiebaoResponse().data(this.baseMapper.listPage(page, queryWrapper));
    }
}
