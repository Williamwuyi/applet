package com.jiebao.platfrom.meeting.controller;


import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.service.IRoomUsersService;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
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
@RequestMapping("/meeting/room-users")
public class RoomUsersController {
    @Autowired
    IRoomUsersService usersService;

    @ApiOperation("创建会议参会人员")
    @Log(value = "创建会议参会人员")
    @PostMapping(value = "addLead")
    public JiebaoResponse addLead(String id, List<String> leadListId) {
        return usersService.addUSer(id, leadListId);
    }


    @Delete(value = "delete/{idList}")
    @ApiOperation("批量 参会人员")
    @Log(value = "领导删除")
    public JiebaoResponse deleteById(@PathVariable String[] idList) {
        return new JiebaoResponse().message(usersService.deleteByListId(Arrays.asList(idList)) ? "操作成功" : "操作失败");
    }

    @GetMapping(value = "select")
    @ApiOperation("根据会议查询 参会人员")
    @Log(value = "根据会议查询 参会人员")
    public JiebaoResponse getFile(String id, QueryRequest queryRequest, Integer leadIf) {
        return new JiebaoResponse().data(usersService.list(queryRequest,id,leadIf)).message("查询成功");
    }
}
