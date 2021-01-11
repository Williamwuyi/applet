package com.jiebao.platfrom.railway.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiebao.platfrom.common.converter.TimeConverter;
import com.wuwenze.poi.annotation.Excel;
import com.wuwenze.poi.annotation.ExcelField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author yf
 */
@Data
@TableName("rail_inform_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Excel("通知公告接收人")
public class InformUser {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ExcelField(value = "和通知公告内容关联ID")
    private String informId;

    @ExcelField(value = "接收公告的用户ID")
    private String sendUserId;

    @ExcelField(value = "创建时间", writeConverter = TimeConverter.class)
    private Date creatTime;


    /**
     * 1收件，2回收 回收站只放收件箱的信息
     */
    private String type;

}
