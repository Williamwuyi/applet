package com.jiebao.platfrom.railway.domain;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 一事一奖推送人表
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rail_prize_user")
@Accessors(chain = true)
public class PrizeUser {

    @TableId(value = "id")
    private String id;

    /**
     * 关联的一事一奖内容id
     */
    private String prizeId;

    /**
     * 推送时间
     */
    @ApiModelProperty(value = "推送时间")
    private Date creatTime;

    /**
     * 推送给谁
     */
    @ApiModelProperty(value = "推送给组织机构的ID")
    private String sendDept;


}
