package com.tany.demo.redis.impl;

import com.tany.demo.redis.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service("redisService")
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


    @Override
    public void addSetValue(long alertId, long userId) {
        redisTemplate.opsForSet().add(CACHE_KEY_PREFIX_NEW_MSG + alertId, CACHE_KEY_PREFIX_USER_ID + userId);
    }

    @Override
    public Boolean getSetValue(long alertId, long userId) {
        boolean hasValue = redisTemplate.opsForSet().isMember(CACHE_KEY_PREFIX_NEW_MSG + alertId, CACHE_KEY_PREFIX_USER_ID + userId);
        return hasValue;
    }

    @Override
    public void removeSetValue(long alertId, long userId) {
        redisTemplate.opsForSet().remove(CACHE_KEY_PREFIX_NEW_MSG + alertId, CACHE_KEY_PREFIX_USER_ID + userId);
    }

    @Override
    public void removeSetAllValue(long alertId) {
        Set<String> memberSet = redisTemplate.opsForSet().members(CACHE_KEY_PREFIX_NEW_MSG + alertId);
        redisTemplate.opsForSet().remove(CACHE_KEY_PREFIX_NEW_MSG + alertId, memberSet.toArray());
    }
}
