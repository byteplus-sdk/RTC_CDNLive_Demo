// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;

import com.google.gson.annotations.SerializedName;
/**
 * The user temporarily leaves the event
 */
public class UserTemporaryLeaveEvent {

    @SerializedName("user_id")
    public String userId;
    @SerializedName("user_name")
    public String userName;

    @Override
    public String toString() {
        return "UserTemporaryLeaveEvent{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
