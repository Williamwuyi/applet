package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.BriefingUser;

/**
 * @author yf
 */
public interface BriefingUserService extends IService<BriefingUser> {

    /**
     *  护路简报选定要发送的的用户存入数据库
     * @param briefingId 护路简报id
     * @param sendUserId  用户ID
     * @return
     */
        boolean saveByUserId(String briefingId, String sendUserId, String sendUserName);

    /**
     * 删除掉选定的用户
     * @param briefingId
     * @return
     */
    boolean deleteByBriefingId(String briefingId);


    /**
     * 根据护路简报ID和发送人删除收件箱记录(直接删除记录)
     * @param sendUserId
     * @param briefingId
     */
    boolean removeBySendUserId(String sendUserId, String briefingId);

    /**
     * 根据ID查接收info（发件箱）
     * @param request
     * @param briefingUser
     * @return
     */
    IPage<BriefingUser> getBriefingUserList(QueryRequest request, BriefingUser briefingUser);


    /**
     * 收件箱删除（移除至回收站，type状态为2）
     * @param sendUserId
     * @param briefingId
     * @return
     */
    boolean upDateBySendUserId(String sendUserId, String briefingId);
}
