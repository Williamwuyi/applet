package com.jiebao.platfrom.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName("sys_user_role")
@Data
public class UserRole implements Serializable{
	
	private static final long serialVersionUID = -3166012934498268403L;

	private String userId;

	/**
	 * 用户角色1、省 2、护路办 3、市 4、区 5、街道
	 */
	private String roleId;

}