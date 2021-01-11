package com.jiebao.platfrom.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询参数定义
 */

@Data
public class QueryRequest implements Serializable {

    private static final long serialVersionUID = -4869594085374385813L;

    private int pageSize = 10;
    private int pageNum = 1;

    private String sortField;
    private String sortOrder;
}
