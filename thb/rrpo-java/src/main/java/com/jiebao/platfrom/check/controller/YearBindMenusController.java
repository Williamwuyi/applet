package com.jiebao.platfrom.check.controller;


import com.jiebao.platfrom.check.domain.YearBindMenus;
import com.jiebao.platfrom.check.service.IYearBindMenusService;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-09-03
 */
@RestController
@RequestMapping("/check/year-bind-menus")
@Api(tags = "check-考勤年度规则绑定考核类型模块")
public class YearBindMenusController {
    @Autowired
    IYearBindMenusService yearBindMenusService;

    @PostMapping("addOrUpdate")
    @ApiOperation("考勤年度规则绑定考核类型模块")
    @Log("绑定 考勤年度规则绑定考核类型模块")
    public JiebaoResponse addOrUpdate(YearBindMenus yearBindMenus) {
        return yearBindMenusService.addOrUpdate(yearBindMenus);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @Log("绑定 删除")
    public JiebaoResponse delete(String id) {
        return yearBindMenusService.delete(id);
    }

    @GetMapping("list")
    @ApiOperation("集合")
    @Log("绑定 考勤年度规则绑定考核类型模块")
    public JiebaoResponse list(String yearId) {
        return yearBindMenusService.list(yearId);
    }
}
