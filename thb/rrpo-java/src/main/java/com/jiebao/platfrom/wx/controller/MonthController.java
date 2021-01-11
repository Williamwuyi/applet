package com.jiebao.platfrom.wx.controller;


import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.service.FileService;
import com.jiebao.platfrom.wx.domain.Month;
import com.jiebao.platfrom.wx.service.IMonthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-22
 */
@RestController
@RequestMapping("/wx/month")
@Api(tags = "wx_月度评选")
public class MonthController {
    @Autowired
    IMonthService monthService;
    @Autowired
    FileService fileService;

    @PostMapping("saveorUpdate")
    @ApiOperation("添加或者修改")
    public JiebaoResponse saveOrUpdate(Month month, String[] fileIds) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        jiebaoResponse = monthService.saveOrUpdate(month) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }

    @Delete("delete")
    @ApiOperation("删除  ")
    public JiebaoResponse saveOrUpdate(String[] ids) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        jiebaoResponse = monthService.removeByIds(Arrays.asList(ids)) ? jiebaoResponse.okMessage("删除成功") : jiebaoResponse.failMessage("删除失败");
        return jiebaoResponse;
    }

    @GetMapping("list")
    @ApiOperation("查询集合")
    public JiebaoResponse pageList(QueryRequest queryRequest, String month, Integer look, Integer status, String dptId, String year,String deptId) {
        return monthService.pageList(queryRequest, month, look, status, dptId, year,deptId);
    }


    @GetMapping("appear")
    @ApiOperation("上报")
    public JiebaoResponse appear(String monthId, Integer status) {
        return monthService.appear(monthId, status);
    }

    @GetMapping("getById")
    @ApiOperation("查看具体信息")
    public JiebaoResponse getById(String monthId) {
        return new JiebaoResponse().data(monthService.getById(monthId)).okMessage("查询成功");
    }

    @PostMapping("downDocx")
    @ApiOperation("月报可入不可入 导出文档")
    public JiebaoResponse downDocx(HttpServletResponse response, String month) {
        return monthService.monthDocx(response, month);
    }

    @PostMapping("koran")
    @ApiOperation("可入不可入按钮")
    public JiebaoResponse koran(String month, Integer status) {  //month  为id
        return monthService.koran(month, status);
    }

    @GetMapping("monthDocxText")
    @ApiOperation("导出表直观样式")
    public JiebaoResponse monthDocxText(QueryRequest queryRequest, String month) {
        return monthService.monthDocxText(queryRequest, month);
    }

    @PostMapping("downDocxGood")
    @ApiOperation("月度评选优秀记录word导出")
    public JiebaoResponse downDocxGood(HttpServletResponse response, String month, String number, String content) {
        return monthService.downDocxGood(response, month, number, content);
    }

    @GetMapping("year")
    @ApiOperation("年度考核统计视图")
    public JiebaoResponse year(Integer year) { //传入年份 获得对应 统计数据
        return monthService.year(year);
    }

    @GetMapping("yearSN")
    @ApiOperation("年度考核统计视图 起止")
    public JiebaoResponse yearSN(Date startDate,Date endDate) { //传入年份 获得对应 统计数据
        return new JiebaoResponse().data(monthService.yearSN(startDate,endDate)).okMessage("查询成功");
    }

}
