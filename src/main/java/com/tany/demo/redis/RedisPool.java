package com.tany.demo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class RedisPool {
    private static Logger logger = LoggerFactory.getLogger(RedisPool.class);
    private static final String REDIS_CONFIG_FILE = "redis.properties";

    private static JedisPool pool;

    public RedisPool(Map redisConfig) throws Exception {
        if (pool == null) {
            synchronized (RedisPool.class) {
                if (pool == null) {
                    initPool(redisConfig);
                }
            }
        }
    }

    /**
     * 获取连接实例
     *
     * @return
     * @throws Exception
     */
    public JedisCommands getConnection() throws Exception {
        if (pool == null) {
            throw new Exception("redis pool is uninitialized");
        }
        return pool.getResource();
    }

    /**
     * 返还连接实例到连接池
     *
     * @param jedisCommands
     */
    public static void retuenResource(JedisCommands jedisCommands) {
        if (jedisCommands != null) {
            try {
                ((Closeable) jedisCommands).close();
            } catch (IOException e) {
                logger.error("Failed to close (return) instance to pool");
            }
        }
    }

    /**
     * 获取连接池
     *
     * @return
     * @throws IOException
     */
    private void initPool(Map redisConfig) throws Exception {
        Properties prop = new Properties();
        prop.putAll(redisConfig);

        String host = prop.getProperty("redis.host", "localhost");
        int port = Integer.parseInt(prop.getProperty("redis.port", "6379"));
        String password = prop.getProperty("redis.password", "");
        if (Objects.equals(password, "")) {
            password = null;
        }
        int databaseIndex = Integer.parseInt(prop.getProperty("redis.database", "1"));

        int timeout = Integer.parseInt(prop.getProperty("redis.timeout", "3000"));
        int maxTotal = Integer.parseInt(prop.getProperty("redis.pool.maxTotal", "8"));
        int maxIdle = Integer.parseInt(prop.getProperty("redis.pool.maxIdle", "8"));
        int maxWaitMillis = Integer.parseInt(prop.getProperty("redis.pool.maxWaitMillis", "3000"));
        boolean testOnBorrow =
                Boolean.parseBoolean(prop.getProperty("redis.pool.testOnBorrow", "false"));

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        // 如果赋值为-1，则表示不限制；如果pool已经分配了maxTotal个jedis实例，则此时pool的状态为exhausted(耗尽)。
        poolConfig.setMaxTotal(maxTotal);
        // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        poolConfig.setMaxIdle(maxIdle);
        // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        poolConfig.setTestOnBorrow(testOnBorrow);

        pool = new JedisPool(poolConfig, host, port, timeout, password, databaseIndex);
    }
}
