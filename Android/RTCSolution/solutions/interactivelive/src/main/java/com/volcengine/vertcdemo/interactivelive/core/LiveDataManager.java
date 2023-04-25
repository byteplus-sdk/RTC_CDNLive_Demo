// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.core;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LiveDataManager {

    @IntDef({GUEST_MANAGE_TYPE_DISCONNECT, GUEST_MANAGE_TYPE_CLOSE_MIC, GUEST_MANAGE_TYPE_CLOSE_CAMERA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GuestManageType {
    }

    public static final int GUEST_MANAGE_TYPE_DISCONNECT = 1;
    public static final int GUEST_MANAGE_TYPE_CLOSE_MIC = 2;
    public static final int GUEST_MANAGE_TYPE_CLOSE_CAMERA = 3;

    @IntDef({INVITE_TYPE_GUEST_APPLY, INVITE_TYPE_HOST_INVITE_GUEST, INVITE_TYPE_HOST_INVITE_HOST, INVITE_TYPE_HOST_PK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface InviteType {
    }

    public static final int INVITE_TYPE_GUEST_APPLY = 1;
    public static final int INVITE_TYPE_HOST_INVITE_GUEST = 2;
    public static final int INVITE_TYPE_HOST_INVITE_HOST = 3;
    public static final int INVITE_TYPE_HOST_PK = 4;

    @IntDef({INTERACT_TYPE_HOST_END_PK, INTERACT_TYPE_END_CO_HOST, INTERACT_TYPE_GUEST_END, INTERACT_TYPE_HOST_END_ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface InteractType {
    }

    public static final int INTERACT_TYPE_HOST_END_PK = 1;
    public static final int INTERACT_TYPE_END_CO_HOST = 2;
    public static final int INTERACT_TYPE_GUEST_END = 3;
    public static final int INTERACT_TYPE_HOST_END_ALL = 4;

    @IntDef({MEDIA_STATUS_ON, MEDIA_STATUS_OFF, MEDIA_STATUS_KEEP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaStatus {
    }

    public static final int MEDIA_STATUS_KEEP = -1;
    public static final int MEDIA_STATUS_ON = 1;
    public static final int MEDIA_STATUS_OFF = 0;

    @IntDef({INVITE_RESULT_GUEST, INVITE_RESULT_HOST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface InviteResultType {
    }

    public static final int INVITE_RESULT_GUEST = 1;
    public static final int INVITE_RESULT_HOST = 2;

    @IntDef({INVITE_REPLY_WAITING, INVITE_REPLY_ACCEPT, INVITE_REPLY_REJECT, INVITE_REPLY_TIMEOUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface InviteReply {
    }

    public static final int INVITE_REPLY_WAITING = 0;
    public static final int INVITE_REPLY_ACCEPT = 1;
    public static final int INVITE_REPLY_REJECT = 2;
    public static final int INVITE_REPLY_TIMEOUT = 3;

    @IntDef({USER_ROLE_AUDIENCE, USER_ROLE_HOST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LiveRoleType {
    }

    public static final int USER_ROLE_AUDIENCE = 1;
    public static final int USER_ROLE_HOST = 2;

    @IntDef({USER_STATUS_OTHER, USER_STATUS_HOST_INVITING, USER_STATUS_CO_HOSTING,
            USER_STATUS_AUDIENCE_INVITING, USER_STATUS_AUDIENCE_INTERACTING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LiveUserStatus {
    }

    public static final int USER_STATUS_OTHER = 1;
    public static final int USER_STATUS_HOST_INVITING = 2;
    public static final int USER_STATUS_CO_HOSTING = 3;
    public static final int USER_STATUS_AUDIENCE_INVITING = 4;
    public static final int USER_STATUS_AUDIENCE_INTERACTING = 5;


    @IntDef({LINK_MIC_STATUS_OTHER, LINK_MIC_STATUS_AUDIENCE_INVITING, LINK_MIC_STATUS_AUDIENCE_APPLYING,
            LINK_MIC_STATUS_AUDIENCE_INTERACTING, LINK_MIC_STATUS_HOST_INTERACTING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LiveLinkMicStatus {
    }
    public static final int LINK_MIC_STATUS_OTHER = 0;
    public static final int LINK_MIC_STATUS_AUDIENCE_INVITING = 1;
    public static final int LINK_MIC_STATUS_AUDIENCE_APPLYING = 2;
    public static final int LINK_MIC_STATUS_AUDIENCE_INTERACTING = 3;
    public static final int LINK_MIC_STATUS_HOST_INTERACTING = 4;

    @IntDef({LIVE_FINISH_TYPE_NORMAL, LIVE_FINISH_TYPE_TIMEOUT, LIVE_FINISH_TYPE_IRREGULARITY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LiveFinishType {
    }

    public static final int LIVE_FINISH_TYPE_NORMAL = 1;
    public static final int LIVE_FINISH_TYPE_TIMEOUT = 2;
    public static final int LIVE_FINISH_TYPE_IRREGULARITY = 3;

    @IntDef({LIVE_PERMIT_TYPE_ACCEPT, LIVE_PERMIT_TYPE_REJECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LivePermitType {
    }
    public static final int LIVE_PERMIT_TYPE_ACCEPT = 1;
    public static final int LIVE_PERMIT_TYPE_REJECT = 2;

    private static LiveDataManager sInstance;

    public static LiveDataManager ins() {
        if (sInstance == null) {
            sInstance = new LiveDataManager();
        }
        return sInstance;
    }
}
