package com.jiebao.platfrom.meeting.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomRecordSevice;
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
public interface IRoomRecordSeviceService extends IService<RoomRecordSevice> {
    Boolean addLead(String id, List<String> LeadListId);

    Boolean deleteByListId( List<String> idList);

    IPage<RoomRecordSevice> list(QueryRequest queryRequest, String id);
}
