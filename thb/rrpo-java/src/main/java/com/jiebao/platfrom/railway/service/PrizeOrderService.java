package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.railway.domain.Prize;
import com.jiebao.platfrom.railway.domain.PrizeOrder;
import com.jiebao.platfrom.railway.domain.PrizeType;

/**
 * @author yf
 */
public interface PrizeOrderService extends IService<PrizeOrder> {

    /**
     * 获取当前用户的组织机构的发布记录，离当前时间最近的一条记录
     * @return
     */
    PrizeOrder selectRecent(String deptId);

    /**
     * 获取当前部门本月一共发布条数
     * @param deptId
     * @return
     */
    Integer getCountByDept(String deptId);
}
