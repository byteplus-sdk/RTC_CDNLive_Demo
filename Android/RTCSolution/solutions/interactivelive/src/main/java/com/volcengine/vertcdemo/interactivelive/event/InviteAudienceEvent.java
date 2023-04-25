// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;

import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
/**
 * Notification of local notification invitation result
 */
public class InviteAudienceEvent {

    public String userId;
    @LiveDataManager.InviteReply
    public int inviteReply;

    public InviteAudienceEvent(String userId, @LiveDataManager.InviteReply int inviteReply) {
        this.userId = userId;
        this.inviteReply = inviteReply;
    }
}
