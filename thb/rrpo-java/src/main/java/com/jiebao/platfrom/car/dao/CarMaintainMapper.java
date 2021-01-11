package com.jiebao.platfrom.car.dao;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.car.domain.Car;
import com.jiebao.platfrom.car.domain.CarMaintain;
import com.jiebao.platfrom.car.domain.CarRecord;
import com.jiebao.platfrom.check.domain.Num;
import com.jiebao.platfrom.railway.domain.Address;
import org.apache.ibatis.annotations.*;

public interface CarMaintainMapper extends BaseMapper<CarMaintain> {

    @Select("select * from car_maintain ${ew.customSqlSegment}")
    @Results({
            @Result(property = "user", column = "maintain_user_id", one = @One(select = "com.jiebao.platfrom.system.dao.UserMapper.selectById")),
            @Result(property = "car", column = "maintain_car_id", one = @One(select = "com.jiebao.platfrom.car.dao.CarMapper.selectById"))
    })
    IPage<CarMaintain> pageList(Page<CarMaintain> page, @Param("ew") QueryWrapper wrapper);

    @Select("select * from car_maintain where maintain_id=#{maintainId}")
    @Results({
            @Result(property = "user", column = "maintain_user_id", one = @One(select = "com.jiebao.platfrom.system.dao.UserMapper.selectById")),
            @Result(property = "car", column = "maintain_car_id", one = @One(select = "com.jiebao.platfrom.car.dao.CarMapper.selectById"))
    })
    CarMaintain selectMaintainId(String maintainId);

}
