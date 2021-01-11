package com.jiebao.platfrom.accident.service;

import com.jiebao.platfrom.accident.daomain.Case;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-08-05
 */
public interface ICaseService extends IService<Case> {
    JiebaoResponse list(QueryRequest queryRequest, String police, String cityLevel,String quDeptId, String lineId, String nature, String startDate, String endDate);

    JiebaoResponse map(String startDate, String endDate, Integer status);//地图视角

    JiebaoResponse lock(String[] caseId,String month,Integer status);
}
