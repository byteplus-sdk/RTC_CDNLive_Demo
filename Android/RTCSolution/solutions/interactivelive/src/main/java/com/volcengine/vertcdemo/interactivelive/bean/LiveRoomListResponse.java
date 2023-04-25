// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.bean;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizResponse;

import java.util.List;
/**
 * The data model returned by the room list interface
 */
public class LiveRoomListResponse implements RTSBizResponse {

    @SerializedName("live_room_list")
    public List<LiveRoomInfo> liveRoomList;
    @SerializedName("user")
    public LiveUserInfo user;
    @SerializedName("reconnect_info")
    public ReconnectInfo recoverInfo;
    @SerializedName("interact_status")
    public int interactStatus;
    @SerializedName("interact_user_list")
    public List<LiveUserInfo> interactUserList;

    @Override
    public String toString() {
        return "LiveRoomListResponse{" +
                "liveRoomList=" + liveRoomList +
                ", user=" + user +
                ", recoverInfo=" + recoverInfo +
                ", interact_status=" + interactStatus +
                ", interactUserList=" + interactUserList +
                '}';
    }
}
