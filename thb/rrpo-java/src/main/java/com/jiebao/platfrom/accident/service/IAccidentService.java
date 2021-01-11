package com.jiebao.platfrom.accident.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.accident.daomain.Accident;
import com.jiebao.platfrom.accident.daomain.CompareTable;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-08-04
 */
public interface IAccidentService extends IService<Accident> {
    JiebaoResponse list(QueryRequest queryRequest, String policeId, String cityLevelId, String quDeptId, String startDate, String endDate);//查询

    JiebaoResponse map(String policeId, String cityLevelId, String startDate, String endDate, String quDeptId);//地图视角

    JiebaoResponse lock(String[] accidentId, String month, Integer status);

    String func(String nature, String instationSection, String road, String age, String closed, String jzd, String distance, String identity, String conditions);

    List<CompareTable> compareTable(String startYear, String endYear);
}
