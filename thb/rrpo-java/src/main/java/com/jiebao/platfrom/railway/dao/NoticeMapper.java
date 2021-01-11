package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.Inform;
import com.jiebao.platfrom.railway.domain.Notice;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

/**
 * @author yf
 */
public interface NoticeMapper extends BaseMapper<Notice> {



    @Insert("INSERT INTO rail_inform_dept (dept_id,inform_id) VALUES (#{deptId},#{informId})")
    boolean setInformDept(String deptId, String informId);

    @Insert("INSERT INTO rail_inform_user (user_id,inform_id) VALUES (#{userId},#{informId})")
    boolean setInformUser(String userId, String informId);

    @Update("UPDATE  rail_inform  r set  r.`status` = 2 WHERE r.id =#{informId}")
    boolean revoke(String informId);

    @Update("UPDATE  rail_notice  r set  r.`status` = 3 ,r.release_time = now() WHERE r.id =#{noticeId}")
    boolean release(String noticeId);

    @Update("UPDATE  rail_notice  r set  r.`status` = 4 WHERE r.id =#{noticeId}")
    boolean updateStatus(String noticeId);

    @Update("UPDATE  rail_notice  r set  r.`status` = 2 WHERE r.id =#{noticeId}")
    boolean revokeNotice(String noticeId);
}
