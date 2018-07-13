package com.tany.demo.ElasticSerch;

import com.tany.demo.elasticSearch.ElasticSerchService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElasticSerchTest {
    ElasticSerchService elasticSerchService ;

//    @BeforeClass
    public void init() {
        elasticSerchService = new ElasticSerchService("twitter", "tweet");
        elasticSerchService.getESClient();
    }

    @Test
    public void insertESInfoTest(){
        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(100);
        for (int i = 1; i < 50; i++) {
            final Random ran = new Random();
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    String idString = "id"+"-"+ Math.abs(ran.nextInt()%100)+"-"+ Math.abs(ran.nextInt()%100);
                    elasticSerchService = new ElasticSerchService("twitter", "tweet");
                    elasticSerchService.getESClient();
                    for(int i=0; i<100;i++ ){
                        List<Map<String,Object>> data = new ArrayList<>();
                        for(int j = 0; j<1000; j++){
                            Map<String ,Object> dataitem  = new HashMap<>();
                            dataitem.put("id",idString+String.format("%03d", i)+"-"+String.format("%04d", j));
                            dataitem.put("user","test"+String.format("%04d", j));
                            dataitem.put("postDate", new Date());
                            dataitem.put("postLong", System.currentTimeMillis());
                            dataitem.put("message", "trying to out "+String.format("%04d", j));

                            data.add(dataitem);
                        }
                        try {
                            elasticSerchService.testBulk(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

}
