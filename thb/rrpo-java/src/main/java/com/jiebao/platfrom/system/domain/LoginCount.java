package com.jiebao.platfrom.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginCount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deptId;
    private String userId;
    private String deptName;  //部门数量
    private String userName;
    private Integer number;  //次数

    public LoginCount setName(String deptName) {
        this.deptName = deptName;
        return this;
    }
}
