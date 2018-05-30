package com.tany.demo.redis;

import com.tany.demo.Utils.ConfigLoader;
import com.tany.demo.Utils.Constants;
import org.testng.annotations.Test;
import redis.clients.jedis.JedisCommands;

import java.util.HashMap;
import java.util.Map;

public class RedisPoolTest {
    @Test
    public void insertdata() throws Exception {
        RedisPool pool = new RedisPool(ConfigLoader.load(Constants.REDIS_CONFIG_FILE));
        JedisCommands jedis = pool.getConnection();
        Map<String, Double> scoreMembers = new HashMap();
        scoreMembers.put("test1",1.0);

        jedis.zadd("test",scoreMembers);
        System.out.println(jedis.zscore("test","test1"));
        jedis.sadd("test2","test1");
        System.out.println(jedis.sismember("test2","test*"));
        RedisPool.retuenResource(jedis);
    }
}
