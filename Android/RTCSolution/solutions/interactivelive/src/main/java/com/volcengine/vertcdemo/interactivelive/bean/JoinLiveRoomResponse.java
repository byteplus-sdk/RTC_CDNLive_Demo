// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.bean;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizResponse;
/**
 * Add the data model returned by the live room interface
 */
public class JoinLiveRoomResponse implements RTSBizResponse {

    @SerializedName("user_info")
    public LiveUserInfo liveUserInfo;
    @SerializedName("host_user_info")
    public LiveUserInfo liveHostUserInfo;
    @SerializedName("live_room_info")
    public LiveRoomInfo liveRoomInfo;
    @SerializedName("rtm_token")
    public String rtmToken;

    @Override
    public String toString() {
        return "JoinLiveRoomResponse{" +
                "liveUserInfo=" + liveUserInfo +
                ", liveHostUserInfo=" + liveHostUserInfo +
                ", liveRoomInfo=" + liveRoomInfo +
                ", rtmToken='" + rtmToken + '\'' +
                '}';
    }
}
