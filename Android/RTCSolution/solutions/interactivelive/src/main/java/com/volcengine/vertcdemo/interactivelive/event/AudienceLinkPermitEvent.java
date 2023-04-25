// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizInform;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;

import java.util.List;
/**
 * The anchor responds to the audience connection application event
 */
public class AudienceLinkPermitEvent implements RTSBizInform {

    @SerializedName("linker_id")
    public String linkerId;
    @SerializedName("permit_type")
    public int permitType;
    @SerializedName("rtc_room_id")
    public String rtcRoomId;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("rtc_token")
    public String rtcToken;
    @SerializedName("rtc_user_list")
    public List<LiveUserInfo> rtcUserList;

    @Override
    public String toString() {
        return "AudienceLinkPermitEvent{" +
                "linkerId='" + linkerId + '\'' +
                ", permitType=" + permitType +
                ", rtcRoomId='" + rtcRoomId + '\'' +
                ", userId='" + userId + '\'' +
                ", rtcToken='" + rtcToken + '\'' +
                ", rtcUserList=" + rtcUserList +
                '}';
    }
}
