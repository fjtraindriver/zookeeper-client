package com.egeniuss.zkweb.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.egeniuss.zkweb.dao.ZkConfigDao;
import com.egeniuss.zkweb.service.ZkCacheService;
import com.egeniuss.zkweb.service.ZkClient;
import com.egeniuss.zkweb.service.impl.ZkClientImpl;

@Controller
@RequestMapping("/zkcfg")
public class ZkCfgController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkCfgController.class);
    private ZkConfigDao zkConfigDao;
    private ZkCacheService zkCacheService;

    @RequestMapping(value = "/queryZkCfg.html", produces = "text/html;charset=UTF-8")
    public @ResponseBody Map<String, Object> queryZkCfg(@RequestParam(required = false) int page, @RequestParam(required = false) int rows) {
        Map<String, Object> _map = new HashMap<String, Object>();
        try {
            _map.put("rows", zkConfigDao.query(page, rows));
            _map.put("total", zkConfigDao.count());
        } catch (SQLException e) {
            LOGGER.error("query zk connector error!", e);
        }
        return _map;
    }

    @RequestMapping(value = "/addZkCfg.html", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
    public void addZkCfg(@RequestParam(required = false) String des, @RequestParam(required = false) String connectstr, @RequestParam(required = false) String sessiontimeout, HttpServletResponse response) {
        String msg = "";
        try {
            ZkClient client = new ZkClientImpl(connectstr, Integer.parseInt(sessiontimeout));
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            zkConfigDao.add(id, des, connectstr, sessiontimeout);
            zkCacheService.put(id, client);
        } catch (IOException | SQLException e) {
            LOGGER.error("add zk connector error!", e);
            msg = "添加失败";
        }
        msg = "添加成功";
        writeData(response, msg);
    }

    private void writeData(HttpServletResponse response, String msg) {
        response.setCharacterEncoding("GB2312");
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/html; charset=UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(msg);
            writer.close();
        } catch (IOException e) {
            LOGGER.error("get response writer error!", e);
        }
    }

    @RequestMapping(value = "/queryZkCfgById.html", produces = "text/html;charset=UTF-8")
    public @ResponseBody Map<String, Object> queryZkCfg(@RequestParam(required = false) String id) {
        try {
            return zkConfigDao.findById(id);
        } catch (SQLException e) {
            LOGGER.error("find zk connector byid error!", e);
            return null;
        }
    }

    @RequestMapping(value = "/updateZkCfg.html", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
    public void updateZkCfg(@RequestParam(required = true) String id, @RequestParam(required = false) String des, @RequestParam(required = false) String connectstr, @RequestParam(required = false) String sessiontimeout, HttpServletResponse response) {
        String msg = "";
        try {
            zkCacheService.remove(id);
            zkCacheService.put(id, new ZkClientImpl(connectstr, Integer.parseInt(sessiontimeout)));
            zkConfigDao.update(id, des, connectstr, sessiontimeout);
        } catch (IOException | SQLException e) {
            LOGGER.error("update zk connector error!", e);
            msg = "保存失败";
        }
        msg = "保存成功";
        writeData(response, msg);
    }

    @RequestMapping(value = "/delZkCfg.html", produces = "text/html;charset=UTF-8")
    public @ResponseBody String delZkCfg(@RequestParam(required = true) String id) {
        try {
            zkConfigDao.delete(id);
            zkCacheService.remove(id);
        } catch (SQLException e) {
            LOGGER.error("delete zk connector error!", e);
            return "删除失败";
        }
        return "删除成功";
    }

    public void setZkConfigDao(ZkConfigDao zkConfigDao) {
        this.zkConfigDao = zkConfigDao;
    }

    public void setZkCacheService(ZkCacheService zkCacheService) {
        this.zkCacheService = zkCacheService;
    }
}
