package com.jiebao.platfrom.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.meeting.daomain.RoomRecordSevice;
import com.jiebao.platfrom.meeting.dao.RoomRecordSeviceMapper;
import com.jiebao.platfrom.meeting.service.IRoomRecordSeviceService;
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
public class RoomRecordSeviceServiceImpl extends ServiceImpl<RoomRecordSeviceMapper, RoomRecordSevice> implements IRoomRecordSeviceService {
    @Override
    public Boolean addLead(String id, List<String> LeadListId) {  //绑定 会议 人员
        List<RoomRecordSevice> list = new ArrayList<>();  //储存对象
        QueryWrapper<RoomRecordSevice> queryWrapper = null;
        for (String sid : LeadListId) {  //服务项

            RoomRecordSevice recordSevice = new RoomRecordSevice();
            recordSevice.setRoomRecordId(id);
            recordSevice.setRoomServiceId(sid);
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_record_id", id);
            queryWrapper.eq("room_file_id", sid);
            if (this.baseMapper.selectOne(queryWrapper) == null) { //本对象在数据库不存在执行添加
                list.add(recordSevice);
            }
        }
        return saveBatch(list);
    }


    @Override
    public Boolean deleteByListId(List<String> idList) {
        return removeByIds(idList);
    }


    @Override
    public IPage<RoomRecordSevice> list(QueryRequest queryRequest, String id) {
        QueryWrapper<RoomRecordSevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_record_id", id);
        Page<RoomRecordSevice> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return this.baseMapper.selectPage(page, queryWrapper);
    }
}
