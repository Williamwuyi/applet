package com.jiebao.platfrom.meeting.controller;


import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomRecord;
import com.jiebao.platfrom.meeting.service.IRoomMessageService;
import com.jiebao.platfrom.meeting.service.IRoomRecordService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
@RequestMapping("/meeting/room-record")
public class RoomRecordController {
    @Autowired
    IRoomRecordService recordService;
    @Autowired
    IRoomMessageService messageService;

    @PostMapping(value = "saveOrUpdate")
    @ApiOperation("会议的创建以及修改")
    @Log(value = "会议的创建以及修改")
    public JiebaoResponse saveOrUpdate(RoomRecord record) {
        return new JiebaoResponse().message(recordService.saveOrUpdate(record) ? "操作成功" : "操作失败");
    }

    @DeleteMapping(value = "delete/{id}")
    @ApiOperation("会议删除")
    @Log(value = "会议删除")
    public JiebaoResponse deleteById(@PathVariable String id) {
        System.out.println(id);
        return new JiebaoResponse().message(recordService.removeById(id) ? "操作成功" : "操作失败");
    }

//    @GetMapping(value = "list")
//    @ApiOperation("会议查询")
//    @Log(value = "会议查询")
//    private JiebaoResponse list(QueryRequest queryRequest, String name, String address, Integer small, Integer big) {
//        return new JiebaoResponse().data(recordService.list(queryRequest, name, address, small, big)).message("查询成功");
//    }

    @GetMapping(value = "getRecord")
    @ApiOperation("查询指定会议")
    @Log(value = "查询指定会议")
    public JiebaoResponse getRecord(String id) {
        return new JiebaoResponse().data(recordService.getById(id)).message("查询成功");
    }

    @GetMapping("sendEmail")
    @ApiOperation("发送会议信息")
    @Log(value = "发送会议信息")
    public JiebaoResponse sendEmail(String recordId, String message, Integer inviteIf, List<String> listUserID) {
        return recordService.sendEmail(recordId, message, inviteIf, listUserID);
    }

    @GetMapping("getRecordByRoomIdOrDateOrUserId")
    @ApiOperation("通过会议室查询所属绑定会议")
    @Log("通过会议室查询所属绑定会议")
    public JiebaoResponse getRecordByRoomIdOrDateOrUserId(QueryRequest queryRequest, String roomId, Date date, String userId, String order) {
        return recordService.getRecordByRoomIdOrDateOrUserId(queryRequest, roomId, date, userId, order);
    }

}
