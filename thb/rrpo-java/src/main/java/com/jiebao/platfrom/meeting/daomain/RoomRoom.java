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
public class RoomRoom implements Serializable {

    private static final long serialVersionUID = 1L;
  @TableId(value = "id",type = IdType.UUID)
    private String rrId;
    /**
     * 会议室名字
     */
    private String name;

    /**
     * 会议室 地址
     */
    private String address;

    /**
     * 会议室容纳人员
     */
    private Integer peopleNum;


}
