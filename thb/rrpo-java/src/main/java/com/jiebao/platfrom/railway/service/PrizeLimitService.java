package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.PrizeLimit;
import com.jiebao.platfrom.railway.domain.PrizeOrder;

/**
 * @author yf
 */
public interface PrizeLimitService extends IService<PrizeLimit> {


    /**
     *
     * @param request
     * @param prizeLimit
     * @return
     */
    IPage<PrizeLimit> getPrizeLimitList(QueryRequest request, PrizeLimit prizeLimit );
}
