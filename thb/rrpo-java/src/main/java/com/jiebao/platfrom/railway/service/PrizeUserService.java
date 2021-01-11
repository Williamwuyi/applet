package com.jiebao.platfrom.railway.service;

import com.jiebao.platfrom.railway.domain.PrizeUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 一事一奖推送人意见表 服务类
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
public interface PrizeUserService extends IService<PrizeUser> {

    /**
     * 发送组织机构保存到数据库
     *
     * @param prizeId  一事一奖内容ID
     * @param sendDept 接收的组织机构
     * @return
     */
    boolean saveByDept(String prizeId, String sendDept);

    /**
     * 根据prizeId删除相应的接收人
     *
     * @param prizeId
     * @return
     */
    boolean ByPrizeId(String prizeId);

    /**
     * 接收人发表或修改意见
     *
     * @param prizeId
     * @param deptId       组织机构
     * @param auditOpinion 意见
     * @param money        钱
     * @return
     */
    boolean updateByPrizeId(String prizeId, String deptId, String auditOpinion, String money);

    /**
     * 删除接收人
     *
     * @param prizeId
     * @return
     */
    boolean deleteByPrizeId(String prizeId);

    /**
     * 删除意见
     *
     * @param prizeId
     * @param deptId
     * @return
     */
    boolean deleteOpinion(String prizeId, String deptId);

}
