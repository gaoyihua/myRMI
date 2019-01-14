package com.gary.core;

import java.lang.reflect.Method;

/**
 * describe:RPCBean描述
 *
 * @author gary
 * @date 2019/01/12
 */
public class RpcBeanDefinition {
    private Class<?> klass;
    private Method method;
    private Object object;

    public RpcBeanDefinition() {
    }

    public Class<?> getKlass() {
        return klass;
    }

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "RpcBeanDefinition{" +
                "klass=" + klass +
                ", method=" + method +
                ", object=" + object +
                '}';
    }
}
