package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.ExchangeFile;

import java.util.List;

/**
 * @author yf
 */
public interface ExchangeFileService extends IService<ExchangeFile> {

    IPage<ExchangeFile> getFileList(QueryRequest request);

    void updateByKey(ExchangeFile files);

    List<ExchangeFile> getByParentsId(String parentsId);

    List<ExchangeFile> findByUser(String userId);

    List<ExchangeFile> getByExchangeId(String exchangeId);
}
