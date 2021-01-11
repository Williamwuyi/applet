package com.jiebao.platfrom.car.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.car.domain.Car;
import com.jiebao.platfrom.car.domain.CarRecord;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;

import java.util.Date;

public interface CarRecordService extends IService<CarRecord> {

    IPage<CarRecord> getCarRecordList(QueryRequest request, Integer state, boolean ziCha, String deptId, String order, Date startDate, Date endDate);

    JiebaoResponse add(CarRecord carRecord);

    JiebaoResponse update(String CarRecordId, Integer state); //0 未审核  1 审核通过 2审核未通过

    JiebaoResponse fina(String carRecordId);//用车完毕后 归还 统计

    CarRecord selectRecordId(String recordId);
}
