package com.jiebao.platfrom.accident.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.accident.dao.DeptSMapper;
import com.jiebao.platfrom.accident.daomain.Dept;
import com.jiebao.platfrom.accident.service.IDeptService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-09-29
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptSMapper, Dept> implements IDeptService {

    @Override
    public JiebaoResponse addOrSave(Dept dept) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        return saveOrUpdate(dept) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
    }

    @Override
    public JiebaoResponse list(String prentId) {
        if (prentId == null)
            prentId = "0";
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", prentId);
        return new JiebaoResponse().data(this.baseMapper.listDept(queryWrapper)).okMessage("查询成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse bind(String[] ids, String prentId) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        List<Dept> list = new ArrayList<>();
        for (String id : ids
        ) {
            Dept dept = new Dept();
            dept.setParentId(prentId);
            dept.setDeptId(id);
            list.add(dept);
        }
        return saveBatch(list) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
    }
}
