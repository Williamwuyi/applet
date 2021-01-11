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
public class RoomRecordSevice implements Serializable {

    private static final long serialVersionUID = 1L;
   @TableId(value = "id",type = IdType.UUID)
    private String rrSid;
    /**
     * 会议id
     */
    private String roomRecordId;

    /**
     * 服务id
     */
    private String roomServiceId;


}
