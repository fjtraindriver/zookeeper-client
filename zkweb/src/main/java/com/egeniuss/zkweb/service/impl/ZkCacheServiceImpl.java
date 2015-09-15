package com.egeniuss.zkweb.service.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.egeniuss.zkweb.dao.ZkConfigDao;
import com.egeniuss.zkweb.service.ZkCacheService;
import com.egeniuss.zkweb.service.ZkClient;

public class ZkCacheServiceImpl implements ZkCacheService {

    private static final Logger LOGGER = Logger.getLogger(ZkCacheServiceImpl.class);
    private ZkConfigDao zkConfigDao;
    private Map<String, ZkClient> _cache = null;

    public void init() {
        _cache = new ConcurrentHashMap<String, ZkClient>();
        try {
            List<Map<String, Object>> list = zkConfigDao.query();
            for (Map<String, Object> m : list) {
                ZkClient client = new ZkClientImpl(m.get("CONNECTSTR").toString(), Integer.parseInt(m.get("SESSIONTIMEOUT").toString()));
                put(m.get("ID").toString(), client);
            }
        } catch (SQLException | IOException e) {
            LOGGER.error("init cache error!", e);
        }
    }

    public void destroy() {
        Set<Entry<String, ZkClient>> set = _cache.entrySet();
        for (Entry<String, ZkClient> e : set) {
            ZkClient client = e.getValue();
            if (client != null) {
                client.close();
            }
        }
    }

    public ZkClient put(String key, ZkClient zk) {
        return _cache.put(key, zk);
    }

    public ZkClient get(String key) {
        return _cache.get(key);
    }

    public void remove(String key) {
        ZkClient client = _cache.remove(key);
        client.close();
    }

    public int size() {
        return _cache.size();
    }

    public void setZkConfigDao(ZkConfigDao zkConfigDao) {
        this.zkConfigDao = zkConfigDao;
    }

}
