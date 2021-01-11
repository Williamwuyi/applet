package com.jiebao.platfrom.check.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiebao.platfrom.check.domain.Year;
import com.jiebao.platfrom.check.service.IYearService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
@RestController
@RequestMapping("/check/year")
@Api(tags = "check-考核年份规则生成")
public class YearController {
    @Autowired
    IYearService yearService;

    @PostMapping("saveOrUpdate")
    @ApiOperation("添加修改  考核年份规则生成")
    public JiebaoResponse saveOrUpdate(Year year,String[] menusIdS) {
        return yearService.addOrUpdate(year,menusIdS);
    }

    @GetMapping("list")
    @ApiOperation("集合")
    public JiebaoResponse list() {
        QueryWrapper<Year> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("year_date");
        return new JiebaoResponse().data(yearService.list(queryWrapper)).message("查询成功");
    }

    @GetMapping("pageList")
    @ApiOperation("集合")
    public JiebaoResponse pageList(QueryRequest queryRequest, String dateYear) {
        return new JiebaoResponse().data(yearService.pageList(queryRequest, dateYear)).message("查询成功");
    }

    @DeleteMapping("deleteByLists/{lists}")
    @ApiOperation("集合删除")
    public JiebaoResponse deleteByLists(@PathVariable String[] lists) {
        return new JiebaoResponse().message(yearService.deleteByIds(Arrays.asList(lists)) ? "删除成功" : "删除失败");
    }

    @GetMapping("yearStringList")
    @ApiOperation("年份集合")
    public JiebaoResponse yearStringList() {
        return new JiebaoResponse().data(yearService.yearStringList()).message("查询成功");
    }

    @GetMapping("ok")
    @ApiOperation("当今年考核规则不在变更的时候  点击此接口")
    public JiebaoResponse ok(String yearId) {
        return yearService.ok(yearId);
    }


}
