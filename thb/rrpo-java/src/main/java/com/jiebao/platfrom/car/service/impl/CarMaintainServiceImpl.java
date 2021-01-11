package com.jiebao.platfrom.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.car.dao.CarMaintainMapper;
import com.jiebao.platfrom.car.domain.Car;
import com.jiebao.platfrom.car.domain.CarMaintain;
import com.jiebao.platfrom.car.service.CarMainService;
import com.jiebao.platfrom.car.service.CarService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class CarMaintainServiceImpl extends ServiceImpl<CarMaintainMapper, CarMaintain> implements CarMainService {


    @Autowired
    CarService carService;

    @Override
    public IPage<CarMaintain> getCarMaintainList(QueryRequest request, Integer state, String CarPlate, String deptId, boolean ziCha, Date startDate, Date endDate, String order) {  //参数状态   车牌号
        QueryWrapper<CarMaintain> carQueryWrapper = new QueryWrapper<>();
        if (order.equals("asc")) {
            carQueryWrapper.orderByAsc("maintain_create_time");
        } else {
            carQueryWrapper.orderByDesc("maintain_create_time");
        }
        if (state != null) {   //状态
            carQueryWrapper.eq("maintain_state", state);
        }
        if (CarPlate != null) {  //车牌
            QueryWrapper<Car> carQueryWrapper1 = new QueryWrapper<>();
            carQueryWrapper1.eq("car_plate", CarPlate);
            Car car = carService.getOne(carQueryWrapper1);  //通过车牌号获得对应的车辆
            System.out.println(car);
            carQueryWrapper.eq("maintain_car_id", car.getCarId());
        }
//        if (ziCha) {   //拆查询当前登陆人相关信息
//            User user = (User) SecurityUtils.getSubject().getPrincipal();  //当前登陆人
//            carQueryWrapper.eq("maintain_user_id", user.getUserId());
//        }
        if (startDate != null) {  //开始时间不问空时
            carQueryWrapper.ge("maintain_create_time", startDate);
        }
        if (endDate != null) {  //结束时间不问空时
            carQueryWrapper.le("maintain_create_time", endDate);
        }
        if (deptId != null) {   //更具部门查询
            List<String> userIdList = carService.userListByDeptId(deptId);
            carQueryWrapper.in("maintain_car_id", userIdList);
        }
        Page<CarMaintain> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.pageList(page, carQueryWrapper);
    }

    @Override
    public JiebaoResponse update(String CarMainId, Integer state) {
        CarMaintain carMaintain = getById(CarMainId);
        carMaintain.setMaintainState(state);
        if (state == 2 || state == 4) {  //当状态 改为审核不通过 或者  维修完成 修改汽车状态
            Car car = carService.getById(carMaintain.getMaintainCarId());
            car.setCarState(2);
            carService.updateById(car);
        }
        return new JiebaoResponse().message(updateById(carMaintain) ? "操作成功" : "操作失败");
    }

    @Override
    public CarMaintain selectMaintainId(String maintainId) {
        return this.selectMaintainId(maintainId);
    }

    @Override
    public JiebaoResponse add(CarMaintain carMaintain) {   //添加送修   修改车辆状态
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        Car car = carService.getById(carMaintain.getMaintainCarId());
        if (car.getCarState() != 2) {  //若已经送修  或者正在使用
            return jiebaoResponse.message("车辆已送修或者正在使用");
        }
        if (carMaintain.getMaintainId() == null) {  //添加时操作
//            carMaintain.setMaintainCreateTime(new Date());  //留着
//            User user = (User) SecurityUtils.getSubject().getPrincipal();
//            carMaintain.setMaintainUserId(user.getUserId());
            carMaintain.setMaintainState(0);
        }
        if (!saveOrUpdate(carMaintain)) {
            return jiebaoResponse.message("操作失败");
        }
        car.setCarState(1);//修改汽车状态
        return jiebaoResponse.message(carService.saveOrUpdate(car) ? "操作成功" : "操作失败");
    }
}
