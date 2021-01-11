package com.jiebao.platfrom.railway.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 一事一奖驳回意见表
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rail_prize_reject_opinion")
@Accessors(chain = true)
public class PrizeRejectOpinion {


    /**
     * 关联的一事一奖内容id
     */
    private String id;


    /**
     * 驳回时间
     */
    @ApiModelProperty(value = "驳回时间")
    private Date auditTime;

    /**
     * 驳回意见
     */
    @ApiModelProperty(value = "驳回意见")
    private String auditOpinion;



    private String userId;

    @TableField(exist = false)
    private Integer rank;

    @TableField(exist = false)
    private String userName;
}
