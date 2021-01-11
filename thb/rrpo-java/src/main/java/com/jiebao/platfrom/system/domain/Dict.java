package com.jiebao.platfrom.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiebao.platfrom.common.converter.TimeConverter;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_dict_new")
@Excel("字典信息表")
public class Dict implements Serializable {

    private static final long serialVersionUID = 7780820231535870010L;

    @TableId(value = "dict_id", type = IdType.UUID)
    private String dictId;


    @ExcelField(value = "父级ID")
    @ApiModelProperty(value = "父级ID")
    private String parentId;


    @ExcelField(value = "字典名")
    @ApiModelProperty(value = "字典名")
    private String tableName;


    @ApiModelProperty(value = "字典项名称")
    @ExcelField(value = "字典项名称")
    private String fieldName;

    @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    @ApiModelProperty(value = "创建时间")
    private Date creatTime;

    @ApiModelProperty(value = "公里数", example = "125")
    private Integer revenue;

    @ApiModelProperty(value = "线路性质")
    private String nature;

    @ApiModelProperty(value = "系数占比",example = "12.23")
    private Double coefficient;

    @ApiModelProperty(value = "值")
    private String value;
   /* private static final long serialVersionUID = 7780820231535870010L;

    @TableId(value = "dict_id", type = IdType.UUID)
    private String dictId;

    @NotBlank(message = "{required}")
    @Size(max = 10, message = "{noMoreThan}")
    @ExcelField(value = "键")
    private String keyy;

    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    @ExcelField(value = "值")
    private String valuee;

    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    @ExcelField(value = "表名")
    private String tableName;

    @NotBlank(message = "{required}")
    @Size(max = 20, message = "{noMoreThan}")
    @ExcelField(value = "字段名")
    private String fieldName;*/
   @ApiModelProperty(value = "是否禁用0、禁用 1、未禁用" ,example = "1")
   private Integer ifDisable;

}
