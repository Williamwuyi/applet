package com.jiebao.platfrom.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiebao.platfrom.check.domain.Grade;
import com.jiebao.platfrom.check.domain.YearBindMenus;
import com.jiebao.platfrom.check.dao.YearBindMenusMapper;
import com.jiebao.platfrom.check.service.IGradeService;
import com.jiebao.platfrom.check.service.IYearBindMenusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-09-03
 */
@Service
public class YearBindMenusServiceImpl extends ServiceImpl<YearBindMenusMapper, YearBindMenus> implements IYearBindMenusService {
    @Autowired
    IGradeService gradeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse addOrUpdate(YearBindMenus yearBindMenus) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        if (this.baseMapper.exist(yearBindMenus.getYearId(), yearBindMenus.getMenusId()) != null) {
            return jiebaoResponse.failMessage("不可重复绑定");
        }
        jiebaoResponse = super.saveOrUpdate(yearBindMenus) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse delete(String id) {//如果删除  某一模块   题库不用删除 怕后悔  但是   已经生成的考试项（已经分发的题目）得删除
        YearBindMenus yearBindMenus = getById(id);
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("year_id", yearBindMenus.getYearId());
        queryWrapper.in("parent_id", yearBindMenus.getBindId());
        gradeService.remove(queryWrapper);//删除试题
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        jiebaoResponse = removeById(id) ? jiebaoResponse.okMessage("删除成功") : jiebaoResponse.failMessage("删除失败");
        return jiebaoResponse;
    }

    @Override
    public JiebaoResponse list(String yearId) {
        QueryWrapper<YearBindMenus> queryWrapper = new QueryWrapper<>();
        if (yearId != null) {
            queryWrapper.eq("year_id", yearId);
        }
        return new JiebaoResponse().data(list(queryWrapper)).message("查询成功");
    }


}
