package com.tany.demo.redis;

import com.tany.demo.Utils.ConfigLoader;
import com.tany.demo.Utils.Constants;
import org.testng.annotations.Test;
import redis.clients.jedis.JedisCommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MutilTreadRedisTest {

    @Test
    public static void dropData() throws Exception{
        final RedisPool pool = new RedisPool(ConfigLoader.load(Constants.REDIS_CONFIG_FILE));
        JedisCommands redisCommands = pool.getConnection();
        String key = "topo_000_V10:RedisLoopSumStatisticsBolt_2:1234567890123";
        for (int i = 0; i < 100000; i++){
            String str = String.format("%05d", i);
            String keyall = key+str;
            redisCommands.zremrangeByScore(keyall,1531122017404L,1531123912954L);
        }
        RedisPool.retuenResource(redisCommands);
    }

    @Test
    public static void makeData() throws Exception{
        final RedisPool pool = new RedisPool(ConfigLoader.load(Constants.REDIS_CONFIG_FILE));
        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(100);

        for (int i = 1; i < 50; i++) {
            final Random ran = new Random();
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        int count = 0;
                        String key = "topo_000_V10:RedisLoopSumStatisticsBolt_2:1234567890123";
                        JedisCommands redisCommands = pool.getConnection();
                        int number = 0;
                        long timea = System.currentTimeMillis();
                        while (true){
                            long startTime = System.currentTimeMillis();
                            long endTime = startTime - 3600*1000;
                            int randomint = Math.abs(ran.nextInt()%100000);
                            int randomint2 = Math.abs(ran.nextInt()%100000);
                            String str = String.format("%05d", randomint);
                            String keyall = key+str;
                            redisCommands.zadd(keyall, startTime, String.format("%16d",randomint2));
                            Set<String> set = redisCommands.zrangeByScore(keyall, startTime, endTime);
                            Map<String, Integer> top = new HashMap();
                            for(String setItem :set){
                                if(top.containsKey(setItem)){
                                    top.put(setItem,top.get(setItem)+1);
                                }else {
                                    top.put(setItem,1);
                                }
                            }
                            number++;
                            if(number >= 10000 ){
                                long b = System.currentTimeMillis();
                                long a = b - timea;
                                timea = b;
                                System.out.println("input 10000!:"+String.valueOf(a));
                                number =0;
                                count++;
                                if (count >10){
                                    RedisPool.retuenResource(redisCommands);
                                    System.out.println("input all:"+String.valueOf(count));
                                    break;
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
