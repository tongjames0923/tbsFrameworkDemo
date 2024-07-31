package com.example.demo;

import cn.hutool.core.util.StrUtil;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.stereotype.Component;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.zookeeper.interfaces.IZookeeperListenner;

@Component
public class ZooEvent implements IZookeeperListenner {

    @AutoLogger
    ILogger logger;

    public ZooEvent() {
        int i = 0;
    }

    @Override
    public boolean accept(String path, Watcher.Event.KeeperState state, Watcher.Event.EventType type) {
        if (StrUtil.isEmpty(path)) {
            return false;
        }
        return path.startsWith("/ZK_NODE/locks") && type == Watcher.Event.EventType.NodeDeleted;
    }

    @Override
    public void onEvent(Watcher watcher, WatchedEvent event) {
        logger.info("{} removed", event.getPath());
    }
}
