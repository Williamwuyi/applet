package com.jiebao.platfrom.common.GuideData.domain;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuwenze.poi.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("unituser")
@Excel("老数据人员信息表")
public class Unituser implements Serializable {

    private static final long serialVersionUID = -7790334862410409053L;

    // 默认密码
    public static final String DEFAULT_PASSWORD = "1234qwer";


    @TableId(value = "id",type = IdType.UUID)
    private Integer id;

    /**
     * 姓名
     */
    private String unitUser;

    /**
     * 所属组织机构
     */
    private String unit;

    private String areaCode;

    private String ucode;

    private String pcode;

    /**
     * 职务
     */
    private String headship;

    private String officeTelephone;

    private String mobileTelephone;

    /**
     * 密码
     */
    private String loginpassword;

    /**
     * 类型，u,p
     */
    private String usertype;

    private String usercode;



}
