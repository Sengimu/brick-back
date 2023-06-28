package com.brick.yggdrasilserver.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void set(String k, String v, long sec) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        if (sec > 0) {
            valueOperations.set(k, v, Duration.ofSeconds(sec));
        } else {
            valueOperations.set(k, v, Duration.ofSeconds(stringRedisTemplate.getExpire(k, TimeUnit.SECONDS)));
        }
    }

    public String get(String k) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(k);
    }

    public void hSet(String k, Map<String, Object> v, long sec) {
        HashOperations<String, String, Object> hashOperations = stringRedisTemplate.opsForHash();
        hashOperations.putAll(k, v);
        if (sec > 0) {
            stringRedisTemplate.expire(k, Duration.ofSeconds(sec));
        }
    }

    public String hGet(String k, String hk) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        return hashOperations.get(k, hk) != null ? hashOperations.get(k, hk) : null;
    }

    public void lSet(String k, String v, long sec) {
        ListOperations<String, String> listOperations = stringRedisTemplate.opsForList();
        listOperations.rightPush(k, v);
        if (sec > 0) {
            stringRedisTemplate.expire(k, Duration.ofSeconds(sec));
        }
    }

    public List<String> lGetAll(String k) {
        if (!exist(k)) {
            return null;
        }

        ListOperations<String, String> listOperations = stringRedisTemplate.opsForList();
        return listOperations.leftPop(k, listOperations.size(k));
    }

    public boolean exist(String k) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(k));
    }

    public boolean del(String k) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(k));
    }
}
