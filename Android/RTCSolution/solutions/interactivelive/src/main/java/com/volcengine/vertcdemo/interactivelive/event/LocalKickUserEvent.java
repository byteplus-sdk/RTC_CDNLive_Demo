// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;
/**
 * Local kick event
 */
public class LocalKickUserEvent {

    public String userId;

    public LocalKickUserEvent(String userId) {
        this.userId = userId;
    }
}
