package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.PrizeOpinion;
import com.jiebao.platfrom.railway.domain.PrizeType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author yf
 */
public interface PrizeOpinionMapper extends BaseMapper<PrizeOpinion> {

    /**
     * 判断是否存在 ，以防止重复提交
     * @param rank
     * @param prizeId
     * @return
     */
    @Select("SELECT count(*) FROM `rail_prize_opinion` WHERE rank =#{rank} and prize_id =#{prizeId}")
    Integer selectOpinion(Integer rank,String prizeId);

    /**
     *  意见分配金额
     * @param prizeId
     * @param opinionMoney
     * @param rank
     * @return
     */
    @Update("UPDATE rail_prize_opinion SET money =#{opinionMoney} WHERE prize_id =#{prizeId} and rank = #{rank}")
    boolean saveByPrizeId(String prizeId, String opinionMoney,Integer rank);
}
