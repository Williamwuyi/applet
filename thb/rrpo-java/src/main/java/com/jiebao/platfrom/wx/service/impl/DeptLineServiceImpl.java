package com.jiebao.platfrom.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.wx.dao.DeptLineMapper;
import com.jiebao.platfrom.wx.domain.DeptLine;
import com.jiebao.platfrom.wx.service.IDeptLineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-10-21
 */
@Service
public class DeptLineServiceImpl extends ServiceImpl<DeptLineMapper, DeptLine> implements IDeptLineService {

    @Autowired
    DeptService deptService;

    @Override
    public JiebaoResponse getLine(String deptId) {
        QueryWrapper<DeptLine> queryWrapper = new QueryWrapper<>();
        if (deptId == null) {
            deptId = deptService.getDept().getDeptId();//当前登陆人
        }
        List<String> ids = new ArrayList<>();  //参数存储
        List<String> list = new ArrayList<>();//  附属 id存储
        ids.add(deptId);
        list.add(deptId);
        deptService.getAllIds(ids, list);
        queryWrapper.in("dept_id", list);
        queryWrapper.groupBy("line_id");
        return new JiebaoResponse().data(this.baseMapper.queryList(queryWrapper)).okMessage("查询成功");
    }

    @Override
    public List<DeptLine> childGetLine(String deptId) {
        QueryWrapper<DeptLine> queryWrapper = new QueryWrapper<>();
        Dept dept = null;
        if (deptId == null)
            dept = deptService.getDept();//当前登陆人
        else
            dept = deptService.getById(deptId);
        if (dept.getRank() < 1)
            return null;
        while (dept.getRank() != 1) {
            dept = deptService.getById(dept.getParentId());
        }
        queryWrapper.lambda().eq(DeptLine::getDeptId, dept.getDeptId());
        return this.baseMapper.queryList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse add(DeptLine deptLine) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        return save(deptLine) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败").data(deptLine);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse delete(String[] ids) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        return removeByIds(Arrays.asList(ids)) ? jiebaoResponse.okMessage("删除成功") : jiebaoResponse.failMessage("删除失败");
    }
}
