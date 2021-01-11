package com.jiebao.platfrom.car.controller;


import com.jiebao.platfrom.car.domain.Car;
import com.jiebao.platfrom.car.service.CarService;
import com.jiebao.platfrom.common.annotation.Log;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/car/car")
@Api(tags = "car-车辆信息")
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping(value = "addCar")
    @ApiOperation(value = "添加车辆信息")
    @Log("增加车辆")
    public JiebaoResponse saveOrUpdate(Car car) {
        return carService.addOrUpdate(car);
    }

    @DeleteMapping(value = "delete/{carId}")
    @Log("删除车辆信息")
    @ApiOperation(value = "删除车辆信息")
    public JiebaoResponse delete(String carId) {
        return new JiebaoResponse().message(carService.removeById(carId) ? "删除成功" : "删除失败");
    }

    @DeleteMapping(value = "delete/{carIds}")
    @Log("批量")
    @ApiOperation(value = "批量")
    public JiebaoResponse deleteList(@PathVariable String[] carIds) {
        return new JiebaoResponse().message(carService.removeByIds(Arrays.asList(carIds)) ? "删除成功" : "删除失败");
    }

    @GetMapping(value = "list")
    @ApiOperation(value = "分页查询车辆")
    @Log("分页查询车辆")
    public JiebaoResponse list(QueryRequest request, Integer state, String order) { //0正在使用  1车辆送修  2车辆空闲
        return new JiebaoResponse().data(carService.getCarList(request, state, order)).message("查询成功");
    }

    @GetMapping(value = "getCar")
    @ApiOperation("查询车辆")
    @Log(value = "查询用车辆")
    private JiebaoResponse getCar(String id) {
        return new JiebaoResponse().data(carService.getById(id)).message("查询成功");
    }
}
