package com.jiebao.platfrom.accident.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.accident.dao.CaseMapper;
import com.jiebao.platfrom.accident.dao.DeptSMapper;
import com.jiebao.platfrom.accident.daomain.Case;
import com.jiebao.platfrom.accident.service.ICaseService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
@Service
public class CaseServiceImpl extends ServiceImpl<CaseMapper, Case> implements ICaseService {
    @Autowired
    DeptService deptService;
    @Autowired
    DeptSMapper deptSMapper;

    @Override
    public JiebaoResponse list(QueryRequest queryRequest, String policeId, String cityLevelId, String quDeptId, String lineId, String nature, String startDate, String endDate) {
        QueryWrapper<Case> queryWrapper = new QueryWrapper<>();
        if (quDeptId != null) {
            queryWrapper.eq("city_qx_id", quDeptId);
        } else if (cityLevelId != null) {
            queryWrapper.eq("city_cs_id", cityLevelId);
        } else if (policeId != null) {
            queryWrapper.in("city_cs_id",  deptSMapper.selectDeptIds(policeId));
        } else {
            Dept dept = deptService.getDept();  //当前登陆人部门
            if (!dept.getDeptId().equals("0"))
                queryWrapper.eq("city_cs_id", dept.getDeptId());
        }
        if (lineId != null) {
            queryWrapper.eq("line_id", lineId);
        }
        if (nature != null) {
            queryWrapper.eq("nature", nature);
        }
        if (startDate != null) {
            queryWrapper.ge("date", startDate);    //不能小于此时间
        }
        if (endDate != null) {
            queryWrapper.le("date", endDate);//不能大于此时间
        }
        queryWrapper.orderByDesc("date");
        Page<Case> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return new JiebaoResponse().data(this.baseMapper.ListPage(page, queryWrapper)).message("查询成功");
    }

    @Override
    public JiebaoResponse map(String startDate, String endDate, Integer status) {
        if (status == null) {
            return new JiebaoResponse().failMessage("请选择类型");
        }
        List<Dept> childrenList = deptService.getChildrenList(deptService.getDept().getDeptId());  //市州级别  详情
        return new JiebaoResponse().data(fz(childrenList, startDate, endDate, status)).okMessage("查询成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse lock(String[] caseId, String month, Integer status) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        UpdateWrapper<Case> qw = new UpdateWrapper<>();
        qw.set("statu", status);
        if (caseId != null && caseId.length != 0) {
            qw.in("case_id", Arrays.asList(caseId));
        } else {
            if (month == null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                month = simpleDateFormat.format(new Date());
            }
            qw.likeRight("date", month);
        }

        jiebaoResponse = update(qw) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    /**
     * 视图
     * @param deptList
     * @param startDate
     * @param endDate
     * @param status
     * @return
     */
    private Map fz(List<Dept> deptList, String startDate, String endDate, Integer status) {  //事故性质  数据
        HashMap<String, List<String>> map = new HashMap<>();
        List<String> city = new ArrayList<>();
        List<String> list = null;  //储存类型
        if (status == 1) {
            list = this.baseMapper.sgXz();  //事故性质
        }
        boolean bool = true;
        for (Dept dept :
                deptList) {
            city.add(dept.getDeptName());
            int b = 1;//记录存储第几个数组
            for (String str : list
            ) {
                QueryWrapper<Case> queryWrapper = new QueryWrapper();
                if (startDate != null) {
                    queryWrapper.ge("date", startDate);
                }
                if (endDate != null) {
                    queryWrapper.le("date", endDate);
                }
                queryWrapper.eq("city_cs_id", dept.getDeptId());
                queryWrapper.eq("nature", str);
                if (bool) {
                    map.put(str + b, new ArrayList<String>());
                }
                map.get(str + b).add(this.baseMapper.count(queryWrapper).toString());
                ++b;
            }
            bool = false;
        }
        map.put("city", city);
        return map;

    }
}
