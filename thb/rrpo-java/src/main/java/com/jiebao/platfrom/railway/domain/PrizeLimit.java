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
 * 限制自定义次数
 *
 * @author yf
 */
@Data
@TableName("rail_prize_limit")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("限制自定义次数")
public class PrizeLimit {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    private Date creatTime;

    @ApiModelProperty(value = "组织机构ID")
    private String deptId;

    @ApiModelProperty(value = "限制次数",example = "1")
    private Integer limitNumber;

    @ApiModelProperty(value = "组织机构名")
    private String deptName;

}
