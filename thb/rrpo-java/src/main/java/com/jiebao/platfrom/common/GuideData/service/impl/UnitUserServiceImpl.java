package com.jiebao.platfrom.common.GuideData.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.GuideData.dao.UnituserMapper;
import com.jiebao.platfrom.common.GuideData.domain.Unituser;
import com.jiebao.platfrom.common.GuideData.service.UnitUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("UnitUserService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@DS("old")
public class UnitUserServiceImpl extends ServiceImpl<UnituserMapper, Unituser> implements UnitUserService {

    @Autowired
    private  UnituserMapper unituserMapper;

    @Override
    public List<Unituser> selectNew() {
        return unituserMapper.selectNew();
    }

    @Override
    public Unituser selectName(String userName) {
        return unituserMapper.selectName(userName);
    }

    @Override
    public List<Unituser> selectCT() {
        return unituserMapper.selectCT();
    }

    @Override
    public List<Unituser> selectHT() {
        return unituserMapper.selectHT();
    }

    @Override
    public List<Unituser> selectHHT() {
        return unituserMapper.selectHHT();
    }
}
