package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.Briefing;
import com.jiebao.platfrom.railway.domain.BriefingUser;
import com.jiebao.platfrom.railway.domain.ExchangeUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author yf
 */
public interface BriefingUserMapper extends BaseMapper<BriefingUser> {

    /**
     * parent_id =1  实际上创建信息时，信息就已经发送到接收用户的收件箱，但是briefing的status为未发送状态，发布时改下status即可批量发送
     */
    @Insert("INSERT INTO `rail_briefing_user` (briefing_id,send_user_id,send_user_name,is_read,type) VALUES (#{briefingId},#{sendUserId},#{sendUserName},0,1)")
    boolean saveByUserId(String briefingId, String sendUserId, String sendUserName);

    @Delete("DELETE FROM rail_briefing_user WHERE briefing_id = #{briefingId}")
    boolean deleteByBriefingId(String briefingId);

    /**
     * 根据接收人查询他所接收到的信息
     *
     * @param sendUserId
     * @return
     */
    @Select("SELECT * FROM `rail_briefing_user` WHERE send_user_id = #{sendUserId}")
    List<BriefingUser> getBySendUserId(String sendUserId);


    /**
     * 根据护路简报ID和发送人删除收件箱记录
     *
     * @param sendUserId
     * @param briefingId
     */
    @Delete("DELETE FROM rail_briefing_user WHERE briefing_id = #{briefingId} and  send_user_id = #{sendUserId}")
    boolean removeBySendUserId(String sendUserId, String briefingId);

    @Insert("UPDATE rail_briefing_user set creat_time = now()  WHERE briefing_id =#{briefingId}")
    boolean setCreatTime(String briefingId);

    @Select("SELECT * FROM `rail_briefing_user` WHERE send_user_id = #{sendUserId} and briefing_id = #{briefingId}")
    BriefingUser getIsRead(String sendUserId, String briefingId);

    @Update("UPDATE rail_briefing_user set type = 2  WHERE briefing_id =#{briefingId} and send_user_id =#{sendUserId}")
    boolean upDateBySendUserId(String sendUserId, String briefingId);

    @Select("SELECT count(*) FROM `rail_briefing_user` WHERE  briefing_id = #{briefingId} and is_read = 0 ")
    int countByIsReadZero(String briefingId);

    @Select("SELECT count(*) FROM `rail_briefing_user` WHERE  briefing_id = #{briefingId} and is_read = 1 ")
    int countByIsReadOne(String briefingId);


    @Select("SELECT * FROM `rail_briefing_user` WHERE briefing_id =#{briefingId} and send_user_id = #{sendUserId}")
    BriefingUser findByNameAndId(String briefingId, String sendUserId);

    @Update("UPDATE rail_briefing_user set is_read = 1,receive_time =now()  WHERE briefing_id =#{briefingId} and send_user_id =#{sendUserId}")
    boolean updateIsRead(String briefingId, String sendUserId);
}
