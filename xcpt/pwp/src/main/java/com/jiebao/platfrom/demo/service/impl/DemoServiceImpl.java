package com.jiebao.platfrom.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.demo.dao.DemoMapper;
import com.jiebao.platfrom.demo.domain.Demo;
import com.jiebao.platfrom.demo.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 演示代码 - service impl
 * @Author Sinliz
 * @Date 2020/3/12 10:00
 */

@Slf4j
@Service("demoService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements DemoService {

    /**
     * 分页查询示例
     * @param request
     * @return
     */
    @Override
    public IPage<Demo> getDemoList(QueryRequest request) {
        LambdaQueryWrapper<Demo> lambdaQueryWrapper = new LambdaQueryWrapper();
        Page<Demo> page = new Page<>(request.getPageNum(), request.getPageSize());
        lambdaQueryWrapper.orderByDesc(Demo::getId);
        return this.baseMapper.selectPage(page, lambdaQueryWrapper);
    }

}
