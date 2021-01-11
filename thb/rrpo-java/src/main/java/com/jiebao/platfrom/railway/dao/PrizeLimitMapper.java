package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.PrizeLimit;
import com.jiebao.platfrom.railway.domain.PrizeType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yf
 */
public interface PrizeLimitMapper extends BaseMapper<PrizeLimit> {


    /**
     * 查询deptId是否存在
     * @param deptId
     * @return
     */
    @Select("SELECT  COUNT(*) FROM rail_prize_limit WHERE dept_id = #{deptId}")
    Integer selectDeptId(String deptId);
}
