package com.jiebao.platfrom.wx.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.jiebao.platfrom.system.domain.Dept;
import io.swagger.annotations.ApiModelProperty;
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
@TableName("wx_user")
public class UserI implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "wx_user_id", type = IdType.UUID)
    private String wxUserId;

    /**
     * 名字
     */
    private String name;

    /**
     * 身份证
     */
    private String idCar;

    private String phone;

    /**
     * 身份
     */
    private String sf;

    /**
     * 创建时间
     */
    @DateTimeFormat(style = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    private String qunId;//群

    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private String wxName;

    //乡镇 组织
    private String deptXz;

    //曲线 组织
    private String deptQx;

    //市州 组织
    private String deptSz;
    /**
     * 下面对应实体
     */
    //乡镇 组织
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dept deptXzO;

    public UserI(String name, String idCar, String phone, String sf, String qunId, String deptXz, String deptQx, String deptSz) {
        this.name = name;
        this.idCar = idCar;
        this.phone = phone;
        this.sf = sf;
        this.qunId = qunId;
        this.deptXz = deptXz;
        this.deptQx = deptQx;
        this.deptSz = deptSz;
    }

    //曲线 组织
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dept deptQxO;

    //市州 组织
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Dept deptSzO;

    public UserI() {

    }



}
