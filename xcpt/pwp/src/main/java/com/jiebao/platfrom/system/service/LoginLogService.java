package com.jiebao.platfrom.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiebao.platfrom.system.domain.LoginLog;

public interface LoginLogService extends IService<LoginLog> {

    void saveLoginLog (LoginLog loginLog);
}
