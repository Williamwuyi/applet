package com.jiebao.platfrom.accident.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.LoginLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/accident/login")
@Api(tags = "accident-用户登录次数统计分析")
public class LoginLogController {
    @Autowired
    LoginLogService loginLogService;


    @GetMapping("list")
    @ApiOperation("查看登录记录")
    @Log("查看登录记录")
    public JiebaoResponse list(String deptParentId, Integer year, Integer month) {
        return loginLogService.lists(deptParentId, year, month);
    }

    @GetMapping("userList")
    @ApiOperation("查询组织 具体人员登录次数")
    @Log("查询组织 具体人员登录次数")
    public JiebaoResponse userList(QueryRequest queryRequest, String deptId, String startDate, String endDate) {
        return loginLogService.listUsers(queryRequest, deptId, startDate, endDate);
    }


    @GetMapping("week")
    @ApiOperation("传入 年份  月份  得到 月内周数")
    @Log("查询组织 具体人员登录次数")
    public JiebaoResponse selectWeekCount(Integer year, Integer month) {
        return loginLogService.selectWeekCount(year, month);
    }


    @GetMapping("weekList")
    @ApiOperation("传入 年份  月份  得到 月内周数")
    @Log("查询组织 具体人员登录次数")
    public JiebaoResponse weekList(Integer year, Integer month, String deptId) {
        return loginLogService.weekList(year, month, deptId);
    }

    @GetMapping("yearLoginCount")
    @ApiOperation("年度考核统计次数专用")
    @Log("年度考核统计次数专用")
    public JiebaoResponse yearLoginCount(String DeptId, Integer number, String year) {
        return loginLogService.yearLoginCount(DeptId, number, year);
    }

}
