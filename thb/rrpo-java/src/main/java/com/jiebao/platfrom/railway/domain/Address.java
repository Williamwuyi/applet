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
 * 通讯录
 *
 * @author yf
 */
@Data
@TableName("rail_address")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("通讯录")
public class Address {

    @TableId(value = "id", type = IdType.UUID)
    private String id;


    @ApiModelProperty(value = "微信")
    private String weiXin;


    // @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    @ApiModelProperty(value = "创建时间")
    private Date creatTime;

    @ApiModelProperty(value = "和user关联ID")
    private String userId;

    /**
     * 状态1正常
     */
    @ApiModelProperty(value = "状态1是创建的，2为导入的", example = "1")
    private Integer status;

    @ApiModelProperty(value = "和组织机构关联ID")
    private String deptId;

    @ExcelField(value = "名称")
    @ApiModelProperty(value = "名称")
    private String userName;


    @ExcelField(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "组织机构名")
    @TableField(exist = false)
    private String deptName;

    @ExcelField(value = "护路职务")
    @ApiModelProperty(value = "护路职务")
    private String position;

    @ExcelField(value = "职务")
    @ApiModelProperty(value = "职务")
    private String newPosition;


    @ExcelField(value = "电话")
    @ApiModelProperty(value = "电话")
    private String phone;

    @ExcelField(value = "手机")
    @ApiModelProperty(value = "手机")
    private String telPhone;

    @ExcelField(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "所处的市级ID")
    private String deptRoot;

    @ApiModelProperty(value = "护路职务Id")
    @TableField(exist = false)
    private String positionId;




}
