package com.jiebao.platfrom.check.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.check.dao.MenusYearMapper;
import com.jiebao.platfrom.check.dao.YearBindMenusMapper;
import com.jiebao.platfrom.check.domain.Menus;
import com.jiebao.platfrom.check.dao.MenusMapper;
import com.jiebao.platfrom.check.domain.MenusYear;
import com.jiebao.platfrom.check.domain.YearBindMenus;
import com.jiebao.platfrom.check.service.IMenusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.check.service.IMenusYearService;
import com.jiebao.platfrom.check.service.IYearService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.domain.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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
public class MenusServiceImpl extends ServiceImpl<MenusMapper, Menus> implements IMenusService {
    @Autowired
    YearBindMenusMapper yearBindMenusMapper;
    @Autowired
    IYearService yearService;
    @Autowired
    IMenusYearService menusYearService;
    @Autowired
    MenusYearMapper menusYearMapper;

    @Override
    public JiebaoResponse addOrUpdate(Menus menus) {
        if (menus != null) {
            Integer integer = this.baseMapper.countName(menus.getName());
            if (integer != null) {
                return new JiebaoResponse().failMessage("名字重复");
            }
            menus.setDate(new Date());
        } else {
            Menus menus1 = getById(menus.getStandardId());  //数据库中的
            if (!menus.getName().equals(menus1.getName())) {  //如果数据发生更改
                Integer integer = this.baseMapper.countName(menus.getName());
                if (integer != null) {
                    return new JiebaoResponse().failMessage("名字重复");
                }
            }
        }
        return new JiebaoResponse().message(super.saveOrUpdate(menus) ? "操作成功" : "操作失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse deleteById(String menusId, Integer status) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        boolean flag = false;
        String message = "";
        if (status == null || status == 0) {//当不确定 本模块是否有绑定时
            if (yearBindMenusMapper.existByMenusId(menusId) == null) {
                flag = removeById(menusId);
                message = flag ? "操作成功" : "操作失败";
            } else {
                flag = false;
                message = "此模块有绑定对象，确定要删除吗？";
            }
        } else { //执意删除
            LambdaQueryWrapper<MenusYear> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MenusYear::getParentId, menusId);
            List<String> list = menusYearMapper.getMenusIdList(queryWrapper);
            if (list == null) {  //未绑定对象
                flag = removeById(menusId);
                message = flag ? "操作成功" : "操作失败";
            } else {   //需要删掉对应的考核内容  以及相关内容
                menusYearService.deleteByListAndYearDate(list);
                flag = removeById(menusId);
                message = "强制删除成功";
            }
        }
        if (flag) {
            //   删除关联对象
            LambdaQueryWrapper<YearBindMenus> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(YearBindMenus::getMenusId, menusId);
            yearBindMenusMapper.delete(queryWrapper);
        }
        return flag ? jiebaoResponse.okMessage(message) : jiebaoResponse.failMessage(message);
    }

    @Override
    public JiebaoResponse lists() {
        return new JiebaoResponse().data(list()).message("操作成功");
    }

    @Override
    public JiebaoResponse fatherList() {
        QueryWrapper<Menus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", "主级");
        return new JiebaoResponse().data(list(queryWrapper)).message("查询成功");
    }

    @Override
    public String selectByName(String name) {
        return this.baseMapper.getMenusIdByName(name);
    }

}
