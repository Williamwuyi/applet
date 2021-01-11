package com.jiebao.platfrom.meeting.daomain;

import java.io.Serializable;

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
public class RoomUsers implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.UUID)
    private  String roomUserId;

    /**
     * 会议id  
     */

    private String meetingId;

    /**
     * 人员id
     */
    private String userId;

    /**
     * 回执 是否参加  0  不参加  1参加
     */
    private Integer status;

    /**
     * 代替参会的人
     */
    private String takeUserId;

    /**
     * 是否是领导
     */
    private Integer leadIf;


}
