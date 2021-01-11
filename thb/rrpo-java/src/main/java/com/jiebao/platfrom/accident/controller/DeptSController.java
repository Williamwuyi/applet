package com.jiebao.platfrom.accident.controller;


import com.jiebao.platfrom.accident.daomain.Dept;
import com.jiebao.platfrom.accident.service.IDeptService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
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
 * @since 2020-09-29
 */
@RestController
@RequestMapping("/accident/dept")
@Api(tags = "accident-统计分析 部门列表")
public class DeptSController {
    @Autowired
    IDeptService deptService;

    @PostMapping("addOrSave")
    @ApiOperation("添加修改")
    public JiebaoResponse addOrSave(Dept dept) {
        return deptService.addOrSave(dept);
    }

    @PostMapping("bind")
    @ApiOperation("绑定")
    public JiebaoResponse bind(String[] ids, String prentId) {
        return deptService.bind(ids, prentId);
    }

    @GetMapping("list")
    @ApiOperation("查询集合")
    public JiebaoResponse list(String prentId) {
        return deptService.list(prentId);
    }
}
