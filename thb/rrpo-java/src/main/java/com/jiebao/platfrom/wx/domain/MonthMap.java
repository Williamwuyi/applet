package com.jiebao.platfrom.wx.domain;


import lombok.Data;

import java.io.Serializable;

@Data
public class MonthMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deptName;

    private Integer number;
}