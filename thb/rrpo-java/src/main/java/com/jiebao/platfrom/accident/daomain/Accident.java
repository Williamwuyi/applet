package com.jiebao.platfrom.accident.daomain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.Dict;
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
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("accident_accident")
public class Accident implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事故记录id
     */
    @TableId(value = "accident_id", type = IdType.UUID)
    private String accidentId;


    /**
     * 事故发生时间
     */
    @ApiModelProperty(value = "事故发生时间", example = "2020/04/03 12:15:12")
    private String date;

    /**
     * 发生城市
     */
    @ApiModelProperty(value = "发生城市", example = "哈哈")
    private String cityCsId;

    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private Dept deptCs;

    /**
     * 发生区县
     */
    @ApiModelProperty(value = "发生区县", example = "哈哈")
    private String cityQxId;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dept deptQx;
    /**
     * 派出所
     */
    @ApiModelProperty(value = "派出所", example = "哈哈")
    private String policeId;

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dept deptPolice;
    /**
     * 线路
     */
    @ApiModelProperty(value = "线路 字典", example = "哈哈")
    private String lineId;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dict dictXl;
    /**
     * 地点
     */
    @ApiModelProperty(value = "地点", example = "哈哈")
    private String address;

    /**
     * b
     * 车务段
     */
    @ApiModelProperty(value = "车务段", example = "哈哈")
    private String trainId;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dict dictCwd;
    /**
     * 公务段
     */
    @ApiModelProperty(value = "公务段", example = "哈哈")
    private String trackId;
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private Dict dictGwd;
    /**
     * 事故性质
     */
    @ApiModelProperty(value = "事故性质", example = "B")
    private String nature;
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private Dict dictNature;

    /**
     * 站内性质
     */
    @ApiModelProperty(value = "站内性质", example = "A")
    private String instationSection;
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private Dict dictInstationSection;
    /**
     * 护路模式
     */
    @ApiModelProperty(value = "护路模式", example = "A")
    private String road;
    @TableField(exist = false)
    private Dict dictRoad;
    /**
     * 年龄段
     */
    @ApiModelProperty(value = "年龄段", example = "A")
    private String age;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dict dictAge;
    /**
     * 封闭程度
     */
    @ApiModelProperty(value = "封闭程度", example = "A")
    private String closed;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dict dictClosed;
    /**
     * 居住地
     */
    @ApiModelProperty(value = "居住地", example = "A")
    private String jzd;

    /**
     * 离铁路远近
     */
    @ApiModelProperty(value = "离铁路远近", example = "A")
    private String distance;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别", example = "A")
    private String sex;

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dict sexDict;

    /**
     * 身份判断
     */
    @ApiModelProperty(value = "身份判断", example = "A")
    private String identity;

    /**
     * 事故情形
     */
    @ApiModelProperty(value = "事故情形", example = "A")
    private String conditions;

    /**
     * 电脑分析系数
     */
    @ApiModelProperty(value = "电脑分析系数", example = "12.5")
    private Double dnxs;

    /**
     * 电脑调节系数
     */
    @ApiModelProperty(value = "电脑调节系数", example = "12.5")
    private Double dntjxs;

    /**
     * 公安分析系数
     */
    @ApiModelProperty(value = "公安分析系数", example = "12.2")
    private Double gaxs;

    /**
     * 属地管理责任死亡人数
     */
    @ApiModelProperty(value = "属地管理责任死亡人数", example = "哈哈")
    private Double deathToll;

    private Integer statu;

    private String month;

    @ApiModelProperty(hidden = true)
    private String policeFather;

}
