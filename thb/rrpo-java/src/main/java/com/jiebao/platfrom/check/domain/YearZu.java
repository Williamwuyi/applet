package com.jiebao.platfrom.check.domain;

import lombok.Data;

import java.util.List;

@Data
public class YearZu {
    private String name;
    private String id; //分组类型所属id
    private Integer num;
    private List<? extends Object> list;  //子集  儿子类的
}
