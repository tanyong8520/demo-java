package com.tany.demo.hive;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiveClient {
    static {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection connection;

    public HiveClient(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int colSize = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>(colSize);
                for (int i = 0; i < colSize; i++) {
                    map.put(metaData.getColumnName(i + 1), rs.getObject(i + 1));
                }
                results.add(map);
            }
        }
        return results;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:hive2://192.168.0.194:10000/default";
        String username = "root";
        String password = "123456";

        HiveClient client = new HiveClient(url, username, password);

        long start = System.currentTimeMillis();
        List<Map<String, Object>> results = client.query("select idNumber, education from total limit 10");
        System.out.println(results);
        System.out.println(System.currentTimeMillis() - start);
    }


}
