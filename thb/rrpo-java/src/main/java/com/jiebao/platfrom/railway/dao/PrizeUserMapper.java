package com.jiebao.platfrom.railway.dao;

import com.jiebao.platfrom.railway.domain.PrizeUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 一事一奖推送人意见表 Mapper 接口
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
public interface PrizeUserMapper extends BaseMapper<PrizeUser> {
    /**
     * 发送人保存到数据库
     *
     * @param prizeId  一事一奖内容ID
     * @param sendDept 接收的组织机构
     * @return
     */
    @Insert("INSERT INTO `rail_prize_user` (prize_id,send_dept) VALUES (#{prizeId},#{sendDept})")
    boolean saveByDept(String prizeId, String sendDept);

    /**
     * 根据prizeId改状为已删除态
     *
     * @param prizeId
     * @return
     */
    @Delete("UPDATE  rail_prize set status = '4' where id = #{prizeId}")
    boolean ByPrizeId(String prizeId);

    /**
     * 发表审核意见
     *
     * @param prizeId
     * @param deptId
     * @param auditOpinion
     * @param money
     * @return
     */
    @Update("UPDATE `rail_prize_opinion` set audit_opinion = #{auditOpinion} ,money = #{money} where prize_id =#{prizeId} and send_dept = #{deptId}")
    boolean updateByPrizeId(String prizeId, String deptId, String auditOpinion, String money);


    /**
     * 删除接收人
     *
     * @param prizeId
     * @return
     */
    @Delete("DELETE FROM rail_prize_user WHERE prize_id =#{prizeId}")
    boolean deleteByPrizeId(String prizeId);


    /**
     * 把意见金额设置为null
     *
     * @param prizeId
     * @param deptId
     * @return
     */
    @Update("UPDATE `rail_prize_opinion` set audit_opinion = null,money = null where prize_id =#{prizeId} and send_dept = #{deptId}")
    boolean deleteOpinion(String prizeId, String deptId);

    @Update("UPDATE  rail_prize_user  r set  r.creat_time = now() WHERE r.prize_id =#{prizeId}")
    boolean setCreatTime(String prizeId);

    @Delete("")
    boolean reject(String prizeId);
}
