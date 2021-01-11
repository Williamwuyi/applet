package com.jiebao.platfrom.railway.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 一事一奖意见表
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rail_prize_opinion")
@Accessors(chain = true)
public class PrizeOpinion {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 关联的一事一奖内容id
     */
    private String prizeId;


    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    /**
     * 审核意见
     */
    @ApiModelProperty(value = "审核意见")
    private String auditOpinion;

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private String money;

    @ApiModelProperty(value = "组织机构等级",example = "1")
    private Integer rank;

}
