package com.gary.core;

import java.util.Objects;

/**
 * describe:应用服务器描述
 *
 * @author gary
 * @date 2019/01/22
 */
public class ServerDefinition {
    private String ip;
    private int port;
    private long intervalTime;

    public ServerDefinition(String ip, int port) {
        this(ip, port, -1);
    }

    public ServerDefinition(String ip, int port, int intervalTime) {
        this.ip = ip;
        this.port = port;
        this.intervalTime = intervalTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerDefinition)) return false;
        ServerDefinition that = (ServerDefinition) o;
        return port == that.port && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, intervalTime);
    }

    @Override
    public String toString() {
        return "ServerDefinition{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", intervalTime=" + intervalTime +
                '}';
    }
}
