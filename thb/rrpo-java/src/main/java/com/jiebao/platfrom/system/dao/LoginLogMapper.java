package com.jiebao.platfrom.system.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiebao.platfrom.system.domain.LoginCount;
import com.jiebao.platfrom.system.domain.LoginLog;
import com.jiebao.platfrom.system.domain.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 获取系统总访问次数
     *
     * @return Long
     */
    Long findTotalVisitCount();

    /**
     * 获取系统今日访问次数
     *
     * @return Long
     */
    Long findTodayVisitCount();

    /**
     * 获取系统今日访问 IP数
     *
     * @return Long
     */
    Long findTodayIp();

    /**
     * 获取系统近七天来的访问记录
     *
     * @param user 用户
     * @return 系统近七天来的访问记录
     */
    List<Map<String, Object>> findLastSevenDaysVisitCount(User user);


    @Select("select count(1) as number,#{deptName} as deptName,#{deptId} as deptId  from sys_login_log ${ew.customSqlSegment}")
    LoginCount loginCount(@Param("ew") QueryWrapper<LoginLog> ew, @Param("deptName") String deptName, @Param("deptId") String deptId);  // 精确到组织结构

    @Select("select count(1)  from sys_login_log where login_time>=#{startDate} and login_time<=#{endDate} and dept_id=#{deptId}")
    Integer loginCountWeek(String startDate, String endDate, @Param("deptId") String deptId);  // 精确到组织结构

    @Select("select * from sys_login_log ${ew.customSqlSegment}")
    List<LoginLog> loginCountUser(Page page, @Param("ew") QueryWrapper<LoginLog> ew);

    @Select("select count(1) as number,(select dept_name from sys_dept where dept_id=#{deptId}) as deptName,#{deptId} as deptId from sys_login_log where dept_id=#{deptId} and login_time like '${yearMonth}%'")
    LoginCount loginCountPrent(@Param("deptId") String deptId, String yearMonth);//
}