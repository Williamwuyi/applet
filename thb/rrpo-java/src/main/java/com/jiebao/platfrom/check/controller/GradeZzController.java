package com.jiebao.platfrom.check.controller;


import com.jiebao.platfrom.check.service.IGradeZzService;
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
 * @since 2020-08-11
 */
@RestController
@RequestMapping("/check/grade-zz")
@Api(tags = "check-佐证查询")
public class GradeZzController {
    @Autowired
    IGradeZzService gradeZzService;

//    @GetMapping("list")
//    @ApiOperation("佐证查询  参数每条扣分记录id")
//    public JiebaoResponse list(String gradeId, String yearId, String deptId, String menusIds, Integer type, QueryRequest queryRequest) {
//        return gradeZzService.list(gradeId, yearId, deptId, menusIds, type,queryRequest);
//    }

//    @DeleteMapping("delete")
//    @ApiOperation("删除佐证")
//    public JiebaoResponse delete(String[] list) {
//        if (list == null) {
//            return new JiebaoResponse().message("未传值");
//        }
//        return gradeZzService.deleteByGradeIdAndZzId(list);
//    }
//
//    @GetMapping("getListCZ")
//    @ApiOperation("去池子里面那数据")
//    public JiebaoResponse getListCZ(Integer type, Integer status, QueryRequest queryRequest) {
//        return gradeZzService.getData(type, status,queryRequest);
//    }
}
