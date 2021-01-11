package com.jiebao.platfrom.mini.domian;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiebao.platfrom.railway.domain.PrizeOpinion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mini_event")
@Accessors(chain = true)
public class Event {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    private String title;

    /**
     * 事迹简要描述
     */

    private String content;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date creatTime;

    @ApiModelProperty(value = "创建人")
    private String creatUser;

    /**
     * 单位编号
     */
    @ApiModelProperty(value = "单位编号")
    private String unitNumber;

    private String name;

    private Integer age;

    private String sex;

    /**
     * 身份
     */
    @ApiModelProperty(value = "身份")
    private String identity;

    /**
     * 发布时间
     */
    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    /**
     * 发布地点
     */
    @ApiModelProperty(value = "事件发生地点")
    private String place;

    /**
     * 由谁发布：本人、他人
     */
    @ApiModelProperty(value = "由谁发布：本人、他人")
    private String releaseWho;

    /**
     * 编号
     */
    @ApiModelProperty(value = "编号")
    private String number;

    /**
     * 状态3，已发送   4已删除
     */
    @ApiModelProperty(value = "状态：1、未发送 2、撤回 3、已发送  4、已删除",example = "1")
    private Integer status;

    @ApiModelProperty(value = "限制天数id")
    private String typeId;

    @ApiModelProperty(value = "事件发生时间")
    private Date happenTime;

    @ApiModelProperty(value = "事件类型")
    private String types;

    @ApiModelProperty(value = "自身意见")
    private String ownOpinion;

    @ApiModelProperty(value = "金额")
    private String ownMoney;

    @ApiModelProperty(value = "意见")
    @TableField(exist = false)
    private List<PrizeOpinion> opinions;

    private Integer isCheck;

    private Integer reviewStatus;
}
