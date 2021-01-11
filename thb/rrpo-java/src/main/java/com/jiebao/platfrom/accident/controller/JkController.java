package com.jiebao.platfrom.accident.controller;


import com.jiebao.platfrom.accident.daomain.Jk;
import com.jiebao.platfrom.accident.service.IJkService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-11-07
 */
@RestController
@RequestMapping("/accident/jk")
@Api(tags = "accident-监控")
public class JkController {

    @Autowired
    IJkService jkService;

    @GetMapping("list")
    @ApiOperation(" ")
    public JiebaoResponse listPage(QueryRequest queryRequest, String gac, String dzs,String xsq,String lineName,String year) {
        try {
            return new JiebaoResponse().data(jkService.listPage(queryRequest, gac, dzs,xsq,lineName,year)).okMessage("查询成功");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new JiebaoResponse().failMessage("查询失败");
        }
    }

    @PostMapping("excel")
    @ApiOperation("导出")
    public JiebaoResponse importExcel(HttpServletResponse response, String gac, String dzs,String xsq,String lineName,String year) {
        return new JiebaoResponse().data(jkService.importExcel(response, gac, dzs,xsq,lineName,year));
    }

    @PostMapping("saveOrUpdate")
    @ApiOperation("新增或者修改")
    public JiebaoResponse saveOrUpdate(Jk jk, String[] tp) {
            try {
        List<String> list = null;
        if (tp != null) {
            list = Arrays.asList(tp);
        }
        jkService.addOrUpdate(jk, list);
        return new JiebaoResponse().data(jk).okMessage("操作成功");
        } catch (Exception e) {
            return new JiebaoResponse().failMessage("操作失败");
        }
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    public JiebaoResponse delete(String[] idList) {
        try {
            jkService.delete(idList);
            return new JiebaoResponse().okMessage("删除");
        } catch (Exception e) {
            return new JiebaoResponse().failMessage("操作失败");
        }
    }

    @GetMapping("countTable")
    @ApiOperation("查看统计图")
    public JiebaoResponse countTable(String year,String xlmc) {
    //    try {
            return new JiebaoResponse().okMessage("删除").data(jkService.countTable(year,xlmc));
//        } catch (Exception e) {
//            return new JiebaoResponse().failMessage("操作失败");
//        }
    }


}
