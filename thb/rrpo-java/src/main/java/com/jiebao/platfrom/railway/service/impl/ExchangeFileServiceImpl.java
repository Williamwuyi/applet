package com.jiebao.platfrom.railway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.dao.ExchangeFileMapper;
import com.jiebao.platfrom.railway.domain.ExchangeFile;
import com.jiebao.platfrom.railway.service.ExchangeFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service("ExchangeFileService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ExchangeFileServiceImpl extends ServiceImpl<ExchangeFileMapper, ExchangeFile> implements ExchangeFileService {

    @Autowired
    ExchangeFileMapper fileMapper;

    @Override
    public IPage<ExchangeFile> getFileList(QueryRequest request) {
        LambdaQueryWrapper<ExchangeFile> lambdaQueryWrapper = new LambdaQueryWrapper();
        Page<ExchangeFile> page = new Page<>(request.getPageNum(), request.getPageSize());
        lambdaQueryWrapper.orderByDesc(ExchangeFile::getId);
        return this.baseMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    @Transactional
    public void updateByKey(ExchangeFile files) {
        this.fileMapper.updateById(files);
    }

    @Override
    public List<ExchangeFile> getByParentsId(String parentsId) {
        return fileMapper.getByParentsId(parentsId);
    }

    @Override
    public List<ExchangeFile> findByUser(String userId) {
        return fileMapper.findByUser(userId);
    }


    @Override
    public List<ExchangeFile> getByExchangeId(String exchangeId) {
        return fileMapper.getByExchangeId(exchangeId);
    }
}
