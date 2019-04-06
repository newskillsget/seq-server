package com.gedo.server.domain;
/**
 * Created by Gedo on 2019/4/4.
 */
public class SeqReq {
    private String appId;
    private Long roomId;
    private int retry;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }
}
