package com.jiebao.platfrom.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.check.dao.NumMapper;
import com.jiebao.platfrom.check.domain.Num;
import com.jiebao.platfrom.check.service.INumService;
import com.jiebao.platfrom.common.authentication.JWTUtil;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.dao.DeptMapper;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-07-28
 */
@Service
public class NumServiceImpl extends ServiceImpl<NumMapper, Num> implements INumService {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    DeptService deptService;
    @Autowired
    DeptMapper deptMapper;

    @Override
    public JiebaoResponse pageList(QueryRequest queryRequest, String deptId, String yearId, Integer status) {
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());  //当前登陆人名字
        Dept dept = deptService.getById(userMapper.getDeptID(username));  //当前登陆人的部门
        QueryWrapper<Num> queryWrapper = new QueryWrapper<>();
        if (!dept.getParentId().equals("-1")) {  //当前登陆人非最高级
            deptId = dept.getDeptId();
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (deptId != null) {
            queryWrapper.eq("dept_id", deptId); //当前部门
        }
        if (yearId != null) {
            queryWrapper.eq("year_id", yearId);
        }
        queryWrapper.orderByDesc("year_date");
        Page<Num> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return new JiebaoResponse().data(this.baseMapper.pageList(page, queryWrapper)).message("查询成功");
    }

    @Override
    public JiebaoResponse map(String yearId) {
        List<Dept> childrenList = deptMapper.getDeptNameByAsc();
        HashMap<String, String[]> map = new HashMap<>();
        String[] dept = new String[childrenList.size()];
        String[] jcGz = new String[childrenList.size()]; //基础工作得分
        String[] gzXg = new String[childrenList.size()];//工作效果得分
        String[] count = new String[childrenList.size()]; //总得分
        String[] fpJcGz = new String[childrenList.size()]; //基础工作得分
        String[] fpGzXg = new String[childrenList.size()];//工作效果得分
        String[] fpCount = new String[childrenList.size()]; //总得分
        int i = 0;
        for (Dept dept1 :
                childrenList) {
            dept[i] = dept1.getDeptName();
            Num num = this.baseMapper.getNum(yearId, dept1.getDeptId());
            if (num != null) {
                jcGz[i] = num.getJcWork().toString();
                fpJcGz[i] = num.getFpJcWork().toString();
                gzXg[i] = num.getXgWork().toString();
                fpGzXg[i] = num.getFpXgWork().toString();
                count[i] = num.getNumber().toString();
                fpCount[i] = num.getFpNumber().toString();
            }
            i++;
        }
        map.put("dept", dept);
        map.put("jcGz", jcGz);
        map.put("gzXg", gzXg);
        map.put("count", count);
        map.put("fpJcGz", fpJcGz);
        map.put("fpGzXg", fpGzXg);
        map.put("fpCount", fpCount);
        return new JiebaoResponse().data(map).message("查询成功");
    }

    @Override
    public JiebaoResponse exist(String yearId, String deptId) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        jiebaoResponse = this.baseMapper.exist(yearId, deptId) == null ? jiebaoResponse.okMessage("可以创建") : jiebaoResponse.failMessage("不可以创建");
        return jiebaoResponse;
    }


    @Override
    public Num selectByYearAndDept(String yearId, String deptId) {
        QueryWrapper<Num> numQueryWrapper = new QueryWrapper<>();
        numQueryWrapper.eq("year_id", yearId);
        numQueryWrapper.eq("dept_id", deptId);
        return getOne(numQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse deadDate(String numId, Date date) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        Num num = getById(numId);
        num.setDeadDate(date);
        jiebaoResponse = updateById(num) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse status(String numId, Integer status) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        Num num = new Num();
        num.setStatus(status);
        num.setNumId(numId);
        return updateById(num) ? jiebaoResponse.okMessage("操作完成") : jiebaoResponse.failMessage("操作时间");
    }
}
