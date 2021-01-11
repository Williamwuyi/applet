package com.jiebao.platfrom.railway.dao;

import com.jiebao.platfrom.railway.domain.Prize;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 一事一奖内容表 Mapper 接口
 * </p>
 *
 * @author yf
 * @since 2020-07-24
 */
public interface PrizeMapper extends BaseMapper<Prize> {

    /**
     * 发布，status改为3
     *
     * @param prizeId
     * @return
     */
    @Update("UPDATE  rail_prize set status = 3,release_time = now() where id = #{prizeId}")
    boolean release(String prizeId);

    /**
     * 驳回
     *
     * @param prizeId
     * @return
     */
    @Update("UPDATE rail_prize SET status = 2 WHERE id =#{prizeId}")
    boolean reject(String prizeId);


    @Update("UPDATE rail_prize SET status = 8 WHERE id =#{prizeId}")
    boolean updateStatusForPro(String prizeId);

    @Update("UPDATE rail_prize SET status = 7 WHERE id =#{prizeId}")
    boolean updateStatusForIron(String prizeId);

    @Update("UPDATE rail_prize SET status = 6 WHERE id =#{prizeId}")
    boolean updateStatusForCity(String prizeId);

    @Update("UPDATE rail_prize SET status = 5 WHERE id =#{prizeId}")
    boolean updateStatusForQu(String prizeId);

    @Select("SELECT * FROM rail_prize WHERE `status`=8 AND #{startTime} <= release_time  and release_time <= #{endTime}")
    List<Prize> selectByBriefing(String startTime, String endTime);


    @Select("SELECT max(number) FROM rail_prize")
    Integer findMaxNumber();

    /**
     * 统计十四个市州发布情况
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    List<Map<String, Object>> countRelease(String startTime, String endTime ,Integer status);

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

    /**
     * 统计事故类型
     * @return
     */
     List<Map<String,Object>> countType(String startTime, String endTime);

    /**
     * 统计市下发生条数
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> countPrizeForCity(String startTime, String endTime,String deptId);

    /**
     * 统计市下发生完结条数
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> countPrizeForCityDone(String startTime, String endTime,String deptId);

    /**
     * 统计区下发生条数
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> countPrizeForArea(String startTime, String endTime,String deptId);

    /**
     * 统计区下完结条数
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> countPrizeForAreaDone(String startTime, String endTime,String deptId);

    /**
     * 统计市下发生金额
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> countMoneyForCity(String startTime, String endTime,String deptId);



    /**
     * 统计区下发生金额
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> countMoneyForArea(String startTime, String endTime,String deptId);



    /**
     * 统计市下事件类型
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> countTypeForCity(String startTime, String endTime,String deptId);


    /**
     * 统计区下事件类型
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> countTypeForArea(String startTime, String endTime,String deptId);



    /**
     *
     * @param prizeId
     * @param value
     * @return
     */

       @Update("UPDATE rail_prize SET city_status = #{value} WHERE id =#{prizeId}")
       boolean updateCityStatus(String prizeId , String value);

    @Update("UPDATE rail_prize SET gong_status = #{value} WHERE id =#{prizeId}")
    boolean updateGongStatus(String prizeId , String value);

    @Update("UPDATE rail_prize SET if_import = 1 WHERE status = 8")
    boolean updateAll();
}

