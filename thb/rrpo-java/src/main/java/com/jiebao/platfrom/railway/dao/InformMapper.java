package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.demo.domain.Demo;
import com.jiebao.platfrom.railway.domain.Inform;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author yf
 */
public interface InformMapper extends BaseMapper<Inform> {



    @Insert("INSERT INTO rail_inform_dept (dept_id,inform_id) VALUES (#{deptId},#{informId})")
    boolean setInformDept(String deptId, String informId);

    @Insert("INSERT INTO rail_inform_user (user_id,inform_id) VALUES (#{userId},#{informId})")
    boolean setInformUser(String userId, String informId);

    @Update("UPDATE  rail_inform  r set  r.`status` = 2 WHERE r.id =#{informId}")
    boolean revoke(String informId);

    @Update("UPDATE  rail_inform  r set  r.`status` = 3 ,r.release_time = now() WHERE r.id =#{informId}")
    boolean release(String informId);

    @Update("UPDATE  rail_inform  r set  r.`status` = 4 WHERE r.id =#{informId}")
    boolean updateStatus(String informId);
}
