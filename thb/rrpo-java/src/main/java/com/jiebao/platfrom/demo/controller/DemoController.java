package com.jiebao.platfrom.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.common.exception.RedisConnectException;
import com.jiebao.platfrom.common.service.RedisService;
import com.jiebao.platfrom.demo.dao.DemoMapper;
import com.jiebao.platfrom.demo.domain.Demo;
import com.jiebao.platfrom.demo.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 演示代码 - controller
 *
 * @Author Sinliz
 * @Date 2020/3/11 16:03
 */

@RestController
@RequestMapping(value = "/demo")
@Api(tags = "演示代码接口")   //swagger2 api文档说明示例
public class DemoController extends BaseController {
    //BaseController 封装了 分页数据处理方法 getDataTable(IPage<?> pageInfo);
    //git 分支提交测试
    @Autowired
    private DemoMapper demoMapper;

    @Autowired
    private DemoService demoService;

    @Autowired
    private RedisService redisService;

    /**
     * 使用Mapper操作数据库示例
     * @return JiebaoResponse 标准返回数据类型
     */
    @PostMapping(value = "/getDemoListByMapper")    // TODO: 推荐使用PostMapping / getMapping
    @ApiOperation(value = "查询数据List", notes = "查询数据List列表", response = JiebaoResponse.class, httpMethod = "POST")
    //swagger2 api文档说明示例
    public JiebaoResponse getDemoListByMapper() {
        List<Demo> list = demoMapper.getDemoList();
        return new JiebaoResponse().data(list).message("查询成功").put("remake", "其他数据返回");
    }

    /**
     * 分页查询示例
     * @param request - 分页参数
     * @return
     */
    @PostMapping(value = "getDemoList")
    @ApiOperation(value = "分页查询", notes = "查询分页数据", response = JiebaoResponse.class, httpMethod = "POST")
    public JiebaoResponse getDemoList(QueryRequest request) {
        IPage<Demo> iPage = demoService.getDemoList(request);
        return new JiebaoResponse().data(this.getDataTable(iPage));
    }

    /**
     * 删除接口示例
     * @param ids
     * @return
     * @throws JiebaoException
     */
    @Log("代码示例-测试删除")   //日志
    @PostMapping(value = "/delete/{ids}")
    @Transactional  //TODO:事务管理
    public JiebaoResponse delete(@PathVariable String[] ids) throws JiebaoException {
        try {
            Arrays.stream(ids).forEach(id -> {
                demoService.removeById(id);
            });
        } catch (Exception e) {
            throw new JiebaoException("删除失败");
        }
        return new JiebaoResponse().message("删除成功");
    }

    /**
     * redis 使用示例
     * @throws JiebaoException
     */
    public void redisTest() throws JiebaoException {
        try {
            redisService.set("key", "value", 1L);
        } catch (RedisConnectException e) {
            throw new JiebaoException("redis连接异常");
        }
    }

}
