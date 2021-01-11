package com.jiebao.platfrom.attendance.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiebao.platfrom.attendance.daomain.Record;
import com.jiebao.platfrom.attendance.service.IRecordsService;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.utils.CheckExcelUtil;
import com.jiebao.platfrom.system.dao.UserMapper;
import com.jiebao.platfrom.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-07
 */
@RestController
@RequestMapping("/attendance/record")
@Api(tags = "attendance-record")
public class RecordsController {
    @Autowired
    IRecordsService recordsService;
    @Autowired
    UserService userService;

    @PostMapping("add")
    @ApiOperation("月底接口录入")
    @Log("月底接口录入")
    public JiebaoResponse add(Record record) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
          jiebaoResponse = recordsService.saveOrUpdate(record) ? jiebaoResponse.okMessage("月底录入成功") : jiebaoResponse.failMessage("月底录入失败");
        return jiebaoResponse;
    }


    @GetMapping("listPage")
    @ApiOperation("打卡记录查询  部门  姓名模糊 时间")
    public JiebaoResponse list(QueryRequest queryRequest, String deptId, String name, String date) {
        return null;
    }

    @GetMapping("excel")
    @ApiOperation("导出")
    public void exportExcel(HttpServletResponse response, String dateMonth) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_month", dateMonth);
        queryWrapper.orderByDesc("date");
        CheckExcelUtil.exportExcel(recordsService.list(queryWrapper), response, userService,dateMonth);
    }
}
