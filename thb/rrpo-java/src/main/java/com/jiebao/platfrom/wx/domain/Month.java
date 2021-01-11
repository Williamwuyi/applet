package com.jiebao.platfrom.wx.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.File;
import com.jiebao.platfrom.system.domain.User;
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
 * @since 2020-08-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wx_month")
public class Month implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "wx_month_id", type = IdType.UUID)
    private String wxMonthId;

    private Integer serial;//需要  自增  主键
    /**
     * 月份
     */
    private String month;

    /**
     * 内容
     */
    private String content;

    private String fuContent;//富文本有用
    /**
     * 来源于什么群
     */
    private String qunId;
    @TableField(exist = false)
    private Qun qun;
    /**
     * 起始地区
     */
    private String jcDeptId;
    @TableField(exist = false)
    private Dept deptJc;

    /**
     * 递交到那一层
     */
    private String shDeptId;
    @TableField(exist = false)
    private Dept deptSh;

    //是否进入年核
    private Integer isCheck;
    //
    private String userId;
    @TableField(exist = false)
    private User user;

    private Date date;
    //最终是否选上  0 未选上  1选上   2 上报中
    private Integer status;


    private Integer look;//0  未看  1查看了

    private Integer preStatus;//初选  0不可辱  1  可入

    private String szDeptName;

    @TableField(exist = false)
    private String[] fileIds;

    @TableField(exist = false)
    private List<File> fileList;

}
