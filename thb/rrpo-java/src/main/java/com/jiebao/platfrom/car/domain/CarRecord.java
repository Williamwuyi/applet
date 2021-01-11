package com.jiebao.platfrom.car.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.jiebao.platfrom.system.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@TableName("car_record")
public class CarRecord extends Model<CarRecord> implements Serializable {
    @TableId(value = "record_id", type = IdType.UUID)
    private String recordId;
    private String recordUserId;   //申请人
    @TableField(exist = false)
    private User user;
    private Date recordCreateTime;  //申请提交时间
    private Date recordDateStart;    //开始用车时间
    private Date recordDateEnd;   //用车结束时间
    private String recordCarId;      //申请所属车辆
    @TableField(exist = false)
    private Car car;
    private String recordBehoof;   //  车辆用途
    private Integer recordState;   //  0 未审核  1 审核通过 2审核未通过
}
