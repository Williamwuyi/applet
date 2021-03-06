package com.jiebao.platfrom.railway.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuwenze.poi.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 一事一奖自定义天数
 *
 * @author yf
 */
@Data
@TableName("rail_prize_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("一事一奖自定义天数")
public class PrizeType {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "设定的天数")
    private String days;

    @ApiModelProperty(value = "创建时间")
    private Date creatTime;


}
