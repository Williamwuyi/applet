package com.jiebao.platfrom.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiebao.platfrom.meeting.daomain.RoomWay;
import com.jiebao.platfrom.meeting.dao.RoomWayMapper;
import com.jiebao.platfrom.meeting.service.IRoomWayService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class RoomWayServiceImpl extends ServiceImpl<RoomWayMapper, RoomWay> implements IRoomWayService {
    @Override
    public Boolean addLead(String id, List<Integer> LeadListId) {  //绑定 会议 消息发送方式   提醒方式  0  邮件  1 短信 2 及时信息
        List<RoomWay> list = new ArrayList<>();  //储存对象
        QueryWrapper<RoomWay> queryWrapper = null;
        for (Integer wid : LeadListId
        ) {
            RoomWay way = new RoomWay();
            way.setRoomReordId(id);
            way.setWay(wid);
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_reord_id", id);
            queryWrapper.eq("way", wid);
            if (this.baseMapper.selectOne(queryWrapper) == null) { //本对象在数据库不存在执行添加
                list.add(way);
            }
        }
        return saveBatch(list);
    }

    @Override
    public Boolean deleteByListId(List<String> idList) {
        return removeByIds(idList);
    }


    @Override
    public List<RoomWay> list(String id) {
        QueryWrapper<RoomWay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_reord_id", id);
        return list(queryWrapper);
    }
}
