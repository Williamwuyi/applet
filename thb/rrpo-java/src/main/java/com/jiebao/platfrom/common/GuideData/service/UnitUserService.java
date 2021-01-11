package com.jiebao.platfrom.common.GuideData.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.GuideData.domain.Unituser;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.common.domain.Tree;
import com.jiebao.platfrom.railway.domain.Address;
import com.jiebao.platfrom.system.domain.Dept;

import java.util.List;
import java.util.Map;

public interface UnitUserService extends IService<Unituser> {

    List<Unituser> selectNew();

    Unituser selectName(String userName);

    List<Unituser> selectCT();

    List<Unituser> selectHT();

    List<Unituser> selectHHT();
}
