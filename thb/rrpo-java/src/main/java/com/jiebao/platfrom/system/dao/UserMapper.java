package com.jiebao.platfrom.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.system.domain.Dept;
import com.jiebao.platfrom.system.domain.User;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

public interface UserMapper extends BaseMapper<User> {


    IPage<User> findUserDetail(Page page, @Param("user") User user);

    @Select("select user_id,user_id as userId,username,password,dept_id,(select dept_name from sys_dept d where u.dept_id=d.dept_id) as deptName,email," +
            "mobile,status,create_time,modify_time,last_login_time,ssex,description,avatar,openid,type,real_name,idcard" +
            "  from sys_user u ${ew.customSqlSegment}")
    @Results({
            @Result(property = "roleList",column = "user_id",many = @Many(select = "com.jiebao.platfrom.system.dao.RoleMapper.listRole"))
    })
    IPage<User> queryList(Page<User> page, @Param("ew") QueryWrapper<User> ew);

    /**
     * 获取单个用户详情
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findDetail(String username);

    /**
     * 部门下所有人员
     *
     * @param deptId
     * @return
     */
    @Select(" select * from sys_user r join sys_dept d on r.dept_id = d.dept_id where  r.dept_id = #{deptId}")
    List<User> getUserList(String deptId);

    /**
     * 根据部门查人员
     *
     * @param deptId
     * @return
     */

    @Select("select * from sys_user r  where  r.dept_id = #{deptId}")
    List<User> getByDepts(String deptId);


    @Select("select username from sys_user   ${ew.customSqlSegment}")
    List<String> getUserNameByDepts(@Param("ew") QueryWrapper<User> ew);


    @Select("select user_id from sys_user   ${ew.customSqlSegment}")
    List<String> getUserIdByDepts(@Param("ew") QueryWrapper<User> ew);

    @Select("select dept_id from sys_user r  where  r.username = #{userName}")
    String getDeptID(String userName);

    @Select("UPDATE sys_user SET dept_id = #{deptId} WHERE user_id = #{userId}")
    void updateDept(String deptId, String userId);

    @Select("SELECT * FROM `sys_user` WHERE dept_id is null")
    List<User> nullList();

    @Update("UPDATE sys_user SET dept_id = #{deptId} WHERE username =#{userName}")
    boolean updateNew(String deptId, String userName);

    /**
     * 查询导入部分角色后剩下哪些用户还没有角色
     *
     * @return
     */
    @Select("SELECT * FROM sys_user WHERE user_id not in (SELECT u.user_id FROM `sys_user` u,sys_user_role r WHERE u.user_id =r.user_id)")
    List<User> remainList();

    /**
     * 导数据，更改部分人的组织机构
     * @param userName
     * @return
     */
    @Update("UPDATE sys_user set dept_id = '4000' WHERE username = #{userName}")
    boolean updateByName(String userName);


    @Update("UPDATE sys_user set dept_id = #{deptId} WHERE user_id = #{userId}")
     boolean updateByUserId(String userId ,String deptId);


    @Select("SELECT * FROM `sys_user` WHERE user_id =#{userId}")
    User getByUserId(String userId);
}