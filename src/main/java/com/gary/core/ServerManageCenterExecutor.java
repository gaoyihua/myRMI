package com.gary.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * describe:总控服务器执行者
 *
 * @author
 * @date 2019/01/22
 */
public class ServerManageCenterExecutor implements Runnable {
    private Socket socket;
    private ServerManageCenter serverManageCenter;
    private ThreadPoolExecutor threadPool;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean goon;
    private static final Gson gson = new GsonBuilder().create();

    public ServerManageCenterExecutor(Socket socket, ServerManageCenter serverManageCenter, ThreadPoolExecutor threadPool) throws Exception {
        this.socket = socket;
        this.serverManageCenter = serverManageCenter;
        this.threadPool = threadPool;
        this.dis = new DataInputStream(this.socket.getInputStream());
        this.dos = new DataOutputStream(this.socket.getOutputStream());
        this.threadPool.execute(this);
        this.goon = true;
    }

    @Override
    public void run() {
        try {
            EServiceCommand command = gson.fromJson(dis.readUTF(), EServiceCommand.class);
            switch (command) {
                case REGISTER_SERVICE:
                    dos.writeUTF(gson.toJson(EServiceCommand.YES));
                    dos.flush();
                    String[] serviceIpAndPort = dis.readUTF().split("-");
                    dealRegister(serviceIpAndPort);
                    break;
                case REQUEST_SERVICE:
                    dos.writeUTF(gson.toJson(EServiceCommand.YES));
                    dos.flush();
                    String[] services = dis.readUTF().split("-");
                    dealRequest(services);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void dealRequest(String[] services) {
        try {
            for (String service : services) {
                EServiceCommand command = gson.fromJson(service, EServiceCommand.class);
                List<ServerDefinition> serverList = ServerManageCenter.getService(command);
                dos.writeUTF(gson.toJson(serverList));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dealRegister(String[] serviceIpAndPort) {
        String serviceName = serviceIpAndPort[0];
        String ip = serviceIpAndPort[1];
        String port = serviceIpAndPort[2];

        ServerDefinition serverDefinition = new ServerDefinition(ip, Integer.valueOf(port));
        EServiceCommand command = gson.fromJson(serviceName, EServiceCommand.class);
        ServerManageCenter.putService(command, serverDefinition);

        TimeMeterDetector timeMeterDetector = new TimeMeterDetector(dos, dis, socket, serverDefinition, command);
        timeMeterDetector.start();
    }
}
