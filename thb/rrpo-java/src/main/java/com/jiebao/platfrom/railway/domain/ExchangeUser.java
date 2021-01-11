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
import java.util.HashMap;
import java.util.List;

/**
 * @author yf
 */
@Data
@TableName("rail_exchange_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("信息互递回复情况表")
public class ExchangeUser  {



    @TableId(value = "id")
    private int id;


    @ApiModelProperty(value = "和信息互递内容关联ID")
    private String exchangeId;


    @ApiModelProperty(value = "接收信息的用户ID")
    private String sendUserId;


    @ApiModelProperty(value = "发件时间")
    private Date creatTime;


    /**
     * 1收件，2回收 回收站只放收件箱的信息
     */
    @ApiModelProperty(value = "放在该用户的哪个箱：1收件，2回收")
    private String type;

    @ApiModelProperty(value = "阅读状态0、未查看 1、已阅读未回复 2、已阅读已回复",example = "1")
    private Integer isRead;

    @ExcelField(value = "组织机构")
    @TableField(exist = false)
    private String deptName;


    @ExcelField(value = "用户名")
    @ApiModelProperty(value = "发送给某用户姓名")
    private String sendUserName;


    @ExcelField(value = "回复意见")
    @ApiModelProperty(value = "回复意见")
    private String opinion;

    @ApiModelProperty(value = "接收时间")
    @ExcelField(value = "接收时间", writeConverter = TimeConverter.class)
    private Date receiveTime;


    @ExcelField(value = "回复时间", writeConverter = TimeConverter.class)
    @ApiModelProperty(value = "回复时间")
    private Date replyTime;


    @TableField(exist = false)
    private List IsReadList;
}
