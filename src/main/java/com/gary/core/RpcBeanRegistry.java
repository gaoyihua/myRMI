package com.gary.core;

import com.gary.util.XMLParser;
import org.w3c.dom.Element;

import java.lang.reflect.Method;

/**
 * describe:RPCBean注册
 *
 * @author gary
 * @date 2019/01/12
 */
public class RpcBeanRegistry {

    public RpcBeanRegistry() {
    }

    private static void doRegister(RpcBeanFactory rpcBeanFactory,
                                 Class<?> interf, Object object) {
        Method[] methods = interf.getDeclaredMethods();
        System.out.println("rpcBean开始注册");
        for (Method method : methods) {

            String rpcBeanId = String.valueOf(method.toString().hashCode());
            System.out.println(method.getName() + "-rpcBeanId:" + rpcBeanId);

            RpcBeanDefinition rpcBeanDefinition = new RpcBeanDefinition();
            rpcBeanDefinition.setKlass(interf);
            rpcBeanDefinition.setMethod(method);
            rpcBeanDefinition.setObject(object);

            rpcBeanFactory.addRpcBean(rpcBeanId, rpcBeanDefinition);
        }
        System.out.println("rpcBean注册完毕");
    }

    static void registInterface(RpcBeanFactory rpcBeanFactory,
                                Class<?> interf, Object object) {
        if (!interf.isAssignableFrom(object.getClass())) {
            return;
        }
        doRegister(rpcBeanFactory, interf, object);
    }

    static void registInterface(RpcBeanFactory rpcBeanFactory,
                                Class<?> interf, Class<?> implementClass) {
        if (!interf.isAssignableFrom(implementClass)) {
            return;
        }
        try {
            doRegister(rpcBeanFactory, interf, implementClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registInterface(RpcBeanFactory rpcBeanFactory, Class<?> klass) {
        try {
            doRegister(rpcBeanFactory, klass, klass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registInterface(RpcBeanFactory rpcBeanFactory, String xmlPath) {
        new XMLParser() {
            @Override
            public void dealElement(Element element, int i) {
                new XMLParser() {
                    @Override
                    public void dealElement(Element element, int i) {
                        String classPath = element.getAttribute("class");
                        String interfacePath = element.getAttribute("interface");
                        String name = element.getAttribute("name");
                        if (interfacePath == null || interfacePath == "") {
                            try {
                                Class<?> klass = Class.forName(classPath);
                                registInterface(rpcBeanFactory, klass);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        try {
                            Class<?> interfaceClass = Class.forName(interfacePath);
                            Class<?> implementClass = Class.forName(classPath);
                            registInterface(rpcBeanFactory, interfaceClass, implementClass);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }.dealElementInTag(element, "rpcBean");
            }
        }.dealElementInTag(XMLParser.getDocument(xmlPath), "rpc-configuration");
    }
}
