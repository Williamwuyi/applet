package com.jiebao.platfrom.mini.controller;

import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/mini/dept")
@Api(tags = "小程序-组织机构")
public class MiniDeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 获取下级组织机构列表
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/getDeptList")
    @ApiOperation("查询下一级组织机构-[parentId为空查询14个市州]")
    public JiebaoResponse getDeptList(String parentId) {
        List<Dept> list;
        if (parentId.equals("0")) {
            list = deptService.getList(parentId);
        } else {
            list = deptService.getChildrenList(parentId);
        }
        return new JiebaoResponse().message("查询成功！").put("list", list).put("status", JiebaoConstant.STATUS_CODE_SUCCESS);
    }

}
