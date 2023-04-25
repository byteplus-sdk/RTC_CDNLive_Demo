// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_KEEP;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_OFF;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_ON;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.volcengine.vertcdemo.common.BaseDialog;
import com.volcengine.vertcdemo.core.eventbus.SolutionDemoEventManager;
import com.volcengine.vertcdemo.core.net.IRequestCallback;
import com.volcengine.vertcdemo.interactivelive.bean.LiveResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.DialogGuestSettingBinding;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkFinishEvent;
import com.volcengine.vertcdemo.interactivelive.event.LocalKickUserEvent;
import com.volcengine.vertcdemo.interactivelive.event.MediaChangedEvent;
import com.volcengine.vertcdemo.utils.DebounceClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/**
 * Guest management dialog
 */
public class GuestOptionDialog extends BaseDialog {

    private final int mDisableColor = Color.parseColor("#22E5E6EB");
    private final int mEnableColor = Color.parseColor("#FFFFFF");

    private final String mRoomId;
    private final String mHostUserId;
    private final LiveUserInfo mUserInfo;

    private DialogGuestSettingBinding mViewBinding;

    public GuestOptionDialog(@NonNull Context context, LiveUserInfo userInfo, String roomId, String hostUserId) {
        super(context);
        this.mRoomId = roomId;
        this.mUserInfo = userInfo;
        this.mHostUserId = hostUserId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViewBinding = DialogGuestSettingBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        super.onCreate(savedInstanceState);

        mViewBinding.closeConnectBtn.setOnClickListener(DebounceClickListener.create(v -> onClickCloseConnect()));
        mViewBinding.cameraBtn.setOnClickListener(DebounceClickListener.create(v -> onClickCamera()));
        mViewBinding.micBtn.setOnClickListener(DebounceClickListener.create(v -> onClickMic()));
        mViewBinding.dismissBtn.setOnClickListener(DebounceClickListener.create(v -> dismiss()));

        updateMediaStatus();
    }

    private void onClickCloseConnect() {
        LiveRTCManager.ins().getRTSClient().kickAudienceByHost(
                mRoomId, mHostUserId, mRoomId, mUserInfo.userId,
                new IRequestCallback<LiveResponse>() {

                    @Override
                    public void onSuccess(LiveResponse data) {
                        dismiss();
                        SolutionDemoEventManager.post(new LocalKickUserEvent(mUserInfo.userId));
                    }

                    @Override
                    public void onError(int errorCode, String message) {

                    }
                });
    }

    private void onClickMic() {
        LiveRTCManager.ins().getRTSClient().requestManageGuest(
                mRoomId, mHostUserId, mRoomId, mUserInfo.userId,
                MEDIA_STATUS_KEEP, MEDIA_STATUS_OFF,
                new IRequestCallback<LiveResponse>() {

                    @Override
                    public void onSuccess(LiveResponse data) {
                        mUserInfo.cameraStatus = MEDIA_STATUS_OFF;
                        updateMediaStatus();
                        dismiss();
                    }

                    @Override
                    public void onError(int errorCode, String message) {

                    }
                });
    }

    private void onClickCamera() {
        LiveRTCManager.ins().getRTSClient().requestManageGuest(
                mRoomId, mHostUserId, mRoomId, mUserInfo.userId,
                MEDIA_STATUS_OFF, MEDIA_STATUS_KEEP,
                new IRequestCallback<LiveResponse>() {

                    @Override
                    public void onSuccess(LiveResponse data) {
                        mUserInfo.cameraStatus = MEDIA_STATUS_ON;
                        updateMediaStatus();
                        dismiss();
                    }

                    @Override
                    public void onError(int errorCode, String message) {

                    }
                });
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

    private void updateMediaStatus() {
        if (mUserInfo.cameraStatus == MEDIA_STATUS_ON) {
            mViewBinding.cameraBtn.setTextColor(mEnableColor);
        } else {
            mViewBinding.cameraBtn.setTextColor(mDisableColor);
        }
        if (mUserInfo.micStatus == MEDIA_STATUS_ON) {
            mViewBinding.micBtn.setTextColor(mEnableColor);
        } else {
            mViewBinding.micBtn.setTextColor(mDisableColor);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMediaChangedEvent(MediaChangedEvent event) {
        if (TextUtils.equals(event.userId, mUserInfo.userId)) {
            mUserInfo.cameraStatus = event.camera;
            mUserInfo.micStatus = event.mic;
            updateMediaStatus();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkFinishEvent(AudienceLinkFinishEvent event) {
        dismiss();
    }
}
