package com.jiebao.platfrom.car.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.car.domain.Car;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.demo.domain.Demo;

import java.util.List;

/**
 * 演示代码 - service
 *
 * @Author Sinliz
 * @Date 2020/3/12 10:00
 */

public interface CarService extends IService<Car> {

    IPage<Car> getCarList(QueryRequest request, Integer state,String order);  //分页查询   order 字符串  "asc"  "dsc"  降序或者和升序

    JiebaoResponse addOrUpdate(Car car);

    List<String>  userListByDeptId(String deptId);//通过部门id  查询 当前 部门 及子部门  相关人员
}
