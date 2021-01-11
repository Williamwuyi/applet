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
import java.util.Date;

@Data
@TableName("car_maintain")
public class CarMaintain extends Model<CarMaintain> implements Serializable {

    @TableId(value = "maintain_id", type = IdType.UUID)
    private String maintainId;
    private String maintainCarId;   //对应车辆id
    @TableField(exist = false)
    private Car car;
    private Date maintainCreateTime;  //申请提交时间
    private Integer maintainState;    //状态    0 未审核  1审核通过 2审核不通过 3 维修正在执行  4 维修完成
    private String maintainUserId;   //申请人
    @TableField(exist = false)
    private User user;
    private String maintainCause;      //维修原因
}
