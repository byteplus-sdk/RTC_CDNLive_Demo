// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizInform;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
/**
 * Media state change event
 */
public class MediaChangedEvent implements RTSBizInform {

    @SerializedName("rtc_room_id")
    public String rtcRoomId;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("operator_user_id")
    public String operatorUserId;
    @SerializedName("camera")
    @LiveDataManager.MediaStatus
    public int camera;
    @SerializedName("mic")
    @LiveDataManager.MediaStatus
    public int mic;

    @Override
    public String toString() {
        return "MediaChangedEvent{" +
                "rtcRoomId='" + rtcRoomId + '\'' +
                ", userId='" + userId + '\'' +
                ", operatorUserId='" + operatorUserId + '\'' +
                ", camera=" + camera +
                ", mic=" + mic +
                '}';
    }
}
