package com.jiebao.platfrom.railway.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.controller.BaseController;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.exception.JiebaoException;
import com.jiebao.platfrom.railway.dao.PrizeLimitMapper;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.PrizeLimit;
import com.jiebao.platfrom.railway.domain.PrizeType;
import com.jiebao.platfrom.railway.service.PrizeLimitService;
import com.jiebao.platfrom.railway.service.PrizeTypeService;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.service.DeptService;
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
@RequestMapping(value = "/prizeLimit")
@Api(tags = "railWay-一事一奖上报限制次数")   //swagger2 api文档说明示例
public class PrizeLimitController extends BaseController {


    private String message;

    @Autowired
    private PrizeLimitService prizeLimitService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private PrizeLimitMapper prizeLimitMapper;


    @PostMapping
    @Log("新增限制次数")
    @ApiOperation(value = "新增限制次数", notes = "新增限制次数", response = JiebaoResponse.class, httpMethod = "POST")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse addPrizeLimit(@Valid PrizeLimit prizeLimit) throws JiebaoException{
        try {
            Dept byId = deptService.getById(prizeLimit.getDeptId());
            prizeLimit.setDeptName(byId.getDeptName());
            prizeLimit.setDeptId(prizeLimit.getDeptId());
            Integer count = prizeLimitMapper.selectDeptId(prizeLimit.getDeptId());
            if (count == 0) {
                prizeLimitService.save(prizeLimit);
            }
            else {
                throw new JiebaoException("组织机构重复");
            }
        }
        catch (Exception e){
            throw new JiebaoException("新增失败");
        }
        return new JiebaoResponse().message("新增成功");
    }

    @DeleteMapping("/{ids}")
    @Log("删除限制次数")
    @ApiOperation(value = "批量删除限制次数", notes = "批量删除限制次数", response = JiebaoResponse.class, httpMethod = "DELETE")
    @Transactional(rollbackFor = Exception.class)
    public JiebaoResponse delete(@PathVariable String[] ids) throws JiebaoException {
        try {
            Arrays.stream(ids).forEach(id -> {
                prizeLimitService.removeById(id);
            });
        } catch (Exception e) {
            throw new JiebaoException("删除失败");
        }
        return new JiebaoResponse().message("删除成功");
    }

    @PutMapping
    @Log("修改限制次数")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "修改限制次数(只能改次数limitNumber)", notes = "修改限制次数(只能改次数limitNumber)", response = JiebaoResponse.class, httpMethod = "PUT")
    public void updatePrizeType(@Valid PrizeLimit prizeLimit) throws JiebaoException {
        try {
            this.prizeLimitService.updateById(prizeLimit);
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
    public JiebaoResponse getPrizeLimitListByMapper(QueryRequest request, PrizeLimit prizeLimit) {
        IPage<PrizeLimit> prizeLimitIPage = prizeLimitService.getPrizeLimitList(request, prizeLimit);
        return new JiebaoResponse().data(this.getDataTable(prizeLimitIPage));
    }
}
