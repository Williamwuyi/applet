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
 * 信息互递文件表
 *
 * @author yf
 */
@Data
@TableName("rail_exchange_file")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExchangeFile {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "名称")
    private String title;

    private Date createTime;

    private String url;

    @ApiModelProperty(value = "创建人(后台直接获取)")
    private String createUser;


    @ApiModelProperty(value = "类型", example = "1")
    private Integer type;

    @ApiModelProperty(value = "和信息互递关联id")
    private String exchangeId;

}
