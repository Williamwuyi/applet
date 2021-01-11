package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.ExchangeUser;

/**
 * @author yf
 */
public interface ExchangeUserService extends IService<ExchangeUser> {

    /**
     *  信息互递选定要发送的的用户存入数据库
     * @param exchangeId 信息互递id
     * @param sendUserId  用户ID
     * @return
     */
        boolean saveByUserId(String exchangeId,String sendUserId,String sendUserName);

    /**
     * 删除掉选定的用户
     * @param exchangeId
     * @return
     */
    boolean deleteByExchangeId(String exchangeId);


    /**
     * 根据信息互递ID和发送人删除收件箱记录(回收站删除)
     * @param sendUserId
     * @param exchangeId
     */
    boolean removeBySendUserId(String sendUserId,String exchangeId);

    /**
     * 根据ID查接收info（发件箱）
     * @param request
     * @param exchangeUser
     * @return
     */
    IPage<ExchangeUser> getExchangeUserList(QueryRequest request, ExchangeUser exchangeUser);


    /**
     * 回复意见
     * @return
     */
    boolean updateByExchangeId(String SendUserId ,String exchangeId,String opinion);

    /**
     * 收件箱删除
     * @param sendUserId
     * @param exchangeId
     * @return
     */
    boolean upDateBySendUserId(String sendUserId,String exchangeId);
}
