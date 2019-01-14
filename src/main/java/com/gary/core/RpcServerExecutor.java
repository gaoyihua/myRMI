package com.gary.core;

import com.gary.util.CloseableUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * describe:RPC服务器端执行者
 *
 * @author gary
 * @date 2019/01/12
 */
public class RpcServerExecutor implements Runnable {
    private Socket socket;
    private RpcServer rpcServer;
    private ThreadPoolExecutor threadPool;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public RpcServerExecutor(Socket socket, RpcServer rpcServer, ThreadPoolExecutor threadPool, long threadId) throws IOException {
        this.socket = socket;
        this.rpcServer = rpcServer;
        this.ois = new ObjectInputStream(this.socket.getInputStream());
        this.oos = new ObjectOutputStream(this.socket.getOutputStream());
        this.threadPool = threadPool;
        this.threadPool.execute(this);
        //new Thread(this, "RPC_EXECUTOR_" + threadId).start();
    }

    /**
     *  This method should be deleted.
     * @param parameters
     */
    private void showParameters(Object[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            System.out.println(i + ":" + parameters[i]);
        }
    }

    @Override
    public void run() {
        try {
            //接收RPC客户端传递的id和参数
            String rpcBeanId = ois.readUTF();
            Object[] parameters = (Object[]) ois.readObject();
            //showParameters(parameters);
            //定位相关类、对象、方法
            RpcBeanDefinition rpcBeanDefinition = rpcServer.getRpcBeanFactory().getRpcBean(rpcBeanId);
            Method method = rpcBeanDefinition.getMethod();
            Object object = rpcBeanDefinition.getObject();
            //反射执行
            Object result = method.invoke(object, parameters);
            //向RPC客户端返回执行结果
            oos.writeObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtil.close(ois, oos, socket);
        }
    }

}
