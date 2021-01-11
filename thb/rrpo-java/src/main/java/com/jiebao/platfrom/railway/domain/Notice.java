package com.jiebao.platfrom.railway.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiebao.platfrom.common.converter.TimeConverter;
import com.jiebao.platfrom.system.domain.File;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 通知公告
 *
 * @author yf
 */
@Data
@TableName("rail_notice")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("通知公告")
public class Notice {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @TableField(exist = false)
    private String key;

    @ExcelField(value = "标题")
    private String title;

    @ExcelField(value = "内容")
    private String content;

    @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    private Date createTime;


    @ExcelField(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "状态：1未发送 2撤销的 3已发布 4已删除")
    private String status;

    /**
     *
     * 是否为站内通知
     */
    @ApiModelProperty(value = "是否为站内通知",example = "1")
    private Integer station;

    /**
     * 是否为小程序通知
     */
    @ApiModelProperty(value = "是否为小程序通知",example = "1")
    private Integer applets;

    /**
     * 是否同步到门户网站
     */
    @ApiModelProperty(value = "是否同步到门户网站",example = "1")
    private Integer synchronizeWeb;

    /**
     * 发布时间
     */
    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    private Integer isCheck;
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

    @TableField(exist = false)
    private File files;

    private String userId;

    private String deptId;

}
