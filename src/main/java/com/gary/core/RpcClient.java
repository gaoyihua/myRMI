package com.gary.core;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * describe:RPC客户端
 *
 * @author gary
 * @date 2019/01/12
 */
public class RpcClient {
    private RpcClientExecutor rpcClientExecutor;
    private EServiceCommand serviceCommand;

    public RpcClient(EServiceCommand serviceCommand) {
        this.rpcClientExecutor = new RpcClientExecutor(serviceCommand);
    }

    public RpcClient(String rpcServerIp, int rpcServerPort) {
        this.rpcClientExecutor =
                new RpcClientExecutor(rpcServerIp, rpcServerPort);
    }

    @SuppressWarnings("unchecked")
    private <T> T cglibProxy(Class<?> klass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(klass);
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                String rpcBeanId = String.valueOf(method.toString().hashCode());
                Class<?> returnType = method.getReturnType();
                return rpcClientExecutor.rpcExecutor(rpcBeanId, objects, returnType);
            }
        };
        enhancer.setCallback(methodInterceptor);
        return (T)enhancer.create();
    };

    @SuppressWarnings("unchecked")
    private <T> T jdkProxy(Class<?> klass) {
        return (T) Proxy.newProxyInstance(
                klass.getClassLoader(),
                new Class<?>[]{ klass },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String rpcBeanId = String.valueOf(method.toString().hashCode());
                        Class<?> returnType = method.getReturnType();
                        return rpcClientExecutor.rpcExecutor(rpcBeanId, args, returnType);
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<?> klass) {
        if (!klass.isInterface()) {
            return cglibProxy(klass);
        }

        return jdkProxy(klass);
    }

}
