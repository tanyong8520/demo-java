package com.tany.demo.redis;

import java.util.Collection;
import java.util.Set;

public interface RedisService {
    String CACHE_KEY_PREFIX_BASE = "tany:";
    String CACHE_KEY_PREFIX_NEW_MSG = CACHE_KEY_PREFIX_BASE + "hasMsg:";
    String CACHE_KEY_PREFIX_USED_TIMES = "usedTimes:";
    String CACHE_KEY_PREFIX_USER_ID = "userId:";

    /**
     * 是否有新消息
     *
     * @param alertId
     * @return
     */
    boolean hasNewMsg(long alertId);

    /**
     * 设置新消息标志
     *
     * @param alertId
     */
    void setNewMsg(long alertId);

    /**
     * 删除新消息标志
     *
     * @param alertId
     */
    void deleteNewMsg(long alertId);

    /**
     * 累计预警使用次数并返回最新结果
     *
     * @param alertId 预警id
     * @param num
     * @return
     */
    int addAndGetUsedTimes(String alertId, int num);

    /**
     * 重置所有预警使用次数
     */
    void resetUsedTimes();

    /**
     * 设置新缓存
     *
     * @param key
     * @param value
     */
    void set(String key, String value);

    /**
     * 设置新缓存(设置有效期)
     *
     * @param key
     * @param value
     * @param expire key过期时间，单位秒
     */
    void set(String key, String value, long expire);

    /**
     * 获取结果
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 将对应的value加上指定的值并返回新值，如key不存在则返回传入的值
     *
     * @param key
     * @param num 需要在原有value上累加的值
     * @return
     */
    double increment(String key, double num);

    /**
     * 获取key
     *
     * @param pattern 通配符
     * @return
     */
    Set<String> getKeys(String pattern);

    /**
     * 删除
     *
     * @param key
     */
    void delete(String key);

    /**
     * 批量删除
     *
     * @param keys
     */
    void deleteBatch(Collection<String> keys);


    /**
     * set添加新元素
     *
     * @param alertId
     * @param userId 需要在set中添加新的用户
     */
    void addSetValue(long alertId, long userId);

    /**
     * set查看元素
     *
     * @param alertId
     * @param userId 查看set中知否有该用户
     */
    Boolean getSetValue(long alertId, long userId);

    /**
     * set删除元素
     *
     * @param alertId
     * @param userId 查看set中知否有该用户
     */
    void removeSetValue(long alertId, long userId);

    /**
     * set删除元素
     *
     * @param alertId
     */
    void removeSetAllValue(long alertId);
}
