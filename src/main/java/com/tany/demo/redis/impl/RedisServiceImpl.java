package com.tany.demo.redis.impl;

import com.tany.demo.redis.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean hasNewMsg(long alertId) {
        return redisTemplate.hasKey(CACHE_KEY_PREFIX_NEW_MSG + alertId);
    }

    @Override
    public void setNewMsg(long alertId) {
        redisTemplate.opsForValue().set(CACHE_KEY_PREFIX_NEW_MSG + alertId, "");
    }

    @Override
    public void deleteNewMsg(long alertId) {
        redisTemplate.delete(CACHE_KEY_PREFIX_NEW_MSG + alertId);
    }

    @Override
    public int addAndGetUsedTimes(String alertId, int num) {
        return redisTemplate.opsForValue().increment(CACHE_KEY_PREFIX_USED_TIMES + alertId, num).intValue();
    }

    @Override
    public void resetUsedTimes() {
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX_USED_TIMES + "*");
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public void set(String key, String value) {
        set(key, value, -1);
    }

    @Override
    public void set(String key, String value, long expire) {
        if (expire < 0) {
            redisTemplate.opsForValue().set(key, value);
        } else {
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
        }
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public double increment(String key, double num) {
        return redisTemplate.opsForValue().increment(key, num);
    }

    @Override
    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void deleteBatch(Collection<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
