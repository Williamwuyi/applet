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
 * 一事一奖自定义身份
 *
 * @author yf
 */
@Data
@TableName("rail_prize_identity")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("一事一奖自定义身份")
public class PrizeIdentity {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "身份名")
    private String identity;


    private Date creatTime;



}
