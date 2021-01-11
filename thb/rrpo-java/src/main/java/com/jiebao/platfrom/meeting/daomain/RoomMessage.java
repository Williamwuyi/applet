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
public class RoomMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.UUID)
    private String roomMessageId;
    /**
     * 内容
     */
    private String content;

    /**
     * 信息创建时间
     */
    private Date date;

    /**
     * 相关会议id
     */
    private String rocordId;

    /**
     * 对应人的id
     */
    private String userId;

    /**
     * 是否已读  0未读  1 已读
     */
    private Integer readIf;

    /**
     * 0  不参会  1 参会
     */
    private Integer status;

    /**
     * 代替参会人员
     */
    private String takeUser;

    /**
     * 邀请参会  解除参会   0 邀请  1解除
     */
    private Integer inviteIf;


}
