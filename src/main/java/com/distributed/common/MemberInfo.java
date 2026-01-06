package com.distributed.common;

public class MemberInfo {

    private final String memberId;
    private final String host;
    private final int port;
    private volatile boolean alive;
    private volatile int messageCount;

    public MemberInfo(String memberId, String host, int port) {
        this.memberId = memberId;
        this.host = host;
        this.port = port;
        this.alive = true;
        this.messageCount = 0;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void incrementMessageCount() {
        this.messageCount++;
    }

    @Override
    public String toString() {
        return memberId + " at " + host + ":" + port;
    }
}
