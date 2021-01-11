package com.jiebao.platfrom.wx.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
@TableName("wx_sh")
public class Sh implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "wx_sh_id", type = IdType.UUID)
    private String wxShId;  //id 主键 不用管

    private String deptId; //审核的部门
    @TableField(exist = false)
    private Dept dept;


    private String wxQunId; //群id

    /**
     * 0通过  1不通过
     */
    private Integer status;//状态
    @DateTimeFormat(style = "yyyy-MM-dd HH:mm:ss")
    private Date shDate;  //时间不用管

    private String massage; //留言  同意或者拒绝的理由

    private String userId; //审核人


}
