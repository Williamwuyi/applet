package com.jiebao.platfrom.railway.controller;

import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.service.AsYearService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping(value = "asYear")
@Api(tags = "railWay-绑定年核")
public class AsYearController {
    @Autowired
    AsYearService asYearService;

    @ApiOperation("删除")
    @DeleteMapping("delete")
    public JiebaoResponse delete(String[] ids) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        return asYearService.removeByIds(Arrays.asList(ids)) ? jiebaoResponse.okMessage("删除成功") : jiebaoResponse.failMessage("删除失败");
    }

    @ApiOperation("查询集合")
    @GetMapping("list")
    public JiebaoResponse list(QueryRequest queryRequest, String gradeId) {
        return asYearService.pagelist(queryRequest, gradeId);
    }


    @GetMapping("fileList")
    @ApiOperation("年度文件池子")
    public JiebaoResponse fileList(QueryRequest queryRequest, String yearId, String deptId) {
        try {
            return new JiebaoResponse().data(asYearService.fileList(queryRequest, yearId, deptId)).okMessage("操作成功");
        } catch (Exception e) {
            return new JiebaoResponse().failMessage("操作失败");
        }


    }
}
