package com.jiebao.platfrom.wx.domain;

import com.jiebao.platfrom.common.utils.ExcelName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@ExcelName(name = "合格群导出")   //必写    要不然会有问题
@Data
public class QunExcel implements Serializable {
    //    private static final long serialVersionUID = 1l;
    @ExcelName(name = "市级单位名称")
    private String cityDeptName;  //市级单位名称
    @ExcelName(name = "乡镇街道单位名称")
    private String villageDeptName; //村级单位名称
    @ExcelName(name = "群名称")
    private String wxName; //微信名字
    @ExcelName(name = "群人数")
    private Integer number;//群人数

    public QunExcel(String wxName) {
        this.wxName = wxName;
    }
}
