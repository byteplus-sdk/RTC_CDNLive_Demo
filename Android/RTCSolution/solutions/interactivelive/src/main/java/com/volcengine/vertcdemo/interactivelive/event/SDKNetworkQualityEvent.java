// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;

import com.ss.bytertc.engine.type.NetworkQualityStats;
/**
 * SDK network quality change event
 */
public class SDKNetworkQualityEvent {

    public String userId;
    /**
     * {@link NetworkQualityStats}
     */
    public int quality;

    public SDKNetworkQualityEvent(String userId, int quality) {
        this.userId = userId;
        this.quality = quality;
    }
}
