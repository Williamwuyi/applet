package com.jiebao.platfrom.railway.controller;


import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.domain.PrizeType;
import com.jiebao.platfrom.railway.service.PrizeTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;


/**
 * @author yf
 */
@Slf4j
@RestController
@RequestMapping(value = "/prizeType")
@Api(tags = "railWay-一事一奖自定义天数")   //swagger2 api文档说明示例
public class PrizeTypeController extends BaseController {


    private String message;

    @Autowired
    private PrizeTypeService prizeTypeService;


    @PostMapping
    @Log("新增天数")
    @ApiOperation(value = "新增天数", notes = "新增天数", response = JiebaoResponse.class, httpMethod = "POST")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse addPrizeType(@Valid PrizeType prizeType) {
        prizeTypeService.save(prizeType);
        return new JiebaoResponse().message("成功");
    }

    @DeleteMapping("/{ids}")
    @Log("删除天数")
    @ApiOperation(value = "批量删除天数", notes = "批量删除天数", response = JiebaoResponse.class, httpMethod = "DELETE")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse delete(@PathVariable String[] ids) throws JiebaoException {
        try {
            Arrays.stream(ids).forEach(id -> {
                prizeTypeService.removeById(id);
            });
        } catch (Exception e) {
            throw new JiebaoException("删除失败");
        }
        return new JiebaoResponse().message("删除成功");
    }

    @PutMapping
    @Log("修改天数")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "修改天数", notes = "修改天数", response = JiebaoResponse.class, httpMethod = "PUT")
    public void updatePrizeType(@Valid PrizeType prizeType) throws JiebaoException {
        try {
            this.prizeTypeService.updateById(prizeType);
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
    @GetMapping(value = "/getList")
    @ApiOperation(value = "查询数据List", notes = "查询数据List列表", response = JiebaoResponse.class, httpMethod = "GET")
    public JiebaoResponse getPrizeTypeListByMapper() {
        List<PrizeType> list = prizeTypeService.list();
        return new JiebaoResponse().data(list).message("查询成功").put("remake", "其他数据返回");
    }
}
