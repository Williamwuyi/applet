package com.jiebao.platfrom.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.wx.dao.NoticesMapper;
import com.jiebao.platfrom.wx.domain.Notice;
import com.jiebao.platfrom.wx.service.INoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qta
 * @since 2020-09-18
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticesMapper, Notice> implements INoticeService {
    @Override
    public boolean save(Notice entity) {
        entity.setDate(LocalDateTime.now());
        return super.save(entity);
    }

    @Override

    public JiebaoResponse List(QueryRequest queryRequest) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("date");
        Page<Notice> page = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        return new JiebaoResponse().data(page(page, queryWrapper));
    }

}
