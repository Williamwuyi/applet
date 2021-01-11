package com.jiebao.platfrom.accident.daomain;

import lombok.Data;

import java.io.Serializable;


@Data
public class ANumber implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subscript;//下表  代表不同类型名字
    private Integer number; //
}
