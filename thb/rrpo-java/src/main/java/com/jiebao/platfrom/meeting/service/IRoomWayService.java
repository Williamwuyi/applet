package com.jiebao.platfrom.meeting.service;

import com.jiebao.platfrom.meeting.daomain.RoomWay;
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
public interface IRoomWayService extends IService<RoomWay> {
    Boolean addLead(String id, List<Integer> LeadListId);

    Boolean deleteByListId( List<String> idList);

    List<RoomWay> list(String id);
}
