package com.jiebao.platfrom.meeting.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomRoom;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qta
 * @since 2020-08-17
 */
public interface IRoomRoomService extends IService<RoomRoom> {
    IPage<RoomRoom> list(QueryRequest queryRequest, String name, String address, Integer small, Integer big);  // 姓名模糊查询 地址模糊查询   small会议室容纳最小值  big 会议室最大值
}
