package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.railway.domain.PrizeOpinion;
import com.jiebao.platfrom.railway.domain.PrizeUser;

/**
 * <p>
 * 一事一奖推送人意见表 服务类
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
public interface PrizeOpinionService extends IService<PrizeOpinion> {

   boolean saveByPrizeId(String prizeId,String opinionMoney,Integer rank);

}
