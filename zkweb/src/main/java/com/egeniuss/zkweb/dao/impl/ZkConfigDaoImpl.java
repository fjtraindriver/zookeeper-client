package com.egeniuss.zkweb.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.h2.jdbcx.JdbcConnectionPool;

import com.egeniuss.zkweb.dao.ZkConfigDao;

public class ZkConfigDaoImpl implements ZkConfigDao {

    private JdbcConnectionPool pool = null;
    private Connection conn = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private String createTableSql;
    private String insertDataSql;
    private String selectAllDataSql;
    private String updateDataSql;
    private String deleteDataSql;
    private String selectDataByIdSql;
    private String selectDataPagableSql;
    private String countDataSql;

    public void init() throws SQLException {
        pool = JdbcConnectionPool.create("jdbc:h2:~/zkweb", "zkweb", "zk4321");
        pool.setMaxConnections(20);
        pool.setLoginTimeout(10);
        try {
            conn = pool.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            resultSet = metaData.getTables(null, null, "ZKCONNECTOR", new String[] { "TABLE" });
            if (!resultSet.next()) {// 不存在zkConnector表，则创建该表
                statement = conn.prepareStatement(createTableSql);
                statement.executeUpdate();
            }
        } finally {
            releaseJdbcResource();
        }
    }

    private void releaseJdbcResource() throws SQLException {
        if (resultSet != null) {
            resultSet.close();
            resultSet = null;
        }
        if (statement != null) {
            statement.close();
            statement = null;
        }
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    @Override
    public void destroy() {
        if (pool != null) {
            pool.dispose();
        }
    }

    public void add(String des, String connectStr, String sessionTimeOut) throws SQLException {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        add(id, des, connectStr, sessionTimeOut);
    }

    public void add(String id, String des, String connectStr, String sessionTimeOut) throws SQLException {
        try {
            conn = pool.getConnection();
            statement = conn.prepareStatement(insertDataSql);
            statement.setString(1, id);
            statement.setString(2, des);
            statement.setString(3, connectStr);
            statement.setString(4, sessionTimeOut);
            statement.executeUpdate();
        } finally {
            releaseJdbcResource();
        }
    }

    public List<Map<String, Object>> query() throws SQLException {
        try {
            conn = pool.getConnection();
            statement = conn.prepareStatement(selectAllDataSql);
            resultSet = statement.executeQuery();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            ResultSetMetaData meta = resultSet.getMetaData();
            Map<String, Object> map = null;
            int cols = meta.getColumnCount();
            while (resultSet.next()) {
                map = new HashMap<String, Object>();
                for (int i = 0; i < cols; i++) {
                    map.put(meta.getColumnName(i + 1).toUpperCase(), resultSet.getObject(i + 1));
                }
                list.add(map);
            }
            return list;
        } finally {
            releaseJdbcResource();
        }
    }

    public void update(String id, String des, String connectStr, String sessionTimeOut) throws SQLException {
        try {
            conn = pool.getConnection();
            statement = conn.prepareStatement(updateDataSql);
            statement.setString(1, des);
            statement.setString(2, connectStr);
            statement.setString(3, sessionTimeOut);
            statement.setString(4, id);
            statement.executeUpdate();
        } finally {
            releaseJdbcResource();
        }
    }

    public void delete(String id) throws SQLException {
        try {
            conn = pool.getConnection();
            statement = conn.prepareStatement(deleteDataSql);
            statement.setString(1, id);
            statement.executeUpdate();
        } finally {
            releaseJdbcResource();
        }
    }

    public Map<String, Object> findById(String id) throws SQLException {
        try {
            conn = pool.getConnection();
            statement = conn.prepareStatement(selectDataByIdSql);
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            Map<String, Object> map = new HashMap<String, Object>();
            ResultSetMetaData meta = resultSet.getMetaData();
            int cols = meta.getColumnCount();
            if (resultSet.next()) {
                for (int i = 0; i < cols; i++) {
                    map.put(meta.getColumnName(i + 1).toUpperCase(), resultSet.getObject(i + 1));
                }
            }
            return map;
        } finally {
            releaseJdbcResource();
        }
    }

    public List<Map<String, Object>> query(int page, int rows) throws SQLException {
        try {
            conn = pool.getConnection();
            statement = conn.prepareStatement(selectDataPagableSql);
            statement.setInt(1, (page - 1) * rows);
            statement.setInt(2, rows);
            resultSet = statement.executeQuery();
            ResultSetMetaData meta = resultSet.getMetaData();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = null;
            while (resultSet.next()) {
                map = new HashMap<String, Object>();
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    map.put(meta.getColumnName(i + 1).toUpperCase(), resultSet.getObject(i + 1));
                }
                list.add(map);
            }
            return list;
        } finally {
            releaseJdbcResource();
        }
    }

    public int count() throws SQLException {
        try {
            conn = pool.getConnection();
            statement = conn.prepareStatement(countDataSql);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } finally {
            releaseJdbcResource();
        }
    }

    public void setCreateTableSql(String createTableSql) {
        this.createTableSql = createTableSql;
    }

    public void setInsertDataSql(String insertDataSql) {
        this.insertDataSql = insertDataSql;
    }

    public void setSelectAllDataSql(String selectAllDataSql) {
        this.selectAllDataSql = selectAllDataSql;
    }

    public void setUpdateDataSql(String updateDataSql) {
        this.updateDataSql = updateDataSql;
    }

    public void setDeleteDataSql(String deleteDataSql) {
        this.deleteDataSql = deleteDataSql;
    }

    public void setSelectDataByIdSql(String selectDataByIdSql) {
        this.selectDataByIdSql = selectDataByIdSql;
    }

    public void setSelectDataPagableSql(String selectDataPagableSql) {
        this.selectDataPagableSql = selectDataPagableSql;
    }

    public void setCountDataSql(String countDataSql) {
        this.countDataSql = countDataSql;
    }

}
