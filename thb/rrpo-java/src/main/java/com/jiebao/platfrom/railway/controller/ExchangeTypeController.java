package com.jiebao.platfrom.railway.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.dao.ExchangeMapper;
import com.jiebao.platfrom.railway.dao.ExchangeTypeMapper;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.ExchangeFile;
import com.jiebao.platfrom.railway.domain.ExchangeType;
import com.jiebao.platfrom.railway.domain.Inform;
import com.jiebao.platfrom.railway.service.ExchangeFileService;
import com.jiebao.platfrom.railway.service.ExchangeService;
import com.jiebao.platfrom.railway.service.ExchangeTypeService;
import com.jiebao.platfrom.railway.service.ExchangeUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.Arrays;
import java.util.List;


/**
 * @author yf
 */
@Slf4j
@RestController
@RequestMapping(value = "/exchangeType")
@Api(tags = "railWay-信息互递自定义类别")   //swagger2 api文档说明示例
public class ExchangeTypeController extends BaseController {


    private String message;

    @Autowired
    private ExchangeTypeService exchangeTypeService;


    @PostMapping
    @Log("新增类别")
    @ApiOperation(value = "新增类别", notes = "新增类别", response = JiebaoResponse.class, httpMethod = "POST")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse addExchangeType(@Valid ExchangeType exchangeType) {
        exchangeTypeService.save(exchangeType);
        return new JiebaoResponse().message("成功");
    }

    @DeleteMapping("/{ids}")
    @Log("删除类别")
    @ApiOperation(value = "批量删除类别", notes = "批量删除类别", response = JiebaoResponse.class, httpMethod = "DELETE")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse delete(@PathVariable String[] ids) throws JiebaoException {
        try {
            Arrays.stream(ids).forEach(id -> {
                exchangeTypeService.removeById(id);
            });
        } catch (Exception e) {
            throw new JiebaoException("删除失败");
        }
        return new JiebaoResponse().message("删除成功");
    }

    @PutMapping
    @Log("修改类别")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "修改类别", notes = "修改类别", response = JiebaoResponse.class, httpMethod = "PUT")
    public void updateExchangeType(@Valid ExchangeType exchangeType) throws JiebaoException {
        try {
            this.exchangeTypeService.updateById(exchangeType);
        } catch (Exception e) {
            message = "修改失败";
            log.error(message, e);
            throw new JiebaoException(message);
        }
    }


    /**
     * 使用Mapper操作数据库
     *
     * @return JiebaoResponse 标准返回数据类型
     */
    @GetMapping(value = "/getInformListByMapper")
    @ApiOperation(value = "查询数据List", notes = "查询数据List列表", response = JiebaoResponse.class, httpMethod = "GET")
    public List<ExchangeType> getExchangeTypeListByMapper() {
        List<ExchangeType> list = exchangeTypeService.list();
        return list;
    }
}
