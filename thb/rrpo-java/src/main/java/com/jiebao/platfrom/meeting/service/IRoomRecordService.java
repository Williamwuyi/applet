package com.jiebao.platfrom.meeting.service;

import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.meeting.daomain.RoomRoom;


import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qta
 * @since 2020-08-17
 */
public interface IRoomRecordService extends IService<RoomRecord> {
    JiebaoResponse addOrUpdate(RoomRecord record);

    JiebaoResponse selectByMy(QueryRequest request, String userId, String order);//得到 传入userId  查询 对象创建的会议记录   不传 查询当前登陆人  asc dsc

    JiebaoResponse sendEmail(String recordId, String message, Integer inviteIf, List<String> listUserID);

    JiebaoResponse getRecordByRoomIdOrDateOrUserId(QueryRequest queryRequest, String roomId, Date date, String userId, String order);//  通过 会议室  时间日期    已经创建人   查询 会议   "asc" "dsc"


}
