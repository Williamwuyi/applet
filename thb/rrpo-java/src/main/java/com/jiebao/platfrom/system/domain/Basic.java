package com.jiebao.platfrom.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "sys_basic")
public class Basic implements Serializable {

    private static final long serialVersionUID = -205572403047270463L;

    @TableId(value = "basic_id", type = IdType.UUID)
    private String basicId;

    private Integer imageSize;

    private Integer fileSize;

    private String imageType;

    private String fileType;

}
