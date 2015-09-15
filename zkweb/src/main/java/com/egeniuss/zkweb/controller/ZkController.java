package com.egeniuss.zkweb.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.egeniuss.zkweb.model.Tree;
import com.egeniuss.zkweb.model.TreeRoot;
import com.egeniuss.zkweb.service.ZkCacheService;
import com.egeniuss.zkweb.service.ZkClient;

@Controller
@RequestMapping("/zk")
public class ZkController {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private ZkCacheService zkCacheService;

    @RequestMapping(value = "/queryZnodeInfo.html", produces = "text/html;charset=UTF-8")
    public String queryzNodeInfo(@RequestParam(required = false) String path, Model model, @RequestParam(required = true) String cacheId) {
        try {
            path = URLDecoder.decode(path, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        if (path != null) {
            ZkClient client = zkCacheService.get(cacheId);
            model.addAttribute("data", client.getData(path));
            model.mergeAttributes(client.getNodeMeta(path));
            model.addAttribute("acls", client.getACLs(path));
            model.addAttribute("path", path);
            model.addAttribute("cacheId", cacheId);
        }
        return "info";
    }

    @RequestMapping(value = "/queryZnode.html", produces = "text/html;charset=UTF-8")
    public @ResponseBody List<Tree> query(@RequestParam(required = false) String id, @RequestParam(required = false) String path, @RequestParam(required = true) String cacheId) {
        TreeRoot root = new TreeRoot();
        if (path == null) {

        } else if ("/".equals(path)) {
            root.remove(0);
            List<String> pathList = zkCacheService.get(cacheId).getChildren(null);
            for (String p : pathList) {
                Map<String, Object> atr = new HashMap<String, Object>();
                atr.put("path", "/" + p);
                Tree tree = new Tree(0, p, Tree.STATE_CLOSED, null, atr);
                root.add(tree);
            }
        } else {
            root.remove(0);
            try {
                path = URLDecoder.decode(path, UTF_8.name());
            } catch (UnsupportedEncodingException e) {
            }
            List<String> pathList = zkCacheService.get(cacheId).getChildren(path);
            for (String p : pathList) {
                Map<String, Object> atr = new HashMap<String, Object>();
                atr.put("path", path + "/" + p);
                Tree tree = new Tree(0, p, Tree.STATE_CLOSED, null, atr);
                root.add(tree);
            }
        }
        return root;
    }

    @RequestMapping(value = "/saveData.html", produces = "text/html;charset=UTF-8")
    public @ResponseBody String saveData(@RequestParam() String path, @RequestParam() String data, @RequestParam(required = true) String cacheId) {
        return zkCacheService.get(cacheId).setData(path, data) == true ? "保存成功" : "保存失败";
    }

    @RequestMapping(value = "/createNode.html", produces = "text/html;charset=UTF-8")
    public @ResponseBody String createNode(@RequestParam() String path, @RequestParam() String nodeName, @RequestParam(required = true) String cacheId) {
        return zkCacheService.get(cacheId).createNode(path, nodeName, "") == true ? "保存成功" : "保存失败";
    }

    @RequestMapping(value = "/deleteNode.html", produces = "text/html;charset=UTF-8")
    public @ResponseBody String deleteNode(@RequestParam() String path, @RequestParam(required = true) String cacheId) {
        return zkCacheService.get(cacheId).deleteNode(path) == true ? "删除成功" : "删除失败";
    }

    public void setZkCacheService(ZkCacheService zkCacheService) {
        this.zkCacheService = zkCacheService;
    }

}
