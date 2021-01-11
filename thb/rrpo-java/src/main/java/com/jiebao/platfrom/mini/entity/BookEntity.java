package com.jiebao.platfrom.mini.entity;

import com.jiebao.platfrom.railway.domain.Address;
import lombok.Data;

import java.util.List;

@Data
public class BookEntity {

    private String id;

    private String name;

    private List<Address> bookList;

}
