package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.railway.domain.InformUser;

/**
 * @author yf
 */
public interface InformUserService extends IService<InformUser> {


    /**
     * 删除收件箱记录
     * @param sendUserId 收件人
     * @param informId  通知公告Id
     * @return
     */
    boolean removeBySendUserId(String sendUserId,String informId);
}
