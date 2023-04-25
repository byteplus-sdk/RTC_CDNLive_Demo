// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizInform;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;
/**
 * Reply to the viewer's application connection event
 */
public class AudienceLinkApplyEvent implements RTSBizInform {
    @SerializedName("applicant")
    public LiveUserInfo applicant;
    @SerializedName("linker_id")
    public String linkerId;
    @SerializedName("extra")
    public String extra;

    @Override
    public String toString() {
        return "AudienceLinkApplyEvent{" +
                "applicant=" + applicant +
                ", linkerId='" + linkerId + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
