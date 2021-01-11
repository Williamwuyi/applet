package com.jiebao.platfrom.accident.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.accident.daomain.CountTable;
import com.jiebao.platfrom.accident.daomain.Jk;
import com.jiebao.platfrom.common.domain.QueryRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qta
 * @since 2020-11-07
 */
public interface IJkService extends IService<Jk> {
    IPage<Jk> listPage(QueryRequest queryRequest, String gac, String dzs,String xsq,String lineName,String year);

    boolean importExcel(HttpServletResponse response, String gac, String dzs,String xsq,String lineName,String year);

    boolean addOrUpdate(Jk jk, Collection<? extends Serializable> tp);


    boolean delete(String[] idList);

    List<CountTable> countTable(String year,String xlmc);  //统计  安装监控数据
}
