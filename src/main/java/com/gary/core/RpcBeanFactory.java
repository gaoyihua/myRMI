package com.gary.core;

import java.util.HashMap;
import java.util.Map;

/**
 * describe:RPCBean工厂
 *
 * @author gary
 * @date 2019/01/12
 */
public class RpcBeanFactory {
    private final Map<String, RpcBeanDefinition> rpcBeanMap;

    public RpcBeanFactory() {
        rpcBeanMap = new HashMap<>();
    }

    void addRpcBean(String rpcBeanId, RpcBeanDefinition rpcBeanDefinition) {
        RpcBeanDefinition rbd = rpcBeanMap.get(rpcBeanId);
        if (rbd != null) {
            return;
        }
        rpcBeanMap.put(rpcBeanId, rpcBeanDefinition);
    }

    RpcBeanDefinition getRpcBean(String rpcBeanId) {
        return rpcBeanMap.get(rpcBeanId);
    }
}
