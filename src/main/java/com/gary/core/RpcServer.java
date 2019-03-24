package com.gary.core;

import com.gary.exception.PortNotDefinedException;
import com.gary.exception.ServiceNameNotDefinedException;
import com.gary.util.CloseableUtil;
import com.gary.util.PropertiesParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * describe:RPC服务器端
 *
 * @author gary
 * @date 2019/01/12
 */
public class RpcServer implements Runnable {
    private ServerSocket server;
    private String ip;
    private Socket manageCenterServer;
    private int port;
    private EServiceCommand serviceCommand;
    private volatile boolean goon;
    private static long executorId;
    private final RpcBeanFactory rpcBeanFactory;
    private ThreadPoolExecutor threadPool;
    private static final Gson gson = new GsonBuilder().create();


    public RpcServer(int port, EServiceCommand serviceCommand) {
        this();
        this.port = port;
        this.serviceCommand = serviceCommand;
    }

    public RpcServer(String ip, int port, EServiceCommand serviceCommand) {
        this();
        this.ip = ip;
        this.port = port;
        this.serviceCommand = serviceCommand;
    }

    public RpcServer() {
        rpcBeanFactory = new RpcBeanFactory();
        threadPool = new ThreadPoolExecutor(50,
                100, 500, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>());
        this.goon = false;
    }

    public RpcBeanFactory getRpcBeanFactory() {
        return rpcBeanFactory;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setServiceName(EServiceCommand serviceCommand) {
        this.serviceCommand = serviceCommand;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void linkServerManageCenter(String manageCenterIp, int manageCenterPort) {
        try {
            manageCenterServer = new Socket(manageCenterIp, manageCenterPort);

            DataOutputStream dos = new DataOutputStream(manageCenterServer.getOutputStream());

            dos.writeUTF(gson.toJson(EServiceCommand.REGISTER_SERVICE));
            dos.flush();

            DataInputStream dis = new DataInputStream(manageCenterServer.getInputStream());

            if (gson.fromJson(dis.readUTF(), EServiceCommand.class).equals(EServiceCommand.YES)) {
                String sendMessage = gson.toJson(this.serviceCommand) + "-" + this.ip + "-" + this.port;
                dos.writeUTF(sendMessage);
            }

            new Thread(new Keeper(dis, dos)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registryRpc(String xmlPath) {
        //TODO 注册Bean可以提供多种方式，如扫描注解注册、XML注册、调用专门用来注册的方法注册等等。
        RpcBeanRegistry.registInterface(rpcBeanFactory, xmlPath);
    }

    public void registryRpc(Class<?> klass) {
        //TODO 注册Bean可以提供多种方式，如扫描注解注册、XML注册、调用专门用来注册的方法注册等等。
        RpcBeanRegistry.registInterface(rpcBeanFactory, klass);
    }

    public void registryRpc(Class<?> interf, Object object) {
        //TODO 注册Bean可以提供多种方式，如扫描注解注册、XML注册、调用专门用来注册的方法注册等等。
        RpcBeanRegistry.registInterface(rpcBeanFactory, interf, object);
    }

    public void registryRpc(Class<?> interf, Class<?> implementClass) {
        //TODO 注册Bean可以提供多种方式，如扫描注解注册、XML注册、调用专门用来注册的方法注册等等。
        RpcBeanRegistry.registInterface(rpcBeanFactory, interf, implementClass);
    }

    private void stopRpcServer() {
        CloseableUtil.close(this.server);
    }

    public void startRpcServer() throws Exception {
        if (this.port == 0 ) {
            throw new PortNotDefinedException();
        }
        this.server = new ServerSocket(port);
        this.goon = true;
        new Thread(this, "RPC_SERVER").start();
    }

    @Override
    public void run() {
        try {
            if (this.serviceCommand != null) {
                PropertiesParser.loadProperties("/smc.properties");
                String manageCenterIp = PropertiesParser.value("manageCenterIp");
                String manageCenterPort = PropertiesParser.value("manageCenterPort");
                linkServerManageCenter(manageCenterIp, Integer.valueOf(manageCenterPort));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (goon) {
            try {
                Socket rpcClient = server.accept();
                new RpcServerExecutor(rpcClient, this, threadPool, ++executorId);
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
            }
        }
        stopRpcServer();
    }

    public class Keeper implements Runnable {
        private DataInputStream dis;
        private DataOutputStream dos;

        public Keeper(DataInputStream dis, DataOutputStream dos) {
            this.dis = dis;
            this.dos = dos;
        }

        @Override
        public void run() {
            while (goon) {
                try {
                    EServiceCommand command = gson.fromJson(dis.readUTF(), EServiceCommand.class);
                    if (command != null) {
                        if (command.equals(EServiceCommand.TEST)) {
                            dos.writeUTF(gson.toJson(EServiceCommand.TEST));
                        }
                    }
                } catch (IOException e) {
                    goon = false;
                    e.printStackTrace();
                }
            }
            CloseableUtil.close(dis, dos);
        }
    }
}
