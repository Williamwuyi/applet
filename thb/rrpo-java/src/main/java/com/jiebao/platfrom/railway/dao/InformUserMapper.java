package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.ExchangeUser;
import com.jiebao.platfrom.railway.domain.InformUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yf
 */
public interface InformUserMapper extends BaseMapper<InformUser> {

    /**
     * 新增
     *
     * @param sendUserId
     * @param informId
     * @return
     */
    @Insert("INSERT INTO `rail_inform_user` (inform_id,send_user_id,type) VALUES (#{informId},#{sendUserId},1)")
    boolean sendUser(String sendUserId, String informId);


    /**
     * 根据接收人查询他所接收到的信息
     * @param sendUserId
     * @return
     */
    @Select("SELECT * FROM `rail_inform_user` WHERE send_user_id = #{sendUserId}")
    List<InformUser> getByInformSendUserId(String sendUserId);

    /**
     * 删除收件箱记录
     * @param sendUserId 收件人
     * @param informId  通知公告Id
     * @return
     */
    @Delete("DELETE FROM rail_inform_user WHERE inform_id = #{informId} and  send_user_id = #{sendUserId}")
    boolean removeBySendUserId(String sendUserId, String informId);
}
