package com.jiebao.platfrom.meeting.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomUsers;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qta
 * @since 2020-08-17
 */
public interface IRoomUsersService extends IService<RoomUsers> {
    JiebaoResponse addUSer(String id, List<String> userId);  //绑定人员

    Boolean deleteByListId( List<String> idList);  //批量删除人员

    IPage<RoomUsers> list(QueryRequest queryRequest, String id, Integer leadIf); //  通过会议 id  是否领导查询绑定人员  分页

    List<RoomUsers> listByRecordId(String id,Integer leadIf); //  通过会议 id  是否领导查询绑定人员  不分页
}
