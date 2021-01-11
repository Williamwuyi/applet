package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.Exchange;


/**
 * @author yf
 */
public interface ExchangeService extends IService<Exchange> {
    /**
     * 发件箱
     * @param request
     * @param exchange
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Exchange> getExchangeList(QueryRequest request, Exchange exchange, String startTime, String endTime);

    /**
     * 收件箱
     * @param request
     * @param exchange
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Exchange> getExchangeInboxList(QueryRequest request, Exchange exchange, String startTime, String endTime);

    /**
     * 查询当前登录人未发送和已发送的信息互递给年度考核
     * @param request
     * @param exchange
     * @param id
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Exchange> getExchangeListForCheck(QueryRequest request, Exchange exchange,String  id, String startTime, String endTime);
}
