package com.jiebao.platfrom.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.domain.Dict;

import java.util.List;


public interface DictService extends IService<Dict> {



    JiebaoResponse createNewDict(Dict dict);

    boolean updateNewDict(Dict dict);

    IPage<Dict> getListTable(QueryRequest queryRequest, Dict dict);

    IPage<Dict> getListTableTwo(QueryRequest queryRequest, Dict dict);

  /*  IPage<Dict> findDicts(QueryRequest request, Dict dict);

    void createDict(Dict dict);

    void updateDict(Dict dicdt);

    void deleteDicts(String[] dictIds);*/

}
