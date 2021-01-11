package com.jiebao.platfrom.car.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.car.domain.CarRecord;
import com.jiebao.platfrom.check.domain.Num;
import com.jiebao.platfrom.railway.domain.Address;
import org.apache.ibatis.annotations.*;

public interface CarRecordMapper extends BaseMapper<CarRecord> {
    @Select("select * from car_record ${ew.customSqlSegment}")
    @Results({
            @Result(property = "user", column = "record_user_id", one = @One(select = "com.jiebao.platfrom.system.dao.UserMapper.selectById")),
            @Result(property = "car", column = "record_car_id", one = @One(select = "com.jiebao.platfrom.car.dao.CarMapper.selectById"))
    })
    IPage<CarRecord> pageList(Page<CarRecord> page, @Param("ew") QueryWrapper wrapper);

    @Select("select * from car_record where record_id=#{recordId}")
    @Results({
            @Result(property = "user", column = "record_user_id", one = @One(select = "com.jiebao.platfrom.system.dao.UserMapper.selectById")),
            @Result(property = "car", column = "record_car_id", one = @One(select = "com.jiebao.platfrom.car.dao.CarMapper.selectById"))
    })
    CarRecord selectRecordId(String recordId);
}