package com.jiebao.platfrom.accident.daomain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.jiebao.platfrom.common.utils.ExcelName;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.File;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author qta
 * @since 2020-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("accident_ql")
@ExcelName(name = "桥梁统计表")
/**
 * 老系统直接  拷过来的
 */
public class Ql implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.UUID)
    private String id;
    @ExcelName(name = "编码")
    private String bm;
    @ExcelName(name = "地州市")
    private String dzs;
    @ExcelName(name = "县市区")
    private String xsq;
    @ExcelName(name = "乡镇街道")
    private String xz;
    @ExcelName(name = "坐落村组")
    private String zl;
    @ExcelName(name = "公安处")
    private String ga;
    @ExcelName(name = "派出所")
    private String pcs;
    @ExcelName(name = "警务处")
    private String jwq;
    @ExcelName(name = "工务段")
    private String gwd;
    @ExcelName(name = "公里数")
    private String gls;
    @ExcelName(name = "修建方位")
    private String fw;
    @ExcelName(name = "修建长度")
    private String cd;
    @ExcelName(name = "交通事故情况")
    private String jtsgqk;
    @ExcelName(name = "修建原因及周边社情")
    private String yy;
    @ExcelName(name = "陆地已开展工作")
    private String kzqk;

//    private String tp;

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<File> tpFile;
    @ApiModelProperty(hidden = true)
    private String delflag;
    @ApiModelProperty(hidden = true)
    private LocalDateTime creatTime;


}
