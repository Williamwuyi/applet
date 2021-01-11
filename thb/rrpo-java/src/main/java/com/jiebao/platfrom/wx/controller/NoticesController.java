package com.jiebao.platfrom.wx.controller;


import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.wx.domain.Notice;
import com.jiebao.platfrom.wx.service.INoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-09-18
 */
@RestController
@Api(tags = "wx-群 模块 消息通知")
@RequestMapping("/wx/notice")
public class NoticesController {
    @Autowired
    INoticeService noticeService;

    @PostMapping("save")
    @ApiOperation("新增通知")
    public JiebaoResponse save(Notice notice) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        System.out.println();
        return noticeService.save(notice) ? jiebaoResponse.okMessage("添加成功") : jiebaoResponse.failMessage("操作失败");
    }

    @GetMapping("List")
    @ApiOperation("查询通知")
    public JiebaoResponse List(QueryRequest queryRequest) {
        return noticeService.List(queryRequest);
    }

}
