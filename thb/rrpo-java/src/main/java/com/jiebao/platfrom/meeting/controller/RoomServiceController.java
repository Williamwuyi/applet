package com.jiebao.platfrom.meeting.controller;


import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomService;
import com.jiebao.platfrom.meeting.service.IRoomServiceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-17
 */
@RestController
@RequestMapping("/meeting/room-service")
public class RoomServiceController {
    @Autowired
    IRoomServiceService serviceService;

    @PostMapping(value = "saveOrUpdate")
    @ApiOperation("服务的创建以及修改")
    @Log(value = "服务的创建以及修改")
    public JiebaoResponse saveOrUpdate(RoomService service) {
        return new JiebaoResponse().message(serviceService.saveOrUpdate(service) ? "操作成功" : "操作失败");
    }

    @DeleteMapping(value = "delete/{ids}")
    @ApiOperation("服务删除")
    @Log(value = "服务删除")
    public JiebaoResponse deleteById(String[] ids) {
        return new JiebaoResponse().message(serviceService.removeByIds(Arrays.asList(ids)) ? "操作成功" : "操作失败");
    }

    @GetMapping(value = "list")
    @ApiOperation("服务查询")
    @Log(value = "服务查询")
    public JiebaoResponse list(QueryRequest queryRequest, String name, String address, Integer small, Integer big) {
        return new JiebaoResponse().data(serviceService.list()).message("查询成功");
    }

    @GetMapping(value = "getService")
    @ApiOperation("查询指定服务")
    @Log(value = "查询指定服务")
    public JiebaoResponse getService(String id) {
        return new JiebaoResponse().data(serviceService.getById(id)).message("查询成功");
    }
}
