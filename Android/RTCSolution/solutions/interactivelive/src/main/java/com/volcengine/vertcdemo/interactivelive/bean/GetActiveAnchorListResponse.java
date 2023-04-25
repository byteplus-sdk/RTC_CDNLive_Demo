// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.bean;

import com.google.gson.annotations.SerializedName;
import com.volcengine.vertcdemo.core.net.rts.RTSBizResponse;

import java.util.List;
/**
 * Obtain the data model returned by the live broadcast anchor list interface
 */
public class GetActiveAnchorListResponse implements RTSBizResponse {

    @SerializedName("anchor_list")
    public List<LiveUserInfo> anchorList;

    @Override
    public String toString() {
        return "GetActiveHostListResponse{" +
                "anchorList=" + anchorList +
                '}';
    }
}
