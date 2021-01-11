package com.jiebao.platfrom.accident.daomain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.jiebao.platfrom.common.utils.ExcelName;
import com.jiebao.platfrom.system.domain.File;
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
 * @since 2020-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("accident_jk")
@ExcelName(name = "监控表")
public class Jk implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.UUID)
    private String id;

    @TableField("PK")
    private String pk;
    @ExcelName(name = "公安处")
    private String gac;
    @ExcelName(name = "地州市")
    private String dzs;
    @ExcelName(name = "县市区")
    private String xsq;
    @ExcelName(name = "乡镇街道")
    private String xzjd;
    @ExcelName(name = "线路名称")
    private String xlmc;
    @ExcelName(name = "铁路里程")
    private String tllc;
    @ExcelName(name = "对应方位")
    private String azfw;
    @ExcelName(name = "铁路距离")
    private String tljl;
    @ExcelName(name = "道路名称")
    private String dlmc;
    @ExcelName(name = "道路里程")
    private String dllc;
    @ExcelName(name = "安装部位")
    private String azbw;
    @ExcelName(name = "视频个数")
    private Integer sl;
    @ExcelName(name = "视频类型")
    private String jkqlx;
    @ExcelName(name = "视频范围")
    private Integer jkfw;
    @ExcelName(name = "设计类型")
    private String jksjzl;
    @ExcelName(name = "夜市功能")
    private String jkysgn;
    @ExcelName(name = "监控功能")
    private Integer jkgd;
    @ExcelName(name = "保留天数")
    private Integer blts;
    @ExcelName(name = "联网单位")
    private String lwdw;
    @ExcelName(name = "建设单位")
    private String jsdw;
    //    @ExcelName(name = "安装时间")
    private String azsjn;
    @ExcelName(name = "安装时间")
    private String azsj;
    @ExcelName(name = "管理单位")
    private String gldw;
    @ExcelName(name = "使用单位")
    private String sydw;
    @ExcelName(name = "派出所")
    private String pcs;
    @ExcelName(name = "属地派出所")
    private String sdpcs;
    //    @ExcelName(name = "道路里程")
    private String bz;


    //    private String tp;
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<File> tpFile;
    @ApiModelProperty(hidden = true)
    private String delflag;

    private String username;

    private String datalock;
    @ApiModelProperty(hidden = true)
    private LocalDateTime creatTime;


}
