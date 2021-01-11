package com.jiebao.platfrom.demo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.demo.domain.Demo;

/**
 * 演示代码 - service
 * @Author Sinliz
 * @Date 2020/3/12 10:00
 */

public interface DemoService extends IService<Demo>{

    IPage<Demo> getDemoList(QueryRequest request);

}
