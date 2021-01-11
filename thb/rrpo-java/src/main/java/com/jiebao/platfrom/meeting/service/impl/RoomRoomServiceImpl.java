package com.jiebao.platfrom.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomRoom;
import com.jiebao.platfrom.meeting.dao.RoomRoomMapper;
import com.jiebao.platfrom.meeting.service.IRoomRoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-08-17
 */
@Service
public class RoomRoomServiceImpl extends ServiceImpl<RoomRoomMapper, RoomRoom> implements IRoomRoomService {

    @Override
    public IPage<RoomRoom> list(QueryRequest queryRequest, String name, String address, Integer small, Integer big) {
        QueryWrapper<RoomRoom> queryWrapper = new QueryWrapper<>();
        if (name != null) {
            queryWrapper.like("name", name);
        }
        if (address != null) {
            queryWrapper.like("address", address);
        }
        if (small != null) {
            queryWrapper.ge("people_num", small);
        }
        if (big != null) {
            queryWrapper.le("people_num", big);
        }
        Page<RoomRoom> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());


        return this.baseMapper.selectPage(page, queryWrapper);
    }

}
