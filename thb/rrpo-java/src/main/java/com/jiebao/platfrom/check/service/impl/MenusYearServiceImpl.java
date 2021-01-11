package com.jiebao.platfrom.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiebao.platfrom.check.dao.*;
import com.jiebao.platfrom.check.domain.*;
import com.jiebao.platfrom.check.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.utils.CheckExcelUtil;
import com.jiebao.platfrom.common.utils.ExportExcel;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
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
public class MenusYearServiceImpl extends ServiceImpl<MenusYearMapper, MenusYear> implements IMenusYearService {

    @Autowired
    MenusMapper menusMapper;
    @Autowired
    IMenusService menusService;
    @Autowired
    IYearService yearService;
    @Autowired
    MenusYearMapper menusYearMapper;
    @Autowired
    YearBindMenusMapper yearBindMenusMapper;
    @Autowired
    YearMapper yearMapper;
    @Autowired
    GradeMapper gradeMapper;
    @Autowired
    DeptService deptService;
    @Autowired
    IGradeService gradeService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse addOrUpdate(MenusYear menusYear) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        boolean flag = false;
        if (menusYear.getMenusYearId() == null) {//新增
            Integer integer = this.baseMapper.exSit(menusYear.getContent(), menusYear.getYearId());
            if (integer != null) {
                return jiebaoResponse.failMessage("内容重复");
            }
            menusYear.setDate(new Date());
            menusYear.setSorts(1000);
            flag = true;
        } else {  //删除
            MenusYear menusYear1 = getById(menusYear.getMenusYearId()); //数据库已存在的
            if (!menusYear1.getContent().equals(menusYear.getContent())) {  //如果内容发生了变化
                Integer integer = this.baseMapper.exSit(menusYear.getContent(), menusYear.getYearId());
                if (integer != null) {
                    return jiebaoResponse.failMessage("内容重复");
                }
            }
        }
        boolean b = super.saveOrUpdate(menusYear);
        if (b && flag) {
            List<Dept> childrenList = deptService.getChildrenList("0");
            for (Dept dept : childrenList
            ) {
                if (!dept.getDeptName().contains("公安处")&&!dept.getDeptName().contains("段")) {
                    Grade grade = new Grade();
                    grade.setYearId(menusYear.getYearId());
                    grade.setDeptId(dept.getDeptId());
                    grade.setCheckId(menusYear.getMenusYearId());
                    grade.setParentId(menusYear.getParentId());
                    grade.setSorts(menusYear.getSorts());
                    gradeMapper.insert(grade);
                }
            }
        }
        jiebaoResponse = b ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse List(String yearId) {  //通过 年份考核 查询 对应的年内考核项
        List<String> menusId = yearBindMenusMapper.listMenusId(yearId);
        List<YearZu> list = new ArrayList<>();
        for (String menuId : menusId  //考核分组类型
        ) {
            Menus menus = menusService.getById(menuId);
            if (menus == null) {
                continue;
            }
            QueryWrapper<MenusYear> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("year_id", yearId);
            queryWrapper.eq("parent_id", menus.getStandardId());
            queryWrapper.orderByAsc("sorts");
            YearZu yearZu = new YearZu();
            yearZu.setId(menus.getStandardId());
            yearZu.setName(menus.getName());
            yearZu.setNum(menus.getNum());
            yearZu.setList(this.baseMapper.selectList(queryWrapper));
            list.add(yearZu);
        }
        return new JiebaoResponse().data(list).message("查询成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByListAndYearDate(Collection<? extends Serializable> list) {
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("check_id", Arrays.asList(list));
        boolean b = removeByIds(list);
        if (b) {
            gradeMapper.deleteByCheckId(queryWrapper);
            return true;
        } else
            return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse excel(MultipartFile multipartFile, String year_id) {
        List<MenusYear> list = new ArrayList<>();
        JiebaoResponse jiebaoResponse = CheckExcelUtil.excel(year_id, multipartFile, menusService, this, menusYearMapper, yearBindMenusMapper, list);
        if ((int) jiebaoResponse.get("status") == 1) { //操作成功 标志
            List<Grade> gradeArrayList = new ArrayList<>();
            QueryWrapper<MenusYear> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("year_id", year_id);
            queryWrapper.in("parent_id", yearMapper.listYB(year_id));  //现存的绑定项
            List<Dept> childrenList = deptService.getChildrenList("0");
            for (Dept dept : childrenList
            ) {
                if (!dept.getDeptName().contains("公安处")&&!dept.getDeptName().contains("段")) {
                    for (MenusYear m : list
                    ) {
                        Grade grade = new Grade();
                        grade.setYearId(year_id);
                        grade.setDeptId(dept.getDeptId());
                        grade.setCheckId(m.getMenusYearId());
                        grade.setParentId(m.getParentId());
                        grade.setSorts(m.getSorts());
                        gradeArrayList.add(grade);
                    }
                }
            }
            gradeService.saveBatch(gradeArrayList);
        }
        return jiebaoResponse;
    }

    @Override
    public boolean importTemplate(String yearId, HttpServletResponse response) {
        List<String> list = yearBindMenusMapper.listMenusId(yearId);//得到模块分组
        List<Menus> listMenus = (List<Menus>) menusService.listByIds(list);
        if (listMenus == null)
            return false;
        ExportExcel.imTemplate(listMenus, response);
        return true;
    }
}
