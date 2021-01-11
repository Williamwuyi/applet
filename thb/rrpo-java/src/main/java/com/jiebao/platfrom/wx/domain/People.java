package com.jiebao.platfrom.wx.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.jiebao.platfrom.common.utils.ExcelName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author qta
 * @since 2020-10-16
 */
@ExcelName(name = "护路队员导出")   //必写
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wx_people")
public class People implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 护路id
     */
    @TableId(value = "hl_id", type = IdType.UUID)
    private String hlId;

    @ApiModelProperty(value = "添加对象专属id")
    @TableField(exist = false)
    private String deptId;
    /**
     * 市名称
     */
    private String shi;

    @ExcelName(name = "市州")
    @TableField(exist = false)
    private String shiName;

    /**
     * 区县名称
     */

    private String quXian;
    @ExcelName(name = "区县")
    @TableField(exist = false)
    private String quXianName;
    /**
     * 乡镇
     */
    private String xiang;
    @TableField(exist = false)
    @ExcelName(name = "乡镇界")
    private String xiangName;

    /**
     * 队员属性
     */
    @ExcelName(name = "队员属性")
    private String proper;

    /**
     * 线路
     */

    private String line;
    @ExcelName(name = "线路")
    @TableField(exist = false)
    @ApiModelProperty(value = "不用管 展示用的", hidden = true)
    private String lineName;

    @TableField(exist = false)
    @ApiModelProperty(value = "公里数", hidden = true, example = "12.25")
    private Double lineRevenue;

    @TableField(exist = false)
    @ApiModelProperty(value = "线路性质", hidden = true)
    private String lineNature;


    /**
     * 名字
     */
    @ExcelName(name = "名字")
    private String name;

    /**
     * sex
     */
    @ExcelName(name = "sex")
    private String sex;

    /**
     * 年龄
     */
    @ExcelName(name = "年龄")
    private Integer age;

    /**
     * 政治面貌
     */
    @ExcelName(name = "政治面貌")
    private String face;

    /**
     * 家庭地址
     */
    @ExcelName(name = "家庭地址")
    private String address;

    /**
     * 身份证号码
     */
    @ExcelName(name = "身份证号码")
    private String idCard;

    /**
     * 路段——责任
     */
    @ExcelName(name = "责任路段")
    private String luDuan;

    private Integer luNumber;  //编号

    @TableField(exist = false)
    private String lu1;   //编号  K
    @TableField(exist = false)
    private Integer lu2;  // 163
    @TableField(exist = false)
    private Integer lu3;  //多少米处
    @TableField(exist = false)
    private Integer lu4;//164
    @TableField(exist = false)
    private Integer lu5;//多少米处
    //最后组合  k163+500-k164+300
    /**
     * 电话号码
     */
    @ExcelName(name = "电话号码")
    private String phone;

    /**
     * 是否使用微信
     */
    @ExcelName(name = "是否使用微信")
    private String isWx;

    /**
     * 是否加入群
     */
    private String isQun;

    /**
     * 派出所
     */
    @ExcelName(name = "派出所")
    private String police;

    /**
     * 0 正常   1此信息是新增的  2 此信息是要删除的
     */
    @ApiModelProperty(value = "0代表信息正常  1 刚是新增的  2 刚修改的  3是刚删除  4真正执行逻辑删除以后不再用", example = "2")
    private Integer status;

    private Date creatTime;

    @ApiModelProperty(value = "km数")
    private String km;


}
