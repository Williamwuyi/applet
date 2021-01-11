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
 * 信息互递自定义分组
 *
 * @author yf
 */
@Data
@TableName("rail_exchange_group")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("信息互递自定义分组")
public class ExchangeGroup {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "分组名")
    private String name;


    private Date creatTime;

    private String userId;


}
