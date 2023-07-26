package com.brick.yggdrasilserver.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CaffeineUtil {

    public static Cache<String, Object> tokenCache = Caffeine.newBuilder()
            .initialCapacity(10)
            .maximumSize(1024)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .recordStats()
            .build();

    public static void set(String k, Object v) {
        tokenCache.put(k, v);
    }

    public static void set(int type, String k, Object v) {
        tokenCache.put(k, v);
    }

    public static Object get(String k) {
        return tokenCache.getIfPresent(k);
    }
}
