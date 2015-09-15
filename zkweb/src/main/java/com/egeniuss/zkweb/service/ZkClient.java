package com.egeniuss.zkweb.service;

import java.util.List;
import java.util.Map;

public interface ZkClient {

    public void close();

    public List<String> getChildren(String path);

    public String getData(String path);

    public Map<String, String> getNodeMeta(String nodePath);

    public List<Map<String, String>> getACLs(String nodePath);

    public boolean createNode(String path, String nodeName, String data);

    public boolean deleteNode(String nodePath);

    public boolean setData(String nodePath, String data);

    public long getNodeId(String nodePath);

    public enum P {
        host, sessionTimeOut
    }

    public enum Meta {
        czxid, mzxid, ctime, mtime, version, cversion, aversion, ephemeralOwner, dataLength, numChildren, pzxid
    }

    public enum Acl {
        scheme, id, perms
    }

}
