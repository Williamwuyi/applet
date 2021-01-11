package com.jiebao.platfrom.railway.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiebao.platfrom.railway.domain.BriefingCount;
import com.jiebao.platfrom.railway.domain.BriefingUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author yf
 */
public interface BriefingCountMapper extends BaseMapper<BriefingCount> {

    /**
     * 统计每个市州被填报的数量
     * @return
     */
    List<Map<String, Object>> countCity(String startTime ,String endTime);

    List<Map<String, Object>> countCityByI(String startTime ,String endTime,String userName);
}
