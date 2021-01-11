package com.jiebao.platfrom.common.GuideData.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuwenze.poi.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("hlb_info_comment")
@Excel("信息互递接收人表")
public class InfoComment implements Serializable {

    private static final long serialVersionUID = -7790334862410409053L;


    @TableId(value = "id",type = IdType.UUID)
    private Integer id;

    /**
     * 对应的信息互递info_seq
     */
    private String infoSeq;

    /**
     * 标题
     */
    private String title;

    /**
     * 接收人
     */
    private String commentUser;

    /**
     * 回复信息
     */
    private String commentContent;

    /**
     * 阅读时间
     */
    private String viewTime;

}
