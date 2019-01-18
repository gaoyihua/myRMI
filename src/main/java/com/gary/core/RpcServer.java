package com.gary.core;

import com.gary.exception.PortNotDefinedException;
import com.gary.util.CloseableUtil;

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
    private int port;
    private volatile boolean goon;
    private static long executorId;
    private final RpcBeanFactory rpcBeanFactory;
    private ThreadPoolExecutor threadPool;


    public RpcServer(int port) {
        this();
        this.port = port;
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

    public void setPort(int port) {
        this.port = port;
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
//        if(this.server != null && !this.server.isClosed()) {
//            try {
//                this.server.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                this.server = null;
//            }
//        }
    }

    public void startRpcServer() throws Exception {
        if (this.port == 0) {
            throw new PortNotDefinedException();
        }
        this.server = new ServerSocket(port);
        this.goon = true;
        new Thread(this, "RPC_SERVER").start();
    }

    @Override
    public void run() {
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
}
