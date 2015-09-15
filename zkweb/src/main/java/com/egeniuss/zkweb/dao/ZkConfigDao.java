package com.egeniuss.zkweb.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ZkConfigDao {

    /**
     * 
     * 初始化数据库
     * 
     * @author Linhua
     * @created 2015年7月29日 上午11:56:59
     * @throws SQLException
     */
    public void init() throws SQLException;

    /**
     * 
     * 销毁数据库对象
     * 
     * @author Linhua
     * @created 2015年7月29日 上午11:57:22
     */
    public void destroy();

    public void add(String des, String connectStr, String sessionTimeOut) throws SQLException;

    public void add(String id, String des, String connectStr, String sessionTimeOut) throws SQLException;

    public List<Map<String, Object>> query() throws SQLException;

    public List<Map<String, Object>> query(int page, int rows) throws SQLException;

    public void update(String id, String des, String connectStr, String sessionTimeOut) throws SQLException;

    public void delete(String id) throws SQLException;

    public Map<String, Object> findById(String id) throws SQLException;

    public int count() throws SQLException;

}
