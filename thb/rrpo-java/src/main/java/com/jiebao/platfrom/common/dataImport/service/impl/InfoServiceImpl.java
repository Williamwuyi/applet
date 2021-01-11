package com.jiebao.platfrom.common.dataImport.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jiebao.platfrom.common.dataImport.dao.InfoMapper;
import com.jiebao.platfrom.common.dataImport.domain.Info;
import com.jiebao.platfrom.common.dataImport.service.InfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("infoService")
@DS("old")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class InfoServiceImpl extends ServiceImpl<InfoMapper, Info> implements InfoService {
    @Override
    public List<Info> listForYear() {
        return baseMapper.listForYear();
    }
}
