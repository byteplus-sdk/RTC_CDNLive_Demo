// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizInform;
/**
 * Anchor Lianmai end event
 */
public class AnchorLinkFinishEvent implements RTSBizInform {
    @SerializedName("rtc_room_id")
    public String rtcRoomId;

    @Override
    public String toString() {
        return "AudienceLinkFinishEvent{" +
                "rtcRoomId='" + rtcRoomId + '\'' +
                '}';
    }
}
