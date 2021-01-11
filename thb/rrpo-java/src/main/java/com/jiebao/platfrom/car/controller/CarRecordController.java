package com.jiebao.platfrom.car.controller;

import com.jiebao.platfrom.car.domain.CarRecord;
import com.jiebao.platfrom.car.service.CarRecordService;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/car/CarRecord")
@Api(tags = "car-用车记录")
public class CarRecordController {

    @Autowired
    CarRecordService carRecordService;

    @PostMapping(value = "add")
    @ApiOperation(value = "用车记录表 ")  //记录开始申请时间    审批同意后  开始用车  修改汽车状态为正在使用   汽车使用完毕后 使用者提交结束使用  数据库存储使用时长
    //且修改汽车状态
    @Log("申请用车记录")
    public JiebaoResponse add(CarRecord carRecord) {
        return carRecordService.add(carRecord);
    }

    @PostMapping(value = "update")
    @ApiOperation(value = "审批触发 ")
    //且修改汽车状态
    @Log("审批触发")
    public JiebaoResponse update(String CarRecordId, Integer state) {
        return carRecordService.update(CarRecordId, state);
    }

    @PostMapping(value = "fina")
    @ApiOperation(value = "还车 ")
    //且修改汽车状态
    @Log("还车触发")
    public JiebaoResponse fina(String carRecordId) {
        return carRecordService.fina(carRecordId);
    }

    @PostMapping(value = "list")
    @ApiOperation(value = "用车记录多维度查询 ")
    //且修改汽车状态
    @Log("用车记录多维度查询")
    public JiebaoResponse list(QueryRequest request, Integer state, boolean ziCha, String deptId, Date startDate, Date endDate, String order) {
        return new JiebaoResponse().data(carRecordService.getCarRecordList(request, state, ziCha, deptId, order, startDate, endDate));
    }

    @GetMapping(value = "getCarRecord")
    @ApiOperation("查询用车记录")
    @Log(value = "查询用车记录")
    private JiebaoResponse getCarRecord(String id) {
        return new JiebaoResponse().data(carRecordService.selectRecordId(id)).message("查询成功");
    }
}
