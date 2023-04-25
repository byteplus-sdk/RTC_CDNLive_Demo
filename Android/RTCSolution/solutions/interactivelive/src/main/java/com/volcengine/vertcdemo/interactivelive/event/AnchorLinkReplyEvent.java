// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizInform;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;

import java.util.List;
/**
 * The anchor responds to the connection invitation event
 */
public class AnchorLinkReplyEvent implements RTSBizInform {
    @SerializedName("invitee")
    public LiveUserInfo userInfo;
    @SerializedName("linker_id")
    public String linkerId;
    @SerializedName("reply_type")
    public int replyType;
    @SerializedName("rtc_room_id")
    public String rtcRoomId;
    @SerializedName("rtc_token")
    public String rtcToken;
    @SerializedName("rtc_user_list")
    public List<LiveUserInfo> rtcUserList;

    @Override
    public String toString() {
        return "AnchorLinkReplyEvent{" +
                "userInfo=" + userInfo +
                ", linkerId='" + linkerId + '\'' +
                ", replyType=" + replyType +
                ", rtcRoomId='" + rtcRoomId + '\'' +
                ", rtcToken='" + rtcToken + '\'' +
                ", rtcUserList=" + rtcUserList +
                '}';
    }
}
