package com.jiebao.platfrom.common.dataImport.controller;


import com.jiebao.platfrom.common.dataImport.domain.Info;
import com.jiebao.platfrom.common.dataImport.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/testImport")
public class TestImportController {

    @Autowired
    private InfoService infoService;

    @PostMapping(value = "/list")
    public void list() {
        List<Info> list = infoService.list();

    }

}
