package com.jiebao.platfrom.meeting.controller;


import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.meeting.service.IRoomWayService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qta
 * @since 2020-08-17
 */
@RestController
@RequestMapping("/meeting/room-way")
public class RoomWayController {
    @Autowired
    IRoomWayService wayService;

    @ApiOperation("绑定消息发送方式")
    @Log(value = "绑定消息发送方式")
    @PostMapping(value = "addLead")
    public JiebaoResponse addLead(String id, List<Integer> leadListId) {

        return new JiebaoResponse().message(wayService.addLead(id, leadListId) ? "添加成功" : "上传成功");
    }


    @GetMapping(value = "delete/{idList}")
    @ApiOperation("批量 绑定消息发送方式")
    @Log(value = "批量 绑定消息发送方式")
    public JiebaoResponse deleteById(@PathVariable String[] idList) {
        return new JiebaoResponse().message(wayService.deleteByListId(Arrays.asList(idList)) ? "操作成功" : "操作失败");
    }

    @GetMapping(value = "select")
    @ApiOperation("根据会议查询 绑定消息发送方式")
    @Log(value = "根据会议查询 绑定消息发送方式")
    public JiebaoResponse getFile(String id) {
        return new JiebaoResponse().data(wayService.list(id)).message("查询成功");
    }
}
