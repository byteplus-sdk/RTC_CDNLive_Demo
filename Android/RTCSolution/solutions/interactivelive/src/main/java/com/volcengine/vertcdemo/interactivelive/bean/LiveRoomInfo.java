// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.bean;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.interactivelive.feature.liveroommain.LiveRoomMainActivity;

import java.util.Map;
/**
 * Room data model
 */
public class LiveRoomInfo {

    @SerializedName("live_app_id")
    public String liveAppId;
    @SerializedName("rtc_app_id")
    public String rtcAppId;
    @SerializedName("room_id")
    public String roomId;
    @SerializedName("room_name")
    public String roomName;
    @SerializedName("host_user_id")
    public String anchorUserId;
    @SerializedName("host_user_name")
    public String anchorUserName;
    @LiveRoomMainActivity.RoomStatus
    @SerializedName("status")
    public int status;
    @SerializedName("audience_count")
    public int audienceCount;
    @SerializedName("start_time")
    public long startTime;
    @SerializedName("stream_pull_url_list")
    public Map<String, String> streamPullStreamList;

    @Override
    public String toString() {
        return "LiveRoomInfo{" +
                "liveAppId='" + liveAppId + '\'' +
                ", rtcAppId='" + rtcAppId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", anchorUserId='" + anchorUserId + '\'' +
                ", anchorUserName='" + anchorUserName + '\'' +
                ", status=" + status +
                ", audienceCount=" + audienceCount +
                ", startTime=" + startTime +
                ", streamPullStreamList=" + streamPullStreamList +
                '}';
    }
}
