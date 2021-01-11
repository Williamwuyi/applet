package com.jiebao.platfrom.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.check.dao.*;
import com.jiebao.platfrom.check.domain.*;
import com.jiebao.platfrom.check.service.*;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
@Service
public class YearServiceImpl extends ServiceImpl<YearMapper, Year> implements IYearService {
    @Autowired
    MenusYearMapper menusYearMapper;
    @Autowired
    DeptService deptService;
    @Autowired
    IMenusService menusService;
    @Autowired
    IMenusYearService menusYearService;
    @Autowired
    IGradeService gradeService;
    @Autowired
    GradeMapper gradeMapper;
    @Autowired
    YearBindMenusMapper yearBindMenusMapper;
    @Autowired
    NumMapper numMapper;
    @Autowired
    IYearBindMenusService yearBindMenusService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse addOrUpdate(Year year, String[] menusIdS) {
        QueryWrapper<Year> queryWrapper = new QueryWrapper<>();
        if (year.getYearId() != null) {
            Year year1 = getById(year.getYearId());  //还未修改的 数据
            if (year1.getYearDate().equals(year.getYearDate())) {   //本次修改并未修改名字
                super.updateById(year);
                return new JiebaoResponse().okMessage("操作成功");
            }
        }
        queryWrapper.eq("year_date", year.getYearDate());
        if (getOne(queryWrapper) != null) {
            return new JiebaoResponse().message("年份重复");
        }
        boolean save = super.saveOrUpdate(year);
        if (save) {
            List<YearBindMenus> yearBindMenusList = new ArrayList<>();//储存模块
            if(menusIdS!=null)
            for (String mid : menusIdS
            ) {
                YearBindMenus yearBindMenus = new YearBindMenus();
                yearBindMenus.setMenusId(mid);
                yearBindMenus.setYearId(year.getYearId());
                yearBindMenusList.add(yearBindMenus);
            }
            yearBindMenusService.saveBatch(yearBindMenusList);
        }
        return save ? new JiebaoResponse().okMessage("操作成功") : new JiebaoResponse().failMessage("操作失败");
    }

    @Override
    public List<Year> list(QueryWrapper<Year> queryWrapper) {
        List<Year> list = this.baseMapper.list(queryWrapper);
        for (Year year : list
        ) {
            List<String> menusId = yearBindMenusMapper.listMenusId(year.getYearId()); //
            if (menusId == null || menusId.size() == 0)
                continue;
            List<Menus> list1 = (List<Menus>) menusService.listByIds(menusId);  //所绑定的模块
            List<YearSize> list2 = new ArrayList<>();//赋值存储
            for (Menus menu : list1
            ) {
                YearSize yearSize = new YearSize();
                yearSize.setName(menu.getName());
                yearSize.setCount(menusYearMapper.countNumber(year.getYearId(), menu.getStandardId()));
                list2.add(yearSize);
            }
            year.setList(list2);
        }
        return list;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<? extends Serializable> idList) {  //删除 年度考核的同时 也要 删除对应的试题  以及
        LambdaQueryWrapper<MenusYear> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MenusYear::getYearId, idList);
        List<String> menusIdList = menusYearMapper.getMenusIdList(queryWrapper);// 所有的考核条目
        if (super.removeByIds(idList)) {
            if (menusIdList != null && menusIdList.size() > 0)
                return menusYearService.deleteByListAndYearDate(menusIdList);
            else
                return true;
        } else
            return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse ok(String yearId) {
        List<Grade> gradeArrayList = new ArrayList<>();
        QueryWrapper<MenusYear> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("year_id", yearId);
        queryWrapper.in("parent_id", this.baseMapper.listYB(yearId));  //现存的绑定项
        List<MenusYear> menusYearList = menusYearMapper.selectList(queryWrapper); //当年所有的试题
        List<Dept> childrenList = deptService.getChildrenList("0");
        for (Dept dept : childrenList
        ) {
            if (dept.getDeptName().contains("市")) {
                for (MenusYear m : menusYearList
                ) {
                    if (gradeMapper.exist(yearId, dept.getDeptId(), m.getMenusYearId()) != null) //判断是否绑定已经
                        continue;
                    Grade grade = new Grade();
                    grade.setYearId(yearId);
                    grade.setDeptId(dept.getDeptId());
                    grade.setCheckId(m.getMenusYearId());
                    grade.setParentId(m.getParentId());
                    gradeArrayList.add(grade);
                }
            }
        }
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        jiebaoResponse = gradeService.saveBatch(gradeArrayList) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @Override
    /**
     * 首先删除   此年的相关考核条目  ，然后得删掉  考核生成的分数对象 一并删除
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(Collection<? extends Serializable> list) {
        if (removeByIds(list)) {
            LambdaQueryWrapper<Num> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Num::getYearId, list);
            return numMapper.deleteByYearId(queryWrapper);
        } else
            return true;
    }



    @Override
    public JiebaoResponse pageList(QueryRequest queryRequest, String yearDate) {
        QueryWrapper<Year> queryWrapper = new QueryWrapper<>();
        if (yearDate != null) {
            queryWrapper.eq("year_date", yearDate);
        }
        queryWrapper.orderByDesc("year_date");
        Page<Year> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return new JiebaoResponse().data(this.baseMapper.pageYear(page, queryWrapper)).message("查询成功");
    }

    @Override
    public JiebaoResponse yearStringList() {
        return new JiebaoResponse().data(this.baseMapper.yearStringList()).message("查询成功");
    }

}
