package com.jiebao.platfrom.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName("sys_user_role")
@Data
public class UserRole implements Serializable{
	
	private static final long serialVersionUID = -3166012934498268403L;

	private String userId;

	private String roleId;

}