package com.jiebao.platfrom.common.task;

import com.jiebao.platfrom.common.domain.JiebaoConstant;
import com.jiebao.platfrom.common.service.RedisService;
import com.jiebao.platfrom.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 主要用于定时删除 Redis中 key为 jiebao.user.active 中
 * 已经过期的 score
 */
@Slf4j
@Component
public class CacheTask {

    @Autowired
    private RedisService redisService;

    @Scheduled(fixedRate = 3600000)
    public void run() {
        try {
            String now = DateUtil.formatFullTime(LocalDateTime.now());
            redisService.zremrangeByScore(JiebaoConstant.ACTIVE_USERS_ZSET_PREFIX, "-inf", now);
            log.info("delete expired user");
        } catch (Exception ignore) {
        }
    }
}
