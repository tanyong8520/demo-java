package com.tany.demo.HBase;

import com.tany.demo.Hbase.HbaseDataType;
import com.tany.demo.Hbase.PhoenixDAO;
import com.tany.demo.Utils.ConfigLoader;
import com.tany.demo.Utils.Constants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class HbaseTest {

    private PhoenixDAO dao;
    private String testTable;

    @BeforeClass
    public void init() throws IOException {
        dao = new PhoenixDAO(ConfigLoader.load(Constants.PHOENIX_CONFIG_FILE));
        testTable = "tany123";
    }

    @Test
    public void createTableTest() throws SQLException {
        dao.createTable(testTable, new LinkedHashMap<String, HbaseDataType>() {
            {
                put("id", HbaseDataType.INTEGER);
                put("name", HbaseDataType.VARCHAR);
                put("groups", HbaseDataType.VARCHAR);
                put("sex", HbaseDataType.VARCHAR);
                put("age", HbaseDataType.INTEGER);
                put("address", HbaseDataType.VARCHAR);
            }
        }, Arrays.asList("id", "name", "groups"), 3600);
    }

    @Test
    public void upsertTest() throws SQLException {
        dao.upsert(testTable, new HashMap<String, Object>() {
            {
                put("id", 126);
                put("name", "name4");
                put("groups", "group4");
                put("age", 23);
                put("sex", "男");
                put("address", "西安市");
            }
        });
    }

    @Test
    public void upsertBatchTest() throws SQLException {
        List<Map<String, Object>> datas = new ArrayList<>();
        datas.add(new HashMap<String, Object>() {
            {
                put("id", 123);
                put("name", "name1");
                put("groups", "group1");
                put("age", 23);
                put("sex", "男");
            }
        });

        datas.add(new HashMap<String, Object>() {
            {
                put("id", 124);
                put("name", "name2");
                put("groups", "group2");
                put("age", 27);
                put("sex", "男");
            }
        });

        datas.add(new HashMap<String, Object>() {
            {
                put("id", 125);
                put("name", "name3");
                put("groups", "group3");
                put("age", 25);
                put("sex", "女");
            }
        });
        dao.upsertBatch(testTable, datas);
    }

    @Test
    public void queryTest() throws SQLException {
        List<Map<String, Object>> list =
                dao.query("select * from test123 where id=?", new Object[]{123});
        System.out.println(list);

        Assert.assertNotNull(list);
    }

    @Test
    public void queryByKeysTest() throws SQLException {
        Map<String, Object> keys = new HashMap<String, Object>() {
            {
                put("id", 123);
                put("groups", "group1");
                put("name", "name1");
            }
        };

        Map<String, Object> retMap = dao.queryByKeys(testTable, keys);
        System.out.println(retMap);
    }

    @Test
    public void deleteTest() throws SQLException {
        Map<String, Object> keys = new HashMap<>();
        keys.put("id", 123);
        dao.delete(testTable, keys);
    }

    @Test
    public void deleteTableTest() throws SQLException {
        dao.deleteTable(testTable);
    }
}
