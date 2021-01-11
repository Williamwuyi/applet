package com.jiebao.platfrom.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.system.domain.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
	
	List<Role> findUserRole(String userName);

	@Select("select * from sys_role where role_id in (select role_id from sys_user_role where user_id=#{userId})")

	List<Role> listRole(String userId);
	
}