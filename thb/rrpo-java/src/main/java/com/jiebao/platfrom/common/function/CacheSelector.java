package com.jiebao.platfrom.common.function;

@FunctionalInterface
public interface CacheSelector<T> {
    T select() throws Exception;
}
