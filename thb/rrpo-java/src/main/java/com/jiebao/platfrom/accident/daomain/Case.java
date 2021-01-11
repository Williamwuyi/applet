package com.jiebao.platfrom.accident.daomain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("accident_case")
public class Case implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 涉铁主键
     */
    @TableId(value = "case_id", type = IdType.UUID)
    private String caseId;

    /**
     * 发生时间
     */
    @ApiModelProperty(value = "创建时间", example = "2020/04/03 12:15:12")
    private String date;

    /**
     * 发生城市
     */
    @ApiModelProperty(value = "发生城市", example = "哈哈")
    private String cityCsId;
    @TableField(exist = false)
    private Dept deptCs;
    /**
     * 发生县区
     */
    @ApiModelProperty(value = "发生县区", example = "哈哈")
    private String cityQxId;
    @TableField(exist = false)
    private Dept deptQx;
    /**
     * 发生乡镇村
     */
    @ApiModelProperty(value = "发生乡镇村", example = "哈哈")
    private String cityXc;

    @TableField(exist = false)
    private Dept deptXc;
    /**
     * 派出所
     */
    @ApiModelProperty(value = "派出所", example = "哈哈")
    private String policeId;
    @TableField(exist = false)
    private Dept deptPolice;
    /**
     * 线路
     */
    @ApiModelProperty(value = "线路   字典", example = "哈哈")
    private String lineId;
    @TableField(exist = false)
    private Dict dictXl;
    /**
     * 地点
     */
    @ApiModelProperty(value = "地点", example = "哈哈")
    private String address;

    /**
     * 案件性质分类
     */
    @ApiModelProperty(value = "案件性质分类", example = "哈哈")
    private String nature;

    /**
     * 停车时长
     */
    @ApiModelProperty(value = "停车时长", example = "12.16")
    private Double dateLength;

    /**
     * 案件状态
     */
    @ApiModelProperty(value = "案件状态", example = "哈哈")
    private String status;

    /**
     * 封闭状态
     */
    @ApiModelProperty(value = "封闭状态", example = "哈哈")
    private String fbStatus;

    /**
     * 属地管理责任说明
     */
    @ApiModelProperty(value = "属地管理责任说明", example = "哈哈")
    private String content;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "哈哈")
    private String remark;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", example = "哈哈")
    private String ms;

    private Integer statu;
    private String month;



}
