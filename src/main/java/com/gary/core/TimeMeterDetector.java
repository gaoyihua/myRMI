package com.gary.core;

import com.gary.exception.ServerOffLineException;
import com.gary.util.BaseTimeMeter;
import com.gary.util.CloseableUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sun.nio.ch.ThreadPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Random;

/**
 * describe:定时检测
 *
 * @author gary
 * @date 2019/01/22
 */
public class TimeMeterDetector extends BaseTimeMeter {
    private static int DEFAULT_AVG = 3;
    private DataOutputStream dos;
    private DataInputStream dis;
    private Socket socket;
    private ServerDefinition serverDefinition;
    private EServiceCommand command;
    private static final Gson gson = new GsonBuilder().create();
    private volatile int count;

    public TimeMeterDetector(DataOutputStream dos, DataInputStream dis, Socket socket, ServerDefinition serverDefinition, EServiceCommand command) {
        this(BaseTimeMeter.DEFAULT_WAITTIME, dos, dis, socket, serverDefinition, command);
    }

    public TimeMeterDetector(int waitTime, DataOutputStream dos, DataInputStream dis, Socket socket, ServerDefinition serverDefinition, EServiceCommand command) {
        setWaitTime(waitTime);
        this.dos = dos;
        this.dis = dis;
        this.serverDefinition = serverDefinition;
        this.command = command;
        this.socket = socket;
    }

    public void start() {
        System.out.println("startMeter");
        startTimeMeter();
    }

    public void stop() {
        stopTimeMeter();
    }

    @Override
    public void running() {
        System.out.println("runningMeter");
    }

    @Override
    public void stopRunning() {
        System.out.println("stopMeter");
    }

    @Override
    public void itIsThTime(){
        try {
            count ++ ;
            System.out.println(count);
            long startTime = System.currentTimeMillis();
            dos.writeUTF(gson.toJson(EServiceCommand.TEST));
            dos.flush();
            dis.readUTF();
            //模拟服务器负载大
            Thread.sleep(3000);
            long returnTime = System.currentTimeMillis();
            long intervalTime = returnTime - startTime;
            System.out.println(returnTime + "-" + startTime + "->" + intervalTime);

            if (count % TimeMeterDetector.DEFAULT_AVG == 0) {
                ServerManageCenter.setServiceIntervalTime(command, serverDefinition, intervalTime);
            }
        } catch (Exception e) {
            ServerManageCenter.removeService(command, serverDefinition);
            stop();
            CloseableUtil.close(dis, dos);
            System.out.println("服务器" + socket.getInetAddress() + "掉线");
            e.printStackTrace();
        }
    }
}
