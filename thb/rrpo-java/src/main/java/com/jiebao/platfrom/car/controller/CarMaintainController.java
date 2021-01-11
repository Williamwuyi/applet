package com.jiebao.platfrom.car.controller;


import com.jiebao.platfrom.car.domain.CarMaintain;
import com.jiebao.platfrom.car.service.CarMainService;
import com.jiebao.platfrom.car.service.CarRecordService;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/car/CarMain")
@Api(tags = "car-车辆维修记录")
public class CarMaintainController {
    //欠缺  审计部分
    @Autowired
    CarMainService carMainService;
    @Autowired
    CarRecordService recordService;

    @PostMapping(value = "addCarMaintain")
    @ApiOperation(value = "添加车辆送修")
    @Log("添加车辆送修")   //可对送修记录状态更改
    public JiebaoResponse save(CarMaintain carMaintain) {
        return carMainService.add(carMaintain);
    }

//    @DeleteMapping(value = "delete/{id}")
//    @Log("删除车辆送修信息")
//    public JiebaoResponse delete(Long Id) {
//        return new JiebaoResponse().message(carMainService.removeById(Id) ? "删除成功" : "删除失败");
//    }

    @GetMapping(value = "list")
    @Log("分页查询车辆送修")
    @ApiOperation(value = "分页查询车辆送修")
    public JiebaoResponse list(QueryRequest request, Integer state, String CarPlate, String deptId, boolean ziCha, Date startDate, Date endDate, String order) { //状态 查询  车牌号直接查询   后面2个参数 开始时间  结束时间区间查询
        return new JiebaoResponse().data(carMainService.getCarMaintainList(request, state, CarPlate, deptId, ziCha, startDate, endDate, order)).message("查询成功");
    }

    @GetMapping(value = "update")
    @Log("车辆送修 审核  维修完成后触发")
    @ApiOperation(value = "车辆送修 审核  维修完成后触发")
    public JiebaoResponse update(String CarMainId, Integer state) { //状态 查询  车牌号直接查询  状态   0 未审核  1审核通过 2审核不通过 3 维修正在执行  4 维修完成
        return carMainService.update(CarMainId, state);
    }

    @GetMapping(value = "getCarMaintain")
    @ApiOperation("查询车辆送修")
    @Log(value = "查询用车辆送修")
    private JiebaoResponse getCarMaintain(String id) {
        return new JiebaoResponse().data(carMainService.selectMaintainId(id)).message("查询成功");
    }
}
