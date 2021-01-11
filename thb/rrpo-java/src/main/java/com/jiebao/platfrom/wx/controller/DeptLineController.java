package com.jiebao.platfrom.wx.controller;


import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.wx.domain.DeptLine;
import com.jiebao.platfrom.wx.service.IDeptLineService;
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
 * @since 2020-10-21
 */
@RestController
@RequestMapping("/wx/dept-line")
@Api(tags = "wx-街道绑定线路")
public class DeptLineController {
    @Autowired
    IDeptLineService deptLineService;

    @GetMapping("getLine")
    @ApiOperation("根据对应的 部门去查询 对应的 线路   《=======》废弃")
    public JiebaoResponse getLine(String deptId) {
        return deptLineService.getLine(deptId);
    }

    @GetMapping("childGetLine")
    @ApiOperation("根据对应的 部门去查询 对应的 线路")
    public JiebaoResponse childGetLine(String deptId) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        return jiebaoResponse.data(deptLineService.childGetLine(deptId));
    }

    @PostMapping("add")
    @ApiOperation("添加对象")
    public JiebaoResponse add(DeptLine deptLine) {
        try {
            return deptLineService.add(deptLine);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return new JiebaoResponse().failMessage("不可重复绑定");
        }

    }

    @GetMapping("delete")
    @ApiOperation("解除关系")
    public JiebaoResponse delete(String[] ids) {
        return deptLineService.delete(ids);
    }
}
