package com.jiebao.platfrom.wx.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.io.Serializable;
import java.util.Date;

import com.jiebao.platfrom.system.domain.Dept;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author qta
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wx_qun")
public class Qun implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 微信id
     */
    @TableId(value = "wx_id", type = IdType.UUID)
    private String wxId;

    /**
     * 群名字
     */
    private String wxName;

    /**
     * 群负责人
     */
    private String wxUserName;

    /**
     * 创建时间
     */
    @DateTimeFormat(style = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 是否需要审核群成员信息 0 需要 1不需要
     */
    private Integer status;
    @DateTimeFormat(style = "yyyy-MM-dd HH:mm:ss")
    private Date shDate;  //最后被审核的时间

    private String cjDeptId;  //提交组织层
    @TableField(exist = false)
    private Dept deptJc;
    @TableField(exist = false)
    private Dept deptSh;
    private String shDeptId;  //待审核 层

    private Integer shStatus;  //状态  0未审核  1  正在审核  2  审核完成  3  被打回

    @TableField(exist = false)
    private Integer number;//群人数




}
