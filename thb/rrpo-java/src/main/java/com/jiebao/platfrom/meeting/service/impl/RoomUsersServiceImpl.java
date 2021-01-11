package com.jiebao.platfrom.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomRecord;
import com.jiebao.platfrom.meeting.daomain.RoomRoom;
import com.jiebao.platfrom.meeting.daomain.RoomUsers;
import com.jiebao.platfrom.meeting.dao.RoomUsersMapper;
import com.jiebao.platfrom.meeting.service.IRoomRecordService;
import com.jiebao.platfrom.meeting.service.IRoomRoomService;
import com.jiebao.platfrom.meeting.service.IRoomUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class RoomUsersServiceImpl extends ServiceImpl<RoomUsersMapper, RoomUsers> implements IRoomUsersService {
    @Autowired
    IRoomRecordService recordService;
    @Autowired
    IRoomRoomService roomService;

    @Override
    public JiebaoResponse addUSer(String id, List<String> userListId) {  //绑定 会议 人员
        int size = listByRecordId(id, null).size();  //会议现阶段邀请人数
        RoomRecord record = recordService.getById(id);
        RoomRoom room = roomService.getById(record.getRoomId());  //得到对应的会议室
        if (size + userListId.size() > room.getPeopleNum()) {  //当 绑定 已经已存在人数 大于最大容纳人数时
            return new JiebaoResponse().message("超出最大容纳人数" + (size + userListId.size() - room.getPeopleNum()));
        }
        List<RoomUsers> list = new ArrayList<>();  //储存对象
        QueryWrapper<RoomUsers> queryWrapper = null;
        for (String userId : userListId
        ) {
            RoomUsers Users = new RoomUsers();
            Users.setMeetingId(id);
            Users.setRoomUserId(userId);
            Users.setStatus(null);
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("meeting_id", id);
            queryWrapper.eq("user_id", userId);
            if (this.baseMapper.selectOne(queryWrapper) == null) { //本对象在数据库不存在执行添加
                list.add(Users);
            }
        }
        return new JiebaoResponse().message(saveBatch(list) ? "添加成员成功" : "添加成员失败");
    }

    @Override
    public Boolean deleteByListId(List<String> idList) {
        return removeByIds(idList);
    }


    @Override
    public IPage<RoomUsers> list(QueryRequest queryRequest, String id, Integer leadIf) {
        QueryWrapper<RoomUsers> queryWrapper = new QueryWrapper<>();
        if (id != null) {
            queryWrapper.eq("metting_id", id);
        }
        if (leadIf != null) {
            queryWrapper.eq("lead_if", id);
        }
        Page<RoomUsers> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    public List<RoomUsers> listByRecordId(String id, Integer leadIf) {
        QueryWrapper<RoomUsers> queryWrapper = new QueryWrapper<>();
        if (id != null) {
            queryWrapper.eq("metting_id", id);
        }
        if (leadIf != null) {
            queryWrapper.eq("lead_if", id);
        }
        return list(queryWrapper);
    }
}
