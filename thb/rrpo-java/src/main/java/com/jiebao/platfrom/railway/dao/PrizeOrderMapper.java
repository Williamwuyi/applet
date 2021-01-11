package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.Prize;
import com.jiebao.platfrom.railway.domain.PrizeOrder;
import com.jiebao.platfrom.railway.domain.PrizeType;
import org.apache.ibatis.annotations.Select;

/**
 * @author yf
 */
public interface PrizeOrderMapper extends BaseMapper<PrizeOrder> {

    /**
     * 获取当前登录用户的组织机构的发布记录，离当前时间最近的一条记录
     * @return
     */
    @Select("SELECT * FROM `rail_prize_order` where dept_id = #{deptId} ORDER BY release_time desc LIMIT 0,1")
    PrizeOrder selectRecent(String deptId);

    /**
     * 获取当前部门本月一共发布条数
     * @param deptId
     * @return
     */
    @Select("select COUNT(*) from rail_prize_order where date_format(creat_time,'%Y-%m') = date_format(now(),'%Y-%m') AND `dept_id` = #{deptId}")
    Integer getCountByDept(String deptId);

}
