package com.jiebao.platfrom.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.common.domain.JiebaoResponse;
import com.jiebao.platfrom.common.domain.QueryRequest;
import com.jiebao.platfrom.system.domain.LoginLog;

import java.time.LocalDateTime;
import java.util.Date;

public interface LoginLogService extends IService<LoginLog> {

    void saveLoginLog(LoginLog loginLog);

    JiebaoResponse lists(String deptParentId,Integer year,Integer month);

    JiebaoResponse listUsers(QueryRequest queryRequest , String deptId, String startDate, String endDate);

    JiebaoResponse selectWeekCount(Integer year,Integer month);

    JiebaoResponse weekList(Integer year, Integer month,String deptId);

    JiebaoResponse yearLoginCount(String DeptId, Integer number,String year);
}
