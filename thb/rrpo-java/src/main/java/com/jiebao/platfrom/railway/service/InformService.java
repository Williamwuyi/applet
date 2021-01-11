package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.Inform;

import java.util.Date;
import java.util.List;

/**
 * @author yf
 */
public interface InformService extends IService<Inform> {

    /**
     * 发件箱
     * @param request
     * @param inform
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Inform> getInformList(QueryRequest request, Inform inform, String startTime, String endTime);

    /**
     * 收件箱
     * @param request
     * @param inform
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Inform> getInformInboxList(QueryRequest request, Inform inform, String startTime, String endTime);

    void updateByKey(Inform inform);

    List<Inform> getInformLists(Inform inform, QueryRequest request);

    /**
     * 撤回
     * @param informId
     * @return
     */
    boolean revoke(String informId);

    /**
     * 发布
     * @param informId
     * @return
     */
    boolean release(String informId);
}
