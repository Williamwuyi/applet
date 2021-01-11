package com.jiebao.platfrom.wx.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.wx.domain.Qun;
import com.jiebao.platfrom.wx.service.IQunService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-20
 */
@Controller
@RequestMapping("/wx/qun")
@Api(tags = "wx_微信群建立")
public class QunController {
    @Autowired
    IQunService qunService;

    @PostMapping("addOrUpdate")
    @ResponseBody
    @ApiOperation("群添加修改")
    public JiebaoResponse saveOrUpdate(Qun qun) {
        return qunService.addOrUpdate(qun);
    }


    @GetMapping("list")
    @ResponseBody
    @ApiOperation("群查询")
    public JiebaoResponse pageList(QueryRequest queryRequest, String name, String userName, Integer status, String deptId) {
        return qunService.pageList(queryRequest, name, userName, status, deptId);
    }

    @GetMapping("ListByDeptId")
    @ResponseBody
    @ApiOperation("通过部门查询")
    public JiebaoResponse ListByDeptId(String deptId) {
        return qunService.ListByDeptId(deptId);
    }


//    @GetMapping("qunCreat")
//    @ResponseBody
//    @ApiOperation("加载已经上报合格的群")
//    public JiebaoResponse qunCreat(String content) {
//        return qunService.importQun(content);
//    }


    @PostMapping("exPort")
    @ApiOperation("合格群导出  deptId 不传查出全部市级  反之 特定 市级")
    public void exPort(HttpServletResponse response, String[] deptId, String workName) {
        qunService.exPort(response, deptId, workName);
    }

    @GetMapping("getQunByDept")
    @ResponseBody
    @ApiOperation("通过组织机构查群")
    public JiebaoResponse getByDeptId(String deptId) {
        LambdaQueryWrapper<Qun> qunLambdaQueryWrapper = new LambdaQueryWrapper<>();
        qunLambdaQueryWrapper.eq(Qun::getCjDeptId, deptId);
        return new JiebaoResponse().data(qunService.getOne(qunLambdaQueryWrapper)).okMessage("查询成功");
    }
}
