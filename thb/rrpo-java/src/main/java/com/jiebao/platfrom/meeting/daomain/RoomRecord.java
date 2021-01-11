package com.jiebao.platfrom.meeting.daomain;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author qta
 * @since 2020-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RoomRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.UUID)
    private String roomRecordId;
    /**
     * 会议名称 主题
     */
    private String name;

    /**
     *  会议室绑定
     */
    private String roomId;

    /**
     * 会议开始时间
     */
    private Date startDate;

    /**
     * 会议结束时间
     */
    private Date endDate;

    /**
     * 会议创建人
     */
    private String createUser;

    /**
     * 会议创建时间
     */
    private Date creatDate;


}
