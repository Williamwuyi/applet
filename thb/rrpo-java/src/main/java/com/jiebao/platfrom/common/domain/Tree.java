package com.jiebao.platfrom.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jiebao.platfrom.railway.domain.Address;
import com.jiebao.platfrom.system.domain.File;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tree<T> {

    private String id;

    private String key;

    private String icon;

    private String title;

    private String value;

    private String text;

    private String permission;

    private String type;

    private Double order;

    private String path;

    private String component;

    private List<Tree<T>> children;

    private String parentId;

    private boolean hasParent = false;

    private boolean hasChildren = false;

    private Date createTime;

    private Date modifyTime;

    //@JsonProperty("children")
    private List<Address> address;

    private String weiXin;

    private String userName;

    private  String phone;

    private String telPhone;

    private String email;

    private String userId;

    private String deptName;

    private String deptId;

    private Integer Mark;

    private  String name;

    private Date creatTime;


    public void initChildren(){
        this.children = new ArrayList<>();
    }

}
