package com.jiebao.platfrom.common.GuideData.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuwenze.poi.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("hlb_info")
@Excel("信息互递主表")
public class HlbInfo implements Serializable {

    private static final long serialVersionUID = -7790334862410409053L;


    @TableId(value = "id",type = IdType.UUID)
    private Integer id;

    /**
     * 对应的信息互递info_seq
     */
    private String infoSeq;

    private Date upTime;

}
