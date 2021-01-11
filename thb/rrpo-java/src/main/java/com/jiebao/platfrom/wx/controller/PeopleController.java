package com.jiebao.platfrom.wx.controller;


import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.wx.domain.People;
import com.jiebao.platfrom.wx.service.IPeopleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-10-16
 */
@RestController
@RequestMapping("/wx/people")
@Api(tags = "wx_护路队员")
public class PeopleController {
    @Autowired
    private IPeopleService peopleService;

    @GetMapping("list")
    @ApiOperation("数据集合")
    public JiebaoResponse list(QueryRequest queryRequest, String deptId, String LineId, Integer status,String km) {
        return peopleService.listPage(queryRequest, deptId, LineId, status,km);
    }

    @PostMapping("listExcel")
    @ApiOperation("数据集合导出")
    public JiebaoResponse listExcel(HttpServletResponse response, String deptId, String lineId, Integer status, String title,String km) {
        return peopleService.listExcel(response, deptId, lineId, status, title,km);
    }

    @PostMapping("saveOrUpdate")
    @ApiOperation("新增")
    public JiebaoResponse saveOrUpdate(People people,String qunId) {
        return peopleService.saveOrUpdateChile(people,qunId);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    public JiebaoResponse delete(String[] ids) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        return peopleService.removeByIds(Arrays.asList(ids)) ? jiebaoResponse.okMessage("删除成功") : jiebaoResponse.failMessage("删除失败");
    }

//    @GetMapping("lock")
//    @ApiOperation("上锁 解锁")
//    public JiebaoResponse lock(Integer status) {
//        return peopleService.lock(status);
//    }
//
//    @GetMapping("checkLock")
//    @ApiOperation("查看锁的状态")
//    public JiebaoResponse checkLock() {
//        return peopleService.checkLock();
//    }

    @GetMapping("confirm")
    @ApiOperation("变更查看 确认")
    public JiebaoResponse confirm(String[] ids, Integer status) {
        return peopleService.confirm(ids, status);
    }

}
