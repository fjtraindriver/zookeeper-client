/*
 * Copyright (c) 2005, 2015, EGENIUSS Technology Co.,Ltd. All rights reserved.
 * EGENIUSS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.egeniuss.zkweb.service;


/**
 * <p>
 * Title: ZkCacheService.java
 * </p>
 * Description:
 * <p>
 * Modify histoty:
 * 
 * @author Linhua
 * @version 1.0
 * @created 2015年7月29日 下午4:16:28
 **/
public interface ZkCacheService {

    public void init();

    public void destroy();

    public ZkClient put(String key, ZkClient zk);

    public ZkClient get(String key);

    public void remove(String key);

    public int size();

}
