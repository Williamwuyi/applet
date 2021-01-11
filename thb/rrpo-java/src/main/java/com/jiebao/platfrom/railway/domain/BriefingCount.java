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
import java.util.List;

/**
 * @author yf
 */
@Data
@TableName("rail_briefing_count")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("护路简报市州统计")
public class BriefingCount {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ExcelField(value = "和护路简报内容关联ID")
    @ApiModelProperty(value = "和护路简报内容关联ID")
    private String briefingId;

    @ApiModelProperty(value = "市州ID")
    private String deptId;

    @ApiModelProperty(value = "统计")
    private Integer count;

    @ApiModelProperty(value = "时间")
    private Date creatTime;

}
