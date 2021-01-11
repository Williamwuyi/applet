package com.jiebao.platfrom.common.dataImport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.dataImport.domain.Info;

import java.util.List;


public interface InfoService extends IService<Info> {

    List<Info> listForYear();
}
