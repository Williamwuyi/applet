package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.Briefing;

import java.util.Map;

/**
 * @author yf
 */
public interface BriefingService extends IService<Briefing> {
    /**
     * 发件箱
     *
     * @param request
     * @param briefing
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Briefing> getBriefingList(QueryRequest request, Briefing briefing, String startTime, String endTime);

    /**
     * 收件箱
     *
     * @param request
     * @param briefing
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Briefing> getBriefingInboxList(QueryRequest request, Briefing briefing, String startTime, String endTime);

    /**
     * 查询当前登录人未发送和已发送的护路简报给年度考核
     *
     * @param request
     * @param briefing
     * @param id
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Briefing> getBriefingListForCheck(QueryRequest request, Briefing briefing, String id, String startTime, String endTime);

    /**
     * 查看
     *
     * @param request
     * @param briefing
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Briefing> countList(QueryRequest request, Briefing briefing, String startTime, String endTime);

    /**
     * 根据十四个市州查询对应简报
     * @param  : startTime, endTime, DeptId, title
     * @return
     */
    IPage<Briefing> countListByCity(Integer pageNum,Integer pageSize,String startTime,String endTime,String deptName,String title);
}
