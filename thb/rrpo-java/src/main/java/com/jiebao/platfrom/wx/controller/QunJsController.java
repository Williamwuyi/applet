package com.jiebao.platfrom.wx.controller;


import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.wx.domain.QunJs;
import com.jiebao.platfrom.wx.service.IQunJsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-31
 */
@RestController
@RequestMapping("/wx/qun-js")
@Api(tags = "wx_群申请单")
public class QunJsController {
    @Autowired
    IQunJsService qunJsService;

    @PostMapping("saveOrUpdate")
    @ApiOperation("群 申请单添加修改")
    public JiebaoResponse saveOrUpdate(QunJs qunJs) {
        return qunJsService.addOrUpdate(qunJs);
    }

    @DeleteMapping("delete")
    @ApiOperation("群 申请单删除")
    public JiebaoResponse delete(String qunJsId) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        jiebaoResponse = qunJsService.removeById(qunJsId) ? jiebaoResponse.okMessage("操作成功") : jiebaoResponse.failMessage("操作失败");
        return jiebaoResponse;
    }


    @GetMapping("selectById")
    @ApiOperation("通过wxId查询对应的值")
    public JiebaoResponse selectById(String wXId) {
        return qunJsService.selectById(wXId);
    }
}
