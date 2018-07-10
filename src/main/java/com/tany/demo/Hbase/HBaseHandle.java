package com.tany.demo.Hbase;

import com.tany.demo.Utils.ConfigLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
//import org.apache.hadoop.hbase.client.coprocessor.LongStrColumnInterpreter;
import org.apache.hadoop.hbase.coprocessor.ColumnInterpreter;
import org.apache.hadoop.hbase.util.Bytes;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class HBaseHandle {
    private static final Logger LOG = LoggerFactory.getLogger(HBaseHandle.class);

    private static final String CONFIG_FILE = "hbase.properties";

    // Connection实例需保证单例存在
    private static Connection conn;

    public HBaseHandle() {
        init();
    }

    private void init() {
        if (conn == null) {
            synchronized (HBaseHandle.class) {
                if (conn == null) {
                    try {
                        Configuration conf = HBaseConfiguration.create();
                        Properties p = ConfigLoader.load(CONFIG_FILE);
                        for (Object key : p.keySet()) {
                            conf.set(String.valueOf(key), String.valueOf(p.get(key)));
                        }
                        conn = ConnectionFactory.createConnection(conf);
                    } catch (IOException e) {
                        LOG.error("ConnectionFactory.createConnection error: {}", e);
                    }
                }
            }
        }
    }

    public static long rowCount(String tableName, String family) {
//        AggregationClient ac = new AggregationClient(configuration);
//        Scan scan = new Scan();
//        scan.addFamily(Bytes.toBytes(family));
//        scan.setFilter(new FirstKeyOnlyFilter());
        long rowCount = 0;
//        try {
//            ac.median();  //中值
//            ac.std();  //均方差
//            ac.rowCount(); //计数
//            ac.avg();  //均值
//            ac.max(); //最大
//            ac.min();  //最小
//            ac.sum();  //求和
//
//            rowCount = ac.rowCount(Bytes.toBytes(tableName), new LongColumnInterpreter(), scan);
//        } catch (Throwable e) {
//            LOG.info(e.getMessage(), e);
//        }
        return rowCount;
    }


    private void closeTable(Table table) {
        if (table != null) {
            try {
                table.close();
            } catch (IOException e) {
                LOG.error("close table error", e);
            }
        }
    }

    /**
     * 创建表
     * @param tableName 表名
     * @param familys 列簇
     */
    public void createTable(String tableName, String... familys) {
        HBaseAdmin admin = null;
        try {
            admin = (HBaseAdmin) conn.getAdmin();
            if (admin.tableExists(tableName)) {
                LOG.error("table: " + tableName + " exists");
                return;
            }

            HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
            // 添加列簇
            for (String family : familys) {
                desc.addFamily(new HColumnDescriptor(family));
            }
            admin.createTable(desc);
            LOG.info("表创建成功");
        } catch (IOException e) {
            LOG.error("create table error: {}", e);
        } finally {
            if (admin != null) {
                try {
                    admin.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 写数据
     * @param tableName 表名
     * @param rowKey 行之间
     * @param family 列簇
     * @param qualifier 列修饰符,可为空，为空时直接存储在列簇下
     * @param value 值
     */
    public void insertData(String tableName, String rowKey, String family, String qualifier,
                           String value) {
        HTable table = null;
        try {
            table = (HTable) conn.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            byte[] qulifiers = StringUtils.isBlank(qualifier) ? null : Bytes.toBytes(qualifier);
            put.addColumn(Bytes.toBytes(family), qulifiers, Bytes.toBytes(value));
            table.put(put);
            LOG.debug("插入成功");
        } catch (IOException e) {
            LOG.error("insert error: {}", e);
        } finally {
            closeTable(table);
        }
    }

    /**
     * 向同一个列族下同时插入多个列
     * @param tableName 表名
     * @param rowKey 行之间
     * @param family 列簇
     * @param kv k-列修饰符，v-值
     * @throws Exception
     */
    public void insertDataBatch(String tableName, String rowKey, String family,
                                Map<String, String> kv) {
        HTable table = null;
        try {
            table = (HTable) conn.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            for (Map.Entry<String, String> entry : kv.entrySet()) {
                put.addColumn(Bytes.toBytes(family), Bytes.toBytes(entry.getKey()),
                        Bytes.toBytes(entry.getValue()));
            }
            table.put(put);
            LOG.debug("插入成功");
        } catch (IOException e) {
            LOG.error("insert error: {}", e);
        } finally {
            closeTable(table);
        }
    }

    /**
     * 自增操作
     * @param tableName
     * @param rowKey
     * @param family
     * @param qualifier
     * @param amount
     * @return
     */
    public long incrementColumnValue(String tableName, String rowKey, String family,
                                     String qualifier, long amount) {
        long result = 0;
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            result = table.incrementColumnValue(Bytes.toBytes(rowKey), Bytes.toBytes(family),
                    Bytes.toBytes(qualifier), amount);
        } catch (IOException e) {
            LOG.error("increment error: {}", e);
        } finally {
            closeTable(table);
        }
        return result;
    }

    /**
     * 批量计数
     * @param tableName
     * @param rowKey
     * @param family
     * @param kv k-qualifier，v-amount
     */
    public void incrColumnValueBatch(String tableName, String rowKey, String family,
                                     Map<String, Long> kv) {

        HTable table = null;
        try {
            table = (HTable) conn.getTable(TableName.valueOf(tableName));
            Increment increment = new Increment(Bytes.toBytes(rowKey));
            for (Map.Entry<String, Long> entry : kv.entrySet()) {
                increment.addColumn(Bytes.toBytes(family), Bytes.toBytes(entry.getKey()),
                        entry.getValue().longValue());
            }
            table.increment(increment);
        } catch (IOException e) {
            LOG.error("increment error: {}", e);
        } finally {
            closeTable(table);
        }
    }

    /**
     * 更新某一列的值
     *
     * @param tableName  表名
     * @param rowKey     rowkey
     * @param familyName 列族
     * @param columnName 列名
     * @param value      值
     */
    public void updateColumnValue(String tableName, String rowKey, String familyName,
                                  String columnName, String value) {
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName),
                    Bytes.toBytes(value));
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeTable(table);
        }
    }

    /**
     * 查询指定rowkey下的所有数据
     * @param tableName
     * @param rowKey
     * @return
     */
    public Map<String, String> queryByRowKey(String tableName, String rowKey) {
        Map<String, String> retMap = new HashMap<String, String>();
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            Result rs = table.get(new Get(Bytes.toBytes(rowKey)));
            String key;
            String value;
            if (!rs.isEmpty()) {
                for (Cell cell : rs.rawCells()) {
                    if (cell.getQualifierLength() == 0) {
                        key = Bytes.toString(CellUtil.cloneFamily(cell));
                    } else {
                        key = Bytes.toString(CellUtil.cloneFamily(cell)) + ":"
                                + Bytes.toString(CellUtil.cloneQualifier(cell));
                    }
                    value = Bytes.toString(CellUtil.cloneValue(cell));
                    retMap.put(key, value);
                }
            }
        } catch (IOException e) {
            LOG.error("query error: {}", e);
        } finally {
            closeTable(table);
        }
        return retMap;
    }

    /**
     * 查询某一列数据
     *
     * @param tableName  表名
     * @param rowKey     rowKey
     * @param familyName 列族
     * @param columnName 列名
     */
    public String queryByColumn(String tableName, String rowKey, String familyName,
                                String columnName) {
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
            Result result = table.get(get);
            if (!result.isEmpty()) {
                Cell cell = result.listCells().get(0);
                if (cell != null) {
                    return Bytes.toString(CellUtil.cloneValue(cell));
                }
            }
        } catch (IOException e) {
            LOG.error("query error: {}", e);
        } finally {
            closeTable(table);
        }
        return null;
    }

    /**
     * 范围查询数据
     *
     * @param tableName   表名
     * @param beginRowKey startRowKey（包含）
     * @param endRowKey   stopRowKey（不包含）
     */
    public List<Map<String, String>> scanResult(String tableName, String beginRowKey,
                                                String endRowKey) {
        List<Map<String, String>> retList = new ArrayList<>();

        Table table = null;
        ResultScanner rs = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));

            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(beginRowKey));
            scan.setStopRow(Bytes.toBytes(endRowKey));
            rs = table.getScanner(scan);
            for (Result result : rs) {
                Map<String, String> map = new HashMap<>();
                for (Cell cell : result.listCells()) {
                    String key;
                    if (cell.getQualifierLength() == 0) {
                        key = Bytes.toString(CellUtil.cloneFamily(cell));
                    } else {
                        key = Bytes.toString(CellUtil.cloneFamily(cell)) + ":"
                                + Bytes.toString(CellUtil.cloneQualifier(cell));
                    }
                    String value = Bytes.toString(CellUtil.cloneValue(cell));
                    map.put(key, value);
                }
                retList.add(map);
            }
        } catch (IOException e) {
            LOG.error("query error: {}", e);
        } finally {
            if (null != rs) {
                rs.close();
            }
            closeTable(table);
        }
        return retList;
    }

    /**
     * 删除指定列
     *
     * @param tableName  表名
     * @param rowKey     rowKey
     * @param familyName 列族
     * @param columnName 列名
     */
    public void deleteColumn(String tableName, String rowKey, String familyName,
                             String columnName) {
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            Delete delColumn = new Delete(Bytes.toBytes(rowKey));
            delColumn.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
            table.delete(delColumn);
        } catch (IOException e) {
            LOG.error("deleteColumn error:{}", e);
        } finally {
            closeTable(table);
        }
    }

    /**
     * 删除一行数据
     * @param tableName
     * @param rowKeyr
     */
    public void deleteRow(String tableName, String rowKeyr) {
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
            table.delete(new Delete(Bytes.toBytes(rowKeyr)));
        } catch (IOException e) {
            LOG.error("delete error: {}", e);
        } finally {
            closeTable(table);
        }
    }

    /**
     * 删除表
     * @param tableName
     */
    public void deleteTable(String tableName) {
        Admin admin = null;
        TableName table = TableName.valueOf(tableName);
        try {
            admin = conn.getAdmin();
            if (admin.tableExists(table)) {
                if (admin.isTableEnabled(table)) {
                    admin.disableTable(table);
                }
                admin.deleteTable(table);
                LOG.info("表" + tableName + "删除成功");
            } else {
                LOG.info("表" + tableName + "不存在");
            }
        } catch (IOException e) {
            LOG.error("delete table error: {}", e);
        } finally {
            if (admin != null) {
                try {
                    admin.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
