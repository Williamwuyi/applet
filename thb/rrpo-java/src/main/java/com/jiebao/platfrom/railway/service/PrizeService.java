package com.jiebao.platfrom.railway.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.railway.domain.Exchange;
import com.jiebao.platfrom.railway.domain.Prize;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 一事一奖内容表 服务类
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
public interface PrizeService extends IService<Prize> {
    /**
     * 查询发件箱
     * @param request
     * @param prize
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Prize> getPrizeList(QueryRequest request, Prize prize , String startTime, String endTime);

    /**
     * 查询收件箱
     * @param request
     * @param prize
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<Prize> getPrizeInboxList(QueryRequest request, Prize prize , String startTime, String endTime,String [] statuses);


    /**
     *  简报数据
     * @param prize
     * @param startTime
     * @param endTime
     * @return
     */

    IPage<Prize>  getBriefing( QueryRequest request, Prize prize , String startTime, String endTime);

    /**
     * 统计十四个市州发布情况
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    List<Map<String, Object>> countRelease(String startTime, String endTime,Integer status);

    /**
     * 统计十四个市州金额
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String, Object>> countMoney(String startTime, String endTime);

    /**
     * 统计发布情况
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String, Object> countTypes(String startTime, String endTime);
}
