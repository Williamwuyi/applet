package com.jiebao.platfrom.car.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.car.domain.CarMaintain;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;

import java.util.Date;
import java.util.List;

public interface CarMainService extends IService<CarMaintain> {
    JiebaoResponse add(CarMaintain carMaintain); //修改添加

    IPage<CarMaintain> getCarMaintainList(QueryRequest request, Integer state, String CarPlate, String deptId, boolean ziCha, Date startDate, Date endDate,String order);//分页查询  ziCha  为true查询当前登陆人相关信息

    JiebaoResponse update(String CarMainId ,Integer state); //修改  //维修单  赋予状态

    CarMaintain selectMaintainId(String maintainId);
}
