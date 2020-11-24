package com.jiebao.platfrom.common.function;

import com.jiebao.platfrom.common.exception.RedisConnectException;

@FunctionalInterface
public interface JedisExecutor<T, R> {
    R excute(T t) throws RedisConnectException;
}
