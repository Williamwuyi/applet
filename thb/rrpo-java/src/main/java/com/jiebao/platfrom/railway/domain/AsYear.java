package com.jiebao.platfrom.railway.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiebao.platfrom.system.domain.File;
import com.wuwenze.poi.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 限制自定义次数
 *
 * @author yf
 */
@Data
@TableName("rail_as_year")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("关联年度考核表")
public class AsYear {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    private Date creatTime;

    @ApiModelProperty(value = "年度考核对应ID")
    private String gradeId;

    @ApiModelProperty(value = "类型",example = "1")
    private Integer type;

    @ApiModelProperty(value = "文件对应ID")
    private String fileId;

    @ApiModelProperty(value = "组织机构ID")
    private String deptId;
   @TableField(exist = false)
    private File file;
   @ApiModelProperty(value = "yearId")
    private String yearId;
}
