package com.jiebao.platfrom.railway.domain;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiebao.platfrom.system.domain.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 一事一奖内容表
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rail_prize")
@Accessors(chain = true)
public class Prize {

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
    @ApiModelProperty(value = "编号",example = "1")
    private String number;

    /**
     * 状态3，已发送   4已删除
     */
    @ApiModelProperty(value = "状态：1、未发送 2、撤回 3、已发送  4、已删除 5、区县已通过 6、市级已通过 7、公安处已通过 8、省级已通过" ,example = "1")
    private Integer status;

    @ApiModelProperty(value = "限制天数id")
    private String typeId;

    @ApiModelProperty(value = "事件发生时间")
    private Date happenTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "事件类型对象")
    private Dict typeObject;


    @TableField(exist = false)
    @ApiModelProperty(value = "事件类型对象")
    private Dict identityObject;


    @ApiModelProperty(value = "事件类型对象")
    private String types;

    @ApiModelProperty(value = "自身意见")
    private String ownOpinion;

    @ApiModelProperty(value = "金额")
    private String ownMoney;

    @ApiModelProperty(value = "意见")
    @TableField(exist = false)
    private List<PrizeOpinion> opinions;

    private Integer isCheck;


    @TableField(exist = false)
    private String briefingMoney;

    @ApiModelProperty(value = "新编号")
    private String newNumber;
    /**
     * 是否同步到门户网站
     */
    @ApiModelProperty(value = "是否同步到门户网站",example = "1")
    private Integer synchronizeWeb;

    @ApiModelProperty(value = "事发地点市区ID")
    private String placeIds;

    @ApiModelProperty(value = "事发地点区县ID")
    private String placeIdq;

    @ApiModelProperty(value = "事发地点街道ID")
    private String placeIdj;

    private String userId;

    @ApiModelProperty(value = "由公安处派出所创建或者县市区 0、公安处  1、县市区",example = "1")
    private Integer type;

    @ApiModelProperty(value = "自己控制",example = "1")
    private Integer cityStatus;

    @ApiModelProperty(value = "自己控制公安处",example = "1")
    private Integer gongStatus;


    @ApiModelProperty(value = "是否已经导出1导出，0未导出",example = "1")
    private Integer ifImport;
}
