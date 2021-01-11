package com.jiebao.platfrom.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomMessage;
import com.jiebao.platfrom.meeting.dao.RoomMessageMapper;
import com.jiebao.platfrom.meeting.daomain.RoomUsers;
import com.jiebao.platfrom.meeting.service.IRoomMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.meeting.service.IRoomUsersService;
import com.jiebao.platfrom.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-17
 */
@Service
public class RoomMessageServiceImpl extends ServiceImpl<RoomMessageMapper, RoomMessage> implements IRoomMessageService {
    @Autowired
    IRoomUsersService usersService;

    @Override
    public JiebaoResponse addList(String recordId, List<String> idList, String message, Integer inviteIf) {
        List<RoomMessage> messageList = new ArrayList<>();
        for (String userId : idList
        ) {
            RoomMessage message1 = new RoomMessage();  //信息对象
            message1.setUserId(userId);
            message1.setDate(new Date());
            message1.setRocordId(recordId);
            message1.setContent(message);
            message1.setReadIf(0);
            message1.setStatus(null);
            message1.setInviteIf(inviteIf);
            messageList.add(message1);
        }
        if (inviteIf == 1) {  //解除会议执行   删除绑定人员
            QueryWrapper<RoomUsers> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("meeting_id", recordId);
            queryWrapper.in("user_id", idList);
            usersService.getBaseMapper().delete(queryWrapper);
        }
        return new JiebaoResponse().data(saveBatch(messageList)).message("通知发送完毕");
    }

    @Override
    public JiebaoResponse readIf(String id, Integer read_if) {
        RoomMessage message = getById(id);
        message.setReadIf(read_if);
        return new JiebaoResponse().message(updateById(message) ? "操作成功" : "操作失败");
    }

    @Override
    public JiebaoResponse receipt(String messageId, Integer status, String takeUserId) {  //回执    代替参会人员
        RoomMessage message = getById(messageId);
        message.setStatus(status);
        message.setUserId(takeUserId);
        User recordUser = (User) SecurityUtils.getSubject().getPrincipal();
        QueryWrapper<RoomUsers> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("meeting_id", message.getRocordId());
        queryWrapper.eq("user_id", message.getUserId());
        RoomUsers users = usersService.getBaseMapper().selectOne(queryWrapper);//获得对应的会议  人员挂钩
        users.setStatus(status);
        users.setTakeUserId(takeUserId);//  修改会议代办人
        if (!usersService.updateById(users)) {
            return new JiebaoResponse().message("回执失败");
        }
        return new JiebaoResponse().message(updateById(message) ? "回执成功" : "回执成功");
    }


    @Override
    public JiebaoResponse selectByRecordAndUserId(QueryRequest queryRequest, String RecordId, String userId, Integer readIf, Integer status, String order) {
        QueryWrapper<RoomMessage> queryWrapper = new QueryWrapper<>();
        if (order.equals("asc")) {
            queryWrapper.orderByAsc("date");
        } else {
            queryWrapper.orderByDesc("date");
        }
        if (RecordId != null) {
            queryWrapper.eq("rocord_id", RecordId);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (readIf != null) {
            queryWrapper.eq("read-if", readIf);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        Page<RoomMessage> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return new JiebaoResponse().data(this.baseMapper.selectPage(page, queryWrapper)).message("查询成功");
    }
}
