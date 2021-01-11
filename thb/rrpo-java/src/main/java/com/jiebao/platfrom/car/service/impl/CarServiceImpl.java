package com.jiebao.platfrom.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.car.dao.CarMapper;
import com.jiebao.platfrom.car.domain.Car;
import com.jiebao.platfrom.car.service.CarService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.demo.domain.Demo;
import com.jiebao.platfrom.railway.dao.InformMapper;
import com.jiebao.platfrom.railway.domain.Inform;
import com.jiebao.platfrom.railway.service.InformService;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import com.jiebao.platfrom.system.service.DeptService;
import com.jiebao.platfrom.system.service.LogService;
import com.jiebao.platfrom.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service("CarService")
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements CarService {

    @Autowired
    DeptService deptService;
    @Autowired
    UserService userService;

    @Override
    public IPage<Car> getCarList(QueryRequest request, Integer state, String order) {
        QueryWrapper<Car> carQueryWrapper = new QueryWrapper<>();
        if (order.equals("asc")) {
            carQueryWrapper.orderByAsc("car_create_time");
        } else {
            carQueryWrapper.orderByDesc("car_create_time");
        }
        if (state != null) {
            carQueryWrapper.eq("car_state", state);
        }
        Page<Car> page = new Page<>(request.getPageNum(), request.getPageSize());
        return this.baseMapper.selectPage(page, carQueryWrapper);
    }

    @Override
    public JiebaoResponse addOrUpdate(Car car) {
        JiebaoResponse jiebaoResponse = new JiebaoResponse();
        QueryWrapper<Car> carQueryWrapper = new QueryWrapper<>();
        carQueryWrapper.eq("car_plate", car.getCarPlate());
        if (this.baseMapper.selectList(carQueryWrapper).size() != 0) {   //判断是否此车牌  已存在
            return jiebaoResponse.message("车牌重叠");
        }
        if (car.getCarId() == null) {  //添加时候操作
            car.setCarCreateTime(new Date());
            car.setCarState(2);
        }
        return jiebaoResponse.message(saveOrUpdate(car) ? "操作成功" : "操作失败");
    }

    @Override
    public List<String> userListByDeptId(String deptId) {  //传入 部门id  获得 当前  及 自己  所有部门id  查找对应得员工个人
        Dept dept = deptService.getById(deptId);
        List<String> deptIdList = new ArrayList<>();  //存储  最终结果
        List<String> deptIds = new ArrayList<>();  //初始参数
        deptIds.add(deptId);
        toDeptList(deptIds, deptIdList);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("dept_id", deptIdList);
        List<User> list = userService.list(queryWrapper);  //对应个人
        List<String> userIdList = new ArrayList<>();
        for (User user : list
        ) {
            userIdList.add(user.getId());
        }
        return userIdList;
    }


    private void toDeptList(List<String> deptIds, List<String> deptIdList) {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parent_id", deptIds);
        List<Dept> list = deptService.list(queryWrapper);  //获得所有得 子类子集
        if (list.size() != 0) {
            deptIds.clear();
            for (Dept dept : list
            ) {
                deptIdList.add(dept.getDeptId());  //储存
                deptIds.add(dept.getDeptId());
            }
            toDeptList(deptIds, deptIdList);
        }
    }
}
