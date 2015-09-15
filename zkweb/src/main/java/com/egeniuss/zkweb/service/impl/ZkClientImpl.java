package com.egeniuss.zkweb.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import com.egeniuss.zkweb.service.ZkClient;

public class ZkClientImpl implements Watcher, ZkClient {

    private static final Log LOGGER = LogFactory.getLog(ZkClientImpl.class);
    private ZooKeeper zk = null;
    private static final String ROOT = "/";

    public ZkClientImpl(String connectString, int timeout) throws IOException {
        zk = new ZooKeeper(connectString, timeout, this);
    }

    public void close() {
        if (zk != null) {
            try {
                zk.close();
            } catch (InterruptedException e) {
                LOGGER.error("close zk client error!", e);
            }
        }
    }

    public List<String> getChildren(String path) {
        try {
            return zk.getChildren(path == null ? ROOT : path, false);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("get children error!", e);
        }
        return null;
    }

    public String getData(String path) {
        try {
            Stat s = zk.exists(path, false);
            if (s != null) {
                byte b[] = zk.getData(path, false, s);
                if (null == b) {
                    return null;
                }
                return new String(zk.getData(path, false, s));
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("get data error!", e);
        }
        return null;
    }

    public Map<String, String> getNodeMeta(String nodePath) {
        Map<String, String> nodeMeta = new LinkedHashMap<String, String>();
        try {
            if (nodePath.length() == 0) {
                nodePath = ROOT;
            }
            Stat s = zk.exists(nodePath, false);
            if (s != null) {
                nodeMeta.put(Meta.aversion.toString(), String.valueOf(s.getAversion()));
                nodeMeta.put(Meta.ctime.toString(), String.valueOf(s.getCtime()));
                nodeMeta.put(Meta.cversion.toString(), String.valueOf(s.getCversion()));
                nodeMeta.put(Meta.czxid.toString(), String.valueOf(s.getCzxid()));
                nodeMeta.put(Meta.dataLength.toString(), String.valueOf(s.getDataLength()));
                nodeMeta.put(Meta.ephemeralOwner.toString(), String.valueOf(s.getEphemeralOwner()));
                nodeMeta.put(Meta.mtime.toString(), String.valueOf(s.getMtime()));
                nodeMeta.put(Meta.mzxid.toString(), String.valueOf(s.getMzxid()));
                nodeMeta.put(Meta.numChildren.toString(), String.valueOf(s.getNumChildren()));
                nodeMeta.put(Meta.pzxid.toString(), String.valueOf(s.getPzxid()));
                nodeMeta.put(Meta.version.toString(), String.valueOf(s.getVersion()));
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("get node error!", e);
        }
        return nodeMeta;
    }

    public List<Map<String, String>> getACLs(String nodePath) {
        List<Map<String, String>> returnACLs = new ArrayList<Map<String, String>>();
        try {
            if (nodePath.length() == 0) {
                nodePath = ROOT;
            }
            Stat s = zk.exists(nodePath, false);
            if (s != null) {
                List<ACL> acls = zk.getACL(nodePath, s);
                for (ACL acl : acls) {
                    Map<String, String> aclMap = new LinkedHashMap<String, String>();
                    aclMap.put(Acl.scheme.toString(), acl.getId().getScheme());
                    aclMap.put(Acl.id.toString(), acl.getId().getId());
                    StringBuilder sb = new StringBuilder();
                    int perms = acl.getPerms();
                    boolean addedPerm = false;
                    if ((perms & Perms.READ) == Perms.READ) {
                        sb.append("Read");
                        addedPerm = true;
                    }
                    if (addedPerm) {
                        sb.append(", ");
                    }
                    if ((perms & Perms.WRITE) == Perms.WRITE) {
                        sb.append("Write");
                        addedPerm = true;
                    }
                    if (addedPerm) {
                        sb.append(", ");
                    }
                    if ((perms & Perms.CREATE) == Perms.CREATE) {
                        sb.append("Create");
                        addedPerm = true;
                    }
                    if (addedPerm) {
                        sb.append(", ");
                    }
                    if ((perms & Perms.DELETE) == Perms.DELETE) {
                        sb.append("Delete");
                        addedPerm = true;
                    }
                    if (addedPerm) {
                        sb.append(", ");
                    }
                    if ((perms & Perms.ADMIN) == Perms.ADMIN) {
                        sb.append("Admin");
                        addedPerm = true;
                    }
                    aclMap.put(Acl.perms.toString(), sb.toString());
                    returnACLs.add(aclMap);
                }
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("get acls error!", e);
        }
        return returnACLs;
    }

    public boolean createNode(String path, String nodeName, String data) {
        try {
            String p;
            if (ROOT.equals(path)) {
                p = path + nodeName;
            } else {
                p = path + "/" + nodeName;
            }
            Stat s = zk.exists(p, false);
            if (s == null) {
                zk.create(p, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            return true;
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("create node error!", e);
        }
        return false;
    }

    public boolean deleteNode(String nodePath) {
        try {
            Stat s = zk.exists(nodePath, false);
            if (s != null) {
                List<String> children = zk.getChildren(nodePath, false);
                for (String child : children) {
                    String node = nodePath + "/" + child;
                    deleteNode(node);
                }
                zk.delete(nodePath, -1);
            }
            return true;
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("delete node error!", e);
        }
        return false;
    }

    public boolean setData(String nodePath, String data) {
        try {
            zk.setData(nodePath, data.getBytes("utf-8"), -1);
            return true;
        } catch (KeeperException | InterruptedException | UnsupportedEncodingException e) {
            LOGGER.error("update data error!", e);
        }
        return false;
    }

    public void process(WatchedEvent event) {

    }

    public long getNodeId(String nodePath) {
        try {
            Stat s = zk.exists(nodePath, false);
            if (s != null) {
                return s.getPzxid();
            }
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("get node id error!", e);
        }
        return 0l;
    }

}
