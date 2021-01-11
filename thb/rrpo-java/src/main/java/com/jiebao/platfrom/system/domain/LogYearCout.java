package com.jiebao.platfrom.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogYearCout implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deptName;//组织名字

    private Integer greater;  //大于

    private Integer less;//小于
}
