package com.tany.demo.Hbase;

import com.tany.demo.exception.ExecuteEnum;
import com.tany.demo.exception.TanyException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class PhoenixDAO {
    private static final Logger LOG = LoggerFactory.getLogger(PhoenixDAO.class);

    private PhoenixDataSource dataSource;

    public PhoenixDAO(Map phoenixConfig) {
        this.dataSource = new PhoenixDataSource(phoenixConfig);
    }

    public void createTable(String tableName, LinkedHashMap<String, HbaseDataType> columns, List<String> keys,
                            Integer ttlSeconds) throws TanyException {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append(tableName).append("(");
        for (Map.Entry<String, HbaseDataType> entry : columns.entrySet()) {
            String fieldName = entry.getKey();
            if (CollectionUtils.isNotEmpty(keys) && keys.contains(fieldName)) {
                sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(" NOT NULL,");
            } else {
                sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" CONSTRAINT PK_ID PRIMARY KEY (");
        sb.append(StringUtils.join(keys, ','));
        sb.append("))");
        if (ttlSeconds != null && ttlSeconds.intValue() > 0) {
            sb.append("TTL=").append(ttlSeconds.intValue());
        }
        LOG.info("SQL: " + sb);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement past = conn.prepareStatement(sb.toString())) {
            past.executeUpdate();
            LOG.info("创建表成功, tableName: " + tableName);
        } catch (SQLException e) {
            throw new TanyException(ExecuteEnum.ERROR_HBASE_EXECUTE, e.getMessage());
        }
    }


    /**
     * 更新数据，如果数据不存在则新增
     *
     * @param tableName 表名
     * @param datas     key-字段名，value-字段值
     * @throws SQLException
     */
    public void upsert(String tableName, Map<String, Object> datas) throws TanyException {
        if (MapUtils.isEmpty(datas)) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("UPSERT INTO ").append(tableName).append("(");
        List<Object> values = new ArrayList<>(datas.size());
        for (Map.Entry<String, Object> kv : datas.entrySet()) {
            sb.append(kv.getKey()).append(",");
            values.add(kv.getValue());
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") VALUES (");
        for (int i = 0; i < values.size(); i++) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        LOG.debug("SQL: " + sb);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement past = conn.prepareStatement(sb.toString())) {
            int index = 1;
            for (Object object : values) {
                past.setObject(index++, object);
            }
            past.executeUpdate();
            LOG.debug("更新成功");
        } catch (SQLException e) {
            throw new TanyException(ExecuteEnum.ERROR_HBASE_EXECUTE, e.getMessage());
        }
    }

    /**
     * 批量更新数据，如果数据不存在则新增
     *
     * @param tableName 表名
     * @param datas     数据集合 一个map表示一行数据 key-字段名，value-字段值
     * @throws SQLException
     */
    public void upsertBatch(String tableName, List<Map<String, Object>> datas) throws TanyException{
        if (CollectionUtils.isEmpty(datas) || MapUtils.isEmpty(datas.get(0))) {
            return;
        }

        // 列名集合
        List<String> columns = new ArrayList<>(datas.get(0).keySet());

        StringBuffer sb = new StringBuffer();
        sb.append("UPSERT INTO ").append(tableName).append("(");
        for (String column : columns) {
            sb.append(column).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), ") VALUES (");
        for (int j = 0; j < columns.size(); j++) {
            sb.append("?,");
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        LOG.debug("SQL: " + sb);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement past = conn.prepareStatement(sb.toString())) {

            int n = 0;
            for (Map<String, Object> data : datas) {
                int index = 0;
                for (String column : columns) {
                    past.setObject(++index, data.get(column));
                }
                past.addBatch();
                n++;

                if (n >= 1000) {
                    past.executeBatch();
                    n = 0;
                }
            }

            if (n != 0) {
                past.executeBatch();
            }

            LOG.debug("批量更新成功");
        } catch (SQLException e) {
            throw new TanyException(ExecuteEnum.ERROR_HBASE_EXECUTE, e.getMessage());
        }
    }

    /**
     * 根据主键查询一条记录
     *
     * @param tableName
     * @param keys
     * @return
     */
    public Map<String, Object> queryByKeys(String tableName, Map<String, Object> keys) throws TanyException{
        if (MapUtils.isEmpty(keys)) {
            return Collections.emptyMap();
        }

        StringBuffer sb = new StringBuffer("SELECT * FROM ");
        sb.append(tableName).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : keys.entrySet()) {
            sb.append(" AND ").append(entry.getKey()).append(" = ? ");
            params.add(entry.getValue());
        }
        sb.append(" LIMIT 1");
        LOG.debug("SQL: " + sb);

        List<Map<String, Object>> list = query(sb.toString(), params.toArray());
        if (CollectionUtils.isEmpty(list) || MapUtils.isEmpty(list.get(0))) {
            return Collections.emptyMap();
        }
        return list.get(0);
    }

    /**
     * SQL查询操作
     *
     * @param sql    支持标准sql，参数部分以问号代替，如select * from user where id=?
     * @param params 查询参数
     * @return 注：返回的字段名全大写
     */
    public List<Map<String, Object>> query(String sql, Object[] params) throws TanyException{
        if (StringUtils.isNotBlank(sql) && sql.toUpperCase().indexOf(" LIMIT ") < 0) {
            sql += " LIMIT 2000";
        }
        List<Map<String, Object>> retList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement past = conn.prepareStatement(sql)) {
            int index = 1;
            if (params != null && params.length > 0) {
                for (Object object : params) {
                    past.setObject(index++, object);
                }
            }

            ResultSet rs = past.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> rowMap = new HashMap<>();
                for (int i = 1; i < meta.getColumnCount() + 1; i++) {
                    String columnName = meta.getColumnName(i);
                    rowMap.put(columnName, rs.getObject(columnName));
                }
                retList.add(rowMap);
            }
        } catch (SQLException e) {
            throw new TanyException(ExecuteEnum.ERROR_HBASE_EXECUTE, e.getMessage());
        }
        return retList;
    }

    /**
     * 删除数据
     *
     * @param tableName 表名
     * @param keys      条件字段
     * @return
     * @throws TanyException
     */
    public void delete(String tableName, Map<String, Object> keys) throws TanyException{
        if (MapUtils.isEmpty(keys)) {
            return;
        }

        StringBuffer sb = new StringBuffer("DELETE FROM ");
        sb.append(tableName).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : keys.entrySet()) {
            sb.append(" AND ").append(entry.getKey()).append(" = ? ");
            params.add(entry.getValue());
        }
        LOG.debug("SQL: " + sb);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement past = conn.prepareStatement(sb.toString())) {
            int index = 1;
            for (Object param : params) {
                past.setObject(index++, param);
            }
            past.executeUpdate();
        } catch (SQLException e) {
            throw new TanyException(ExecuteEnum.ERROR_HBASE_EXECUTE, e.getMessage());
        }
    }

    /**
     * 删除表
     *
     * @param tableName
     * @throws TanyException
     */
    public void deleteTable(String tableName) throws TanyException{
        try (Connection conn = dataSource.getConnection();
             PreparedStatement past = conn.prepareStatement("DROP TABLE " + tableName)) {
            past.executeUpdate();
        } catch (SQLException e) {
            throw new TanyException(ExecuteEnum.ERROR_HBASE_EXECUTE, e.getMessage());
        }
    }

}
