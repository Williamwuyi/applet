package com.jiebao.platfrom.meeting.service;

import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomMessage;
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
public interface IRoomMessageService extends IService<RoomMessage> {
    JiebaoResponse addList(String recordId, List<String> idList, String message, Integer inviteIf);   //  会议通知信息      会议id    关系人  内容   会议邀请/会议解除

    JiebaoResponse readIf(String messageId, Integer read_if);//是否已读  0未读  1 已读

    JiebaoResponse receipt(String messageId,Integer status, String takeUserId);//回执   是否参会回执     确定会议   //0  不参会  1 参会    takeUser参会人员

    JiebaoResponse selectByRecordAndUserId(QueryRequest queryRequest, String RecordId, String userId, Integer readIf, Integer status, String order);   // 通过 会议  人   搜索对应信息         参数    是否已读是否已读  0未读  1 已读     参会否 0  不参会  1 参会

}
