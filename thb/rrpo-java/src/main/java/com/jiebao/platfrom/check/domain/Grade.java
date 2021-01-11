package com.jiebao.platfrom.check.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

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
@TableName("check_grade")
public class Grade implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "grade_id", type = IdType.UUID)
    private String gradeId;

    /**
     * 具体年限
     */
    @ApiModelProperty(value = "年份 后端生成不用传", example = "哈哈")
    private String yearId;
    @ApiModelProperty(value = "部门id  暂定", example = "哈哈")
    private String deptId;

    /**
     * 对应考核id
     */
    @ApiModelProperty(value = "对应的考核项id", example = "哈哈")
    private String checkId;

    @TableField(exist = false)
    private MenusYear menusYear;

    @ApiModelProperty(value = "分数", example = "2.0")
    private Double num;

    private Double fpNum;

    private String message;

    private String fpMessage;

    private String parentId;// 所属考核大模块

    private Double num2;//2次提交值

    private String message2;//2次提交值

    private Integer status; //疑点状态

    private Integer sorts;



}
