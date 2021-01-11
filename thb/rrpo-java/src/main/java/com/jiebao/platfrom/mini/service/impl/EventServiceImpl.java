package com.jiebao.platfrom.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.mini.dao.EventMapper;
import com.jiebao.platfrom.mini.domian.Event;
import com.jiebao.platfrom.mini.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("eventService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {

}
