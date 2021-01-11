package com.jiebao.platfrom.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Week implements Serializable {
    private static final long serialVersionUID = 1L;
    private String startDate;
    private String endDate;
}
