package com.jiebao.platfrom.check.controller;


import com.jiebao.platfrom.check.service.INumService;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 20000000000000000000000000000000000000000000000000020-07-28
 */
@RestController
@RequestMapping("/check/num")
@Api(tags = "check-年终考核统计分数表")
public class NumController {
    @Autowired
    INumService numService;

    @GetMapping("num")
    @ApiOperation("获得数据 年终考核 ")
    @Log("获得数据 年终考核")
    public JiebaoResponse pageList(QueryRequest queryRequest, String deptId, String yearId, Integer status) {
        return numService.pageList(queryRequest, deptId, yearId, status);
    }

    @GetMapping("map")
    @ApiOperation("视图数据接口")
    @Log("试图数据")
    public JiebaoResponse map(String yearId) {
        return numService.map(yearId);
    }


    @GetMapping("exist")
    @ApiOperation("查询此考核结果是否第一次产生")
    public JiebaoResponse exist(String yearId, String deptId) {
        return numService.exist(yearId, deptId);
    }

    @PostMapping("deadDate")
    @ApiOperation("逾期回复时间赋值")
    public JiebaoResponse deadDate(String numId, Date date) {
        return numService.deadDate(numId, date);
    }

    @GetMapping("changeStatus")
    @ApiOperation("修改内容状态")
    public JiebaoResponse changeStatus(String numId, Integer status) {
        return numService.status(numId, status);
    }
}
