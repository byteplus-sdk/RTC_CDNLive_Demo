// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.bean;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizResponse;

import java.util.List;
/**
 * Get the data model returned by the audience list interface
 */
public class GetAudienceListResponse implements RTSBizResponse {

    @SerializedName("audience_list")
    public List<LiveUserInfo> audienceList;

    @Override
    public String toString() {
        return "GetAudienceListResponse{" +
                "audienceList=" + audienceList +
                '}';
    }
}
