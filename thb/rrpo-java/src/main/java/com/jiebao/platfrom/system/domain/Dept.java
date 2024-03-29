package com.jiebao.platfrom.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiebao.platfrom.common.converter.TimeConverter;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_dept")
@Excel("部门信息表")
public class Dept implements Serializable {

    private static final long serialVersionUID = -7790334862410409053L;

    @TableId(value = "dept_id", type = IdType.UUID)
    private String deptId;

    private String parentId;


    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    @ExcelField(value = "部门名称")
    private String deptName;

    private Double orderNum;

    @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    private Date createTime;

    @ExcelField(value = "修改时间", writeConverter = TimeConverter.class)
    private Date modifyTime;

    private transient String createTimeFrom;

    private transient String createTimeTo;

    private Integer status;

    private Integer rank;

    @TableField(exist = false)
    private String [] children;

    @TableField(exist = false)
    private String parentDeptName;

}