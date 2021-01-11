package com.jiebao.platfrom.car.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("car_car")
public class Car extends Model<Car> implements Serializable {

    @TableId(value = "car_id", type = IdType.UUID)
    private String carId;
    private String carPlate;  //车牌
    private String carName;   //名字
    private Date carCreateTime;  //添加时间
    private Integer carState;  //0正在使用  1车辆送修  2车辆空闲




}
