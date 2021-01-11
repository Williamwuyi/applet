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
 * 护路简报
 *
 * @author yf
 */
@Data
@TableName("rail_briefing")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("护路简报内容表")
public class Briefing {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ExcelField(value = "标题")
    private String title;

    @ExcelField(value = "内容")
    private String content;

    @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    private Date creatTime;

    @ExcelField(value = "创建人")
    private String creatUser;

    @ApiModelProperty(value = "状态：1未发送 2撤销的 3已发布",example = "1")
    private Integer status;


    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "是否关联年度考核" )
    private String isCheck;

    @ApiModelProperty(value = "是否阅读" ,example = "1")
    @TableField(exist = false)
    private Integer isRead;

    @TableField(exist = false)
    private List refIds;

    @TableField(exist = false)
    private String [] userId;

    @TableField(exist = false)
    private String [] userName;
    /**
     * 是否同步到门户网站
     */
    @ApiModelProperty(value = "是否同步到门户网站",example = "1")
    private Integer synchronizeWeb;

    /**
     *
     * 门户网（自填时间）
     */
    private Date time;

    /**
     * 门户网（自填来源）
     */
    private String source;

    /**
     * 门户网栏目ID
     */
    private String targetsId;

    @ApiModelProperty(value = "数量",example = "1")
    @TableField(exist = false)
    private Integer counts;

}
