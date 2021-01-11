package com.jiebao.platfrom.railway.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiebao.platfrom.common.converter.TimeConverter;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 信息互递自定义类别
 *
 * @author yf
 */
@Data
@TableName("rail_exchange_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("信息互递自定义类别")
public class ExchangeType {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "类别名")
    private String typeName;


    private Date creatTime;


}
