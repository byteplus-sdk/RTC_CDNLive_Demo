// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.volcengine.vertcdemo.common.BaseDialog;
import com.volcengine.vertcdemo.core.SolutionDataManager;
import com.volcengine.vertcdemo.core.eventbus.SolutionDemoEventManager;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.databinding.DialogRequestInteractLayoutBinding;
import com.volcengine.vertcdemo.interactivelive.event.InviteAudienceEvent;
import com.volcengine.vertcdemo.utils.DebounceClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/**
 * Audience application dialog
 */
public class RequestInteractDialog extends BaseDialog {

    private final long mLastApplyTs;

    private DialogRequestInteractLayoutBinding mViewBinding;

    private final RequestInteractClickListener listener;

    public RequestInteractDialog(Context context, long lastApplyTs, RequestInteractClickListener listener) {
        super(context);
        mLastApplyTs = lastApplyTs;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViewBinding = DialogRequestInteractLayoutBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        super.onCreate(savedInstanceState);

        mViewBinding.requestInteractBtn.setOnClickListener(DebounceClickListener.create(v -> {
            listener.onClick();
            dismiss();
        }));

        updateButton(mLastApplyTs);
    }

    private void updateButton(long lastApplyTs) {
        if (System.currentTimeMillis() - lastApplyTs > 4000) {
            mViewBinding.requestInteractBtn.setText(R.string.request_live);
            mViewBinding.requestInteractBtn.setAlpha(1f);
            mViewBinding.requestInteractBtn.setClickable(true);
        } else {
            mViewBinding.requestInteractBtn.setText(R.string.requesting);
            mViewBinding.requestInteractBtn.setAlpha(0.5f);
            mViewBinding.requestInteractBtn.setClickable(false);
        }
    }

    @Override
    public void show() {
        super.show();
        SolutionDemoEventManager.register(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        SolutionDemoEventManager.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInviteAudienceEvent(InviteAudienceEvent event) {
        if (!TextUtils.equals(event.userId, SolutionDataManager.ins().getUserId())) {
            return;
        }
        updateButton(mLastApplyTs);
        if (event.inviteReply == LiveDataManager.INVITE_REPLY_WAITING) {
            mViewBinding.requestInteractBtn.setText(R.string.requesting);
            mViewBinding.requestInteractBtn.setAlpha(0.5f);
        } else {
            dismiss();
        }
    }

    public interface RequestInteractClickListener {
        void onClick();
    }
}
