// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;
/**
 * RTC SDK network connection state change event
 */
public class SDKNetworkConnectEvent {

    public boolean isConnect;

    public SDKNetworkConnectEvent(boolean isConnect) {
        this.isConnect = isConnect;
    }
}
