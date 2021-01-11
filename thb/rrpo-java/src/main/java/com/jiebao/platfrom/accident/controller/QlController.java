package com.jiebao.platfrom.accident.controller;


import com.jiebao.platfrom.accident.daomain.Jk;
import com.jiebao.platfrom.accident.daomain.Ql;
import com.jiebao.platfrom.accident.service.IQlService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-11-07
 */
@RestController
@RequestMapping("/accident/ql")
@Api(tags = "accident-桥梁")
public class QlController {
    @Autowired
    IQlService qlService;

    @GetMapping("list")
    @ApiOperation("查询")
    public JiebaoResponse listPage(QueryRequest queryRequest, String deptName, String policeName, String gwdName, String qxDeptName) {
        //     try {
        return new JiebaoResponse().data(qlService.listPage(queryRequest, deptName, policeName, gwdName, qxDeptName)).okMessage("查询成功");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return new JiebaoResponse().failMessage("查询失败");
//        }

    }

    @PostMapping("excel")
    @ApiOperation("导出")
    public JiebaoResponse importExcel(HttpServletResponse response, String deptName, String policeName, String gwdName, String qxDeptName) {
        return new JiebaoResponse().data(qlService.importExcel(response, deptName, policeName, gwdName, qxDeptName));
    }

    @PostMapping("saveOrUpdate")
    @ApiOperation("新增或者修改")
    public JiebaoResponse saveOrUpdate(Ql ql, String[] tp) { //tp   文件的id
        try {
            qlService.addOrUpdate(ql, Arrays.asList(tp));
            return new JiebaoResponse().data(ql).okMessage("操作成功");
        } catch (Exception e) {
            return new JiebaoResponse().failMessage("操作失败");
        }
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    public JiebaoResponse delete(String[] idList) {
        try {
            qlService.delete(idList);
            return new JiebaoResponse().okMessage("删除");
        } catch (Exception e) {
            return new JiebaoResponse().failMessage("操作失败");
        }
    }


}
