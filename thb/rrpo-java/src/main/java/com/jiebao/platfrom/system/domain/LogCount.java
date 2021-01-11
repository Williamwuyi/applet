package com.jiebao.platfrom.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogCount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date;

    private Integer count;

    public LogCount(String date, Integer count) {
        this.date = date;
        this.count = count;
    }
}
