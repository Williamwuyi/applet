package com.jiebao.platfrom.accident.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.accident.daomain.Accident;
import com.jiebao.platfrom.accident.daomain.Ql;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-11-07
 */
public interface IQlService extends IService<Ql> {
    IPage<Ql> listPage(QueryRequest queryRequest, String deptName, String policeName, String gwdName,String qxDeptName);

    boolean importExcel(HttpServletResponse response, String deptName, String policeName, String gwdName,String qxDeptName);

    boolean addOrUpdate(Ql ql, Collection<? extends Serializable> tp);

    boolean delete(String[] idList);

}
