// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;

import java.util.ArrayList;
import java.util.List;
/**
 * Guest list layout
 */
public class GuestGroupLayout extends LinearLayout {

    private static final int COUNT = 6;

    private final List<AudienceLayout> mAudienceLayouts = new ArrayList<>();

    private String mRoomId;
    private String mHostUserId;
    private boolean mIsSelfHost;

    private final OnUserClickListener mUserClickListener = info -> {
        if (mIsSelfHost) {
            new GuestOptionDialog(getContext(), info, mRoomId, mHostUserId).show();
        }
    };

    public GuestGroupLayout(Context context) {
        super(context);
        initView();
    }

    public GuestGroupLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GuestGroupLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        setVerticalGravity(Gravity.BOTTOM);
        for (int i = 0; i < COUNT; i++) {
            AudienceLayout audienceLayout = new AudienceLayout(getContext());
            mAudienceLayouts.add(audienceLayout);
            addView(audienceLayout);
        }
        setUserList(null, null, false, null);
    }

    public void setUserList(String roomId, String hostUserId, boolean isSelfHost, List<LiveUserInfo> users) {
        mRoomId = roomId;
        mHostUserId = hostUserId;
        mIsSelfHost = isSelfHost;
        if (users == null || users.isEmpty()) {
            for (AudienceLayout audienceLayout : mAudienceLayouts) {
                audienceLayout.bind(null, mUserClickListener);
                audienceLayout.setVisibility(GONE);
            }
            return;
        }
        for (int i = mAudienceLayouts.size() - 1; i >= 0; i--) {
            AudienceLayout audienceLayout = mAudienceLayouts.get(i);
            if (i < users.size()) {
                audienceLayout.bind(users.get(i), mUserClickListener);
                audienceLayout.setVisibility(VISIBLE);
            } else {
                audienceLayout.bind(null, mUserClickListener);
                audienceLayout.setVisibility(GONE);
            }
        }
    }

    public void updateNetStatus(String uid, boolean isGood) {
        for (AudienceLayout audienceLayout : mAudienceLayouts) {
            audienceLayout.setNetStatus(uid, isGood);
        }
    }

    public interface OnUserClickListener {
        void onClick(LiveUserInfo userInfo);
    }
}
