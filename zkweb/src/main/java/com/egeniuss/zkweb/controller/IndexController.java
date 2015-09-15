/*
 * Copyright (c) 2005, 2015, EGENIUSS Technology Co.,Ltd. All rights reserved.
 * EGENIUSS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.egeniuss.zkweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * Title: IndexController.java
 * </p>
 * Description:
 * <p>
 * Modify histoty:
 * 
 * @author Linhua
 * @version 1.0
 * @created 2015年7月29日 下午4:56:05
 **/
@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping("/index.html")
    public String index() {
        return "home";
    }
    
}
