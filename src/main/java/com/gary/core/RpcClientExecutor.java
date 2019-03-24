package com.gary.core;

import com.gary.exception.ServiceNotFoundException;
import com.gary.util.CloseableUtil;
import com.gary.util.PropertiesParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * describe:RPC客户端执行者
 *
 * @author gary
 * @date 2019/01/12
 */
public class RpcClientExecutor {
    private Socket manageCenterServer;
    private EServiceCommand serviceCommand;
    private String rpcServerIp;
    private int rpcServerPort;
    private static final Gson gson = new GsonBuilder().create();

    public RpcClientExecutor() {
    }

    public RpcClientExecutor(String rpcServerIp, int rpcServerPort) {
        this.rpcServerIp = rpcServerIp;
        this.rpcServerPort = rpcServerPort;
    }

    public RpcClientExecutor(EServiceCommand serviceCommand) {
        this.serviceCommand = serviceCommand;
    }

    public EServiceCommand getServiceCommand() {
        return serviceCommand;
    }

    public void setServiceCommand(EServiceCommand serviceCommand) {
        this.serviceCommand = serviceCommand;
    }

    public String getRpcServerIp() {
        return rpcServerIp;
    }

    public void setRpcServerIp(String rpcServerIp) {
        this.rpcServerIp = rpcServerIp;
    }

    public int getRpcServerPort() {
        return rpcServerPort;
    }

    public void setRpcServerPort(int rpcServerPort) {
        this.rpcServerPort = rpcServerPort;
    }

    @SuppressWarnings("unchecked")
    <T> T rpcExecutor(String rpcBeanId, Object[] parameters, Class<?> returnType){
        ServerDefinition serverDefinition = null;
        if (rpcServerIp == null || rpcServerPort == 0) {
            try {
                PropertiesParser.loadProperties("/smc.properties");
                String manageCenterIp = PropertiesParser.value("manageCenterIp");
                String manageCenterPort = PropertiesParser.value("manageCenterPort");
                List<ServerDefinition> serverDefinitionList = requestServerManageCenter(manageCenterIp, Integer.valueOf(manageCenterPort));
                serverDefinition = chooseOneServer(serverDefinitionList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (serverDefinition != null) {
            setRpcServerIp(serverDefinition.getIp());
            setRpcServerPort(serverDefinition.getPort());
        }

        Socket socket = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        Object result = null;
        try {
            socket = new Socket(rpcServerIp, rpcServerPort);

            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(gson.toJson(rpcBeanId));
            dos.writeUTF(gson.toJson(parameters));
            dis = new DataInputStream(socket.getInputStream());
            String str = dis.readUTF();
            result = gson.fromJson(str, returnType);
        } catch (IOException e) {
            System.out.println("服务器断开 请重新请求服务");
            e.printStackTrace();
        } finally {
            CloseableUtil.close(dos, dis, socket);
        }
        return (T)result;
    }

    private ServerDefinition chooseOneServer(List<ServerDefinition> serverDefinitionList) throws Exception {
        if (serverDefinitionList == null || serverDefinitionList.isEmpty()) {
            throw new ServiceNotFoundException("没有找到拥有该服务的服务器");
        }
        ServerDefinition serverDefinition = serverDefinitionList.get(0);
        for (ServerDefinition sd : serverDefinitionList) {
            if (sd.getIntervalTime() < serverDefinition.getIntervalTime()) {
                serverDefinition = sd;
            }
        }
        return serverDefinition;
    }


    private List<ServerDefinition> requestServerManageCenter(String manageCenterIp, int manageCenterPort) {
        try {
            manageCenterServer = new Socket(manageCenterIp, manageCenterPort);

            DataOutputStream dos = new DataOutputStream(manageCenterServer.getOutputStream());

            dos.writeUTF(gson.toJson(EServiceCommand.REQUEST_SERVICE));
            dos.flush();

            DataInputStream dis = new DataInputStream(manageCenterServer.getInputStream());

            if (gson.fromJson(dis.readUTF(), EServiceCommand.class).equals(EServiceCommand.YES)) {
                if (this.serviceCommand != null) {
                    dos.writeUTF(gson.toJson(this.serviceCommand));
                    dos.flush();
                }
                return gson.fromJson(dis.readUTF(), new TypeToken<List<ServerDefinition>>(){}.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
