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
 * 公共信息
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rail_private_file")
@Accessors(chain = true)
public class PrivateFile {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    private String name;

    private String parentId;

    private Date creatTime;

    private String userId;

    @ApiModelProperty(value = "标识符", example = "1")
    private Integer mark;

    @TableField(exist = false)
    private boolean hasChildren = false;

}
