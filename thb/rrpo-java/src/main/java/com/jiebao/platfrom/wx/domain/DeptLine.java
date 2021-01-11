package com.jiebao.platfrom.wx.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2020-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wx_dept_line")
public class DeptLine implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "dept_line_id", type = IdType.UUID)
    private String deptLineId;

    private String deptId;

    private String lineId;

    @TableField(exist = false)
    @ApiModelProperty(value = "不用管 返回用的", hidden = true)
    private String LineName;

    @TableField(exist = false)
    @ApiModelProperty(value = "不用管 返回用的", hidden = true)
    private String nature;

    @TableField(exist = false)
    @ApiModelProperty(value = "不用管 返回用的", hidden = true)
    private String revenue;


}
