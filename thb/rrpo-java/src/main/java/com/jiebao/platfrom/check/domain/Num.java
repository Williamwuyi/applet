package com.jiebao.platfrom.check.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
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
 * @since 2020-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("check_num")
public class Num implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分数总结id
     */
    @TableId(value = "num_id", type = IdType.UUID)
    private String numId;

    /**
     * 存年份
     */
    @ApiModelProperty(value = "年份 后端生成", example = "2018")
    private String yearId;

    private String yearDate;

    /**
     * 年度分数
     */
    @ApiModelProperty(value = "年度分数", example = "23.23")
    private Double number;

    private Double fpNumber;  //复评分数
    /**
     * 所属组织结构或地区
     */
    @ApiModelProperty(value = "所属组织机构", example = "组织结构")
    private String deptId;
    @TableField(exist = false)
    private Dept dept;


    @ApiModelProperty(value = "基础工作分数", example = "23.23")
    private Double jcWork;

    private Double fpJcWork;
    @ApiModelProperty(value = "工作效果分数", example = "23.23")
    private Double xgWork;

    private Double fpXgWork;
    @ApiModelProperty(value = "试卷效果", example = "23.23")
    private Integer status;     //0 未审核  1被驳回  2补充提交   3终审

    private Date deadDate; //限期回复时间

}
