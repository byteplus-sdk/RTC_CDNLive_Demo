// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.bean;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizResponse;
/**
 * Create the data model returned by the live room interface
 */
public class CreateLiveRoomResponse implements RTSBizResponse {

    @SerializedName("live_room_info")
    public LiveRoomInfo liveRoomInfo;
    @SerializedName("user_info")
    public LiveUserInfo userInfo;
    @SerializedName("stream_push_url")
    public String streamPushUrl;
    @SerializedName("rtm_token")
    public String rtmToken;
    @SerializedName("rtc_token")
    public String rtcToken;
    @SerializedName("rtc_room_id")
    public String rtcRoomId;

    @Override
    public String toString() {
        return "CreateLiveRoomResponse{" +
                "liveRoomInfo=" + liveRoomInfo +
                ", userInfo=" + userInfo +
                ", streamPushUrl='" + streamPushUrl + '\'' +
                ", rtmToken='" + rtmToken + '\'' +
                ", rtcToken='" + rtcToken + '\'' +
                ", rtcRoomId='" + rtcRoomId + '\'' +
                '}';
    }
}
