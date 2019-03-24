package com.gary.core;

import com.gary.exception.PortNotDefinedException;
import com.gary.util.CloseableUtil;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * describe:总控服务器
 *
 * @author gary
 * @date 2019/01/22
 */
public class ServerManageCenter implements Runnable {
    private ServerSocket server;
    private int port;
    private volatile boolean goon;
    private ThreadPoolExecutor threadPool;
    private static final Map<EServiceCommand, List<ServerDefinition>> map;

    static {
        map = new ConcurrentHashMap<>();
    }

    public ServerManageCenter(int port) {
        threadPool = new ThreadPoolExecutor(50,
                100, 500, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>());
        this.goon = false;
        this.port = port;
    }

    public static void setServiceIntervalTime(EServiceCommand command, ServerDefinition serverDefinition, long intervalTime) {
        List<ServerDefinition> serverList = ServerManageCenter.getService(command);
        for (ServerDefinition server : serverList) {
            if (server.equals(serverDefinition)) {
                server.setIntervalTime(intervalTime);
                System.out.println("设置interval: " + intervalTime + "成功");
            }
        }
    }

    public static void removeService(EServiceCommand command, ServerDefinition serverDefinition) {
        List<ServerDefinition> serverList = getService(command);
        if (serverList.isEmpty()) {
            removeServeice(command);
            return ;
        }
        for (ServerDefinition server : serverList) {
            if (server.equals(serverDefinition)) {
                serverList.remove(server);
                System.out.println("删除server：" + server.toString());
                break;
            }
        }
    }

    public static void removeServeice(EServiceCommand command) {
        map.remove(command);
    }

    public static List<ServerDefinition> getService(EServiceCommand command) {
        return map.get(command);
    }

    public static void putService(EServiceCommand command, ServerDefinition serverDefinition) {
        List<ServerDefinition> list = map.get(command);
        if (list == null) {
            List<ServerDefinition> newList = new ArrayList<>();
            newList.add(serverDefinition);
            map.put(command, newList);
        } else {
            list.add(serverDefinition);
        }
    }

    @Override
    public void run() {
        while (goon) {
            try {
                Socket socket = server.accept();
                new ServerManageCenterExecutor(socket, this, threadPool);
            } catch (Exception e) {
                goon = false;
                e.printStackTrace();
            }
        }
        stopServer();

    }

    public void startServer() throws Exception {
        if (this.port == 0) {
            throw new PortNotDefinedException();
        }
        this.server = new ServerSocket(port);
        this.goon = true;
        new Thread(this, "SERVER_MANAGE_CENTER").start();
    }

    private void stopServer() {
        CloseableUtil.close(this.server);
    }
}
