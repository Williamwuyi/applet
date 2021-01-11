package com.jiebao.platfrom.wx.controller;


import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.wx.service.IShService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-20
 */
@RestController
@RequestMapping("/wx/sh")
@Api(tags = "wx_微信群提交审核")
public class ShController {
    @Autowired
    IShService shService;

    @PostMapping("shWx")
    @ApiOperation("审核")
    public JiebaoResponse shWx(String qunId, Integer status, String massage) {
        return shService.shWx(qunId, status, massage);
    }

    @GetMapping("list")
    @ApiOperation("查看审核节点")
    public JiebaoResponse list(QueryRequest queryRequest,String qunid) {
        return shService.list(queryRequest,qunid);
    }
}
