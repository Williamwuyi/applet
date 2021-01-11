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
 * 一事一奖限制次数
 *
 * @author yf
 */
@Data
@TableName("rail_prize_order")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("一事一奖限制次数")
public class PrizeOrder {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "发布的标题")
    private String title;


    private Date creatTime;

    @ApiModelProperty(value = "发布的组织机构ID")
    private String deptId;

    @ApiModelProperty(value = "发布的一事一奖ID")
    private String prizeId;


    @ApiModelProperty(value = "发布的次数",example = "1")
    private Integer releaseNumber;

    @ApiModelProperty(value = "限制次数（不同的组织机构可配置）",example = "1")
    private Integer limitNumber;

}
