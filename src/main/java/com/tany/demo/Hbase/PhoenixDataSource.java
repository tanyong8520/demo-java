package com.tany.demo.Hbase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.tany.demo.exception.ExecuteEnum;
import com.tany.demo.exception.TanyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class PhoenixDataSource {
    private static final Logger LOG = LoggerFactory.getLogger(PhoenixDataSource.class);
    private static volatile DruidDataSource dataSource;

    public PhoenixDataSource(Map phoenixConfig) throws TanyException {
        if (dataSource == null) {
            synchronized (PhoenixDataSource.class) {
                if (dataSource == null) {
                    try {
                        dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(phoenixConfig);
                    } catch (Exception e) {
                        throw new TanyException(ExecuteEnum.ERROR_HBASE_EXECUTE);
                    }
                }
            }
        }
    }

    public Connection getConnection() throws TanyException {
        if (dataSource == null) {
            throw new TanyException(ExecuteEnum.ERROR_HBASE_EXECUTE, "hbase datasource is uninitialized");
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOG.error("get connection error：{}", e);
            throw new TanyException(ExecuteEnum.ERROR_HBASE_EXECUTE, e);
        }
    }
}
