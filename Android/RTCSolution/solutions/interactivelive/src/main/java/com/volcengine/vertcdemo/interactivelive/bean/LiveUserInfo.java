// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.bean;

import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.USER_STATUS_OTHER;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.common.GsonUtils;
import com.volcengine.vertcdemo.core.net.rts.RTSBizResponse;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;

import java.util.Map;
/**
 * User data model
 */
public class LiveUserInfo implements RTSBizResponse {

    @SerializedName("room_id")
    public String roomId;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("user_role")
    @LiveDataManager.LiveRoleType
    public int role;
    /**
     * status:
     * 1:Other
     * 2: The host is being invited to Lianmai
     * 3: The host is interacting with the host
     * 4: The audience is being invited to Lianmai
     * 5: The audience is interacting with each other
     */
    @LiveDataManager.LiveUserStatus
    public int status = USER_STATUS_OTHER;
    @LiveDataManager.MediaStatus
    @SerializedName("mic")
    public Integer micStatus;
    @LiveDataManager.MediaStatus
    @SerializedName("camera")
    public Integer cameraStatus;
    /**
     * Additional information storage width and height
     * Format: "extra":"{\"width\":0,\"height\":0}"
     */
    @SerializedName("extra")
    public String extra;
    @LiveDataManager.LiveLinkMicStatus
    @SerializedName("linkmic_status")
    public int linkMicStatus;

    public boolean isMicOn() {
        return micStatus == LiveDataManager.MEDIA_STATUS_ON;
    }

    public boolean isCameraOn() {
        return cameraStatus == LiveDataManager.MEDIA_STATUS_ON;
    }
    /**
     * Get the first character of the user name
     *
     * @return the first character of the username
     */
    public @NonNull
    String getNamePrefix() {
        if (TextUtils.isEmpty(userName)) {
            return "";
        } else {
            return userName.substring(0, 1);
        }
    }
    /**
     * Get the width of the user's streaming video
     */
    public int getWidth() {
        if (TextUtils.isEmpty(extra)) {
            return 0;
        }
        try {
            Map<String, Double> resp = GsonUtils.gson().fromJson(extra, Map.class);
            Double d = resp.get("width");
            return d == null ? 0 : d.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    /**
     * Get the height of the user's streaming video
     */
    public int getHeight() {
        if (TextUtils.isEmpty(extra)) {
            return 0;
        }
        try {
            Map<String, Double> resp = GsonUtils.gson().fromJson(extra, Map.class);
            Double d = resp.get("height");
            return d == null ? 0 : d.intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "LiveUserInfo{" +
                "roomId='" + roomId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", micStatus=" + micStatus +
                ", cameraStatus=" + cameraStatus +
                ", extra='" + extra + '\'' +
                ", linkMicStatus=" + linkMicStatus +
                '}';
    }

    public LiveUserInfo getDeepCopy() {
        LiveUserInfo userInfo = new LiveUserInfo();
        userInfo.userId = userId;
        userInfo.userName = userName;
        userInfo.roomId = roomId;
        userInfo.cameraStatus = cameraStatus;
        userInfo.micStatus = micStatus;
        userInfo.role = role;
        userInfo.status = status;
        userInfo.linkMicStatus = linkMicStatus;
        userInfo.extra = extra;
        return userInfo;
    }
}
