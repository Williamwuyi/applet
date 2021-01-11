package com.jiebao.platfrom.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.car.dao.CarMapper;
import com.jiebao.platfrom.car.dao.CarRecordMapper;
import com.jiebao.platfrom.car.domain.Car;
import com.jiebao.platfrom.car.domain.CarMaintain;
import com.jiebao.platfrom.car.domain.CarRecord;
import com.jiebao.platfrom.car.service.CarRecordService;
import com.jiebao.platfrom.car.service.CarService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
public class CarRecordServiceImpl extends ServiceImpl<CarRecordMapper, CarRecord> implements CarRecordService {
    @Autowired
    CarService carService;


    @Override
    public IPage<CarRecord> getCarRecordList(QueryRequest request, Integer state, boolean ziCha, String deptId, String order, Date startDate, Date endDate) {   //多维度查询   1 //部门   2//状态 0 未审核  1 审核通过 2审核未通过   3//自查 查询当前登陆人相关信息
        QueryWrapper<CarRecord> queryWrapper = new QueryWrapper<>();
        if (order.equals("asc")) {
            queryWrapper.orderByAsc("record_create_time");
        } else {
            queryWrapper.orderByDesc("record_create_time");
        }
        if (state != null) {
            queryWrapper.eq("record_state", state);
        }
        //部门空缺
//        if (ziCha) {     //执行自查  就不执行 部门
//            User user = (User) SecurityUtils.getSubject().getPrincipal();
//            queryWrapper.eq("record_user_id", user.getUserId());
//        } else {
//
//        }
        if (startDate != null) {  //开始时间不问空时
            queryWrapper.ge("record_create_time", startDate);
        }
        if (endDate != null) {  //结束时间不问空时
            queryWrapper.le("record_create_time", endDate);
        }
        if (deptId != null) {  //部门查询
            List<String> userIdList = carService.userListByDeptId(deptId);
            queryWrapper.in("record_user_id", userIdList);
        }
        Page<CarRecord> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.pageList(page, queryWrapper);
    }


    @Override
    public JiebaoResponse add(CarRecord carRecord) {
        Car car = carService.getById(carRecord.getRecordCarId());
        if (car.getCarState() != 2) {    //判断是否可以使用
            return new JiebaoResponse().message("车辆送修或者正在使用");
        }
        if (carRecord.getRecordId() == null) {  //添加时操作
//            User user = (User) SecurityUtils.getSubject().getPrincipal();
//            carRecord.setRecordUserId(user.getUserId());
            carRecord.setRecordCreateTime(new Date());
            carRecord.setRecordState(0);
        }
        car.setCarState(0);//提交申请时汽车被锁定 正在使用  等待审批进一步确认
        carService.updateById(car);
        return new JiebaoResponse().message(save(carRecord) ? "用车申请成功" : "用车申请失败");
    }

    @Override
    public JiebaoResponse update(String carRecordId, Integer state) {  //
        CarRecord carRecord = getById(carRecordId);
        if (state == 1) {  //审核通过
            carRecord.setRecordDateStart(new Date());  //用车正式开始计数
            carRecord.setRecordState(1);
        }
        if (state == 2) {  //审核未通过  解禁  汽车锁定
            carRecord.setRecordState(2);
            Car car = carService.getById(carRecord.getRecordCarId());
            car.setCarState(2);//空闲
            carService.updateById(car);
        }
        return new JiebaoResponse().message(updateById(carRecord) ? "操作成功" : "操作失败");
    }

    @Override
    public JiebaoResponse fina(String carRecordId) {
        CarRecord carRecord = getById(carRecordId);
        carRecord.setRecordDateEnd(new Date());//用车结束时间
        Car car = carService.getById(carRecord.getRecordCarId());
        car.setCarState(2);//空闲
        carService.updateById(car);
        return new JiebaoResponse().message(updateById(carRecord) ? "操作成功" : "操作失败");
    }

    @Override
    public CarRecord selectRecordId(String recordId) {
        return this.baseMapper.selectRecordId(recordId);
    }


}
