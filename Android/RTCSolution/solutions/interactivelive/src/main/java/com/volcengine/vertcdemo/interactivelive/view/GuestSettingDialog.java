// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_OFF;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_ON;
import static com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager.RESO1080;
import static com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager.RESO540;
import static com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager.RESO720;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.volcengine.vertcdemo.common.BaseDialog;
import com.volcengine.vertcdemo.core.SolutionDataManager;
import com.volcengine.vertcdemo.core.eventbus.SolutionDemoEventManager;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.DialogAudienceSettingLayoutBinding;
import com.volcengine.vertcdemo.interactivelive.event.MediaChangedEvent;
import com.volcengine.vertcdemo.utils.DebounceClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/**
 * Guest settings dialog
 */
public class GuestSettingDialog extends BaseDialog {

    private DialogAudienceSettingLayoutBinding mViewBinding;

    private final @LiveDataManager.LiveUserStatus int mUserStatus;
    private final String mRoomId;

    public GuestSettingDialog(@NonNull Context context, @LiveDataManager.LiveUserStatus int userStatus, String roomId) {
        super(context);
        mUserStatus = userStatus;
        mRoomId = roomId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViewBinding = DialogAudienceSettingLayoutBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        super.onCreate(savedInstanceState);
        
        mViewBinding.backIv.setOnClickListener(
                DebounceClickListener.create(v -> onClickBack()));
        mViewBinding.dialogTitle.setText(R.string.settings);

        setResolutionChecked();

        mViewBinding.resolutionLayout.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.resolution_540) {
                LiveRTCManager.ins().setPlayLiveStreamResolution(RESO540);
            } else if (checkedId == R.id.resolution_720) {
                LiveRTCManager.ins().setPlayLiveStreamResolution(RESO720);
            } else if (checkedId == R.id.resolution_1080) {
                LiveRTCManager.ins().setPlayLiveStreamResolution(RESO1080);
            }
        });
        
        mViewBinding.resolutionIv.setOnClickListener(
                DebounceClickListener.create(v -> onClickResolution()));
        mViewBinding.switchCameraIv.setOnClickListener(
                DebounceClickListener.create(v -> LiveRTCManager.ins().switchCamera()));
        mViewBinding.micIv.setOnClickListener(
                DebounceClickListener.create(v -> onClickMic()));
        mViewBinding.cameraIv.setOnClickListener(
                DebounceClickListener.create(v -> onClickCamera()));
        
        updateButtonStatusByRole();
    }

    private void setResolutionChecked() {
        String resolution = LiveRTCManager.ins().getPlayLiveStreamResolution();
        if (TextUtils.equals(resolution, RESO540)) {
            ((RadioButton) findViewById(R.id.resolution_540)).setChecked(true);
        } else if (TextUtils.equals(resolution, RESO720)) {
            ((RadioButton) findViewById(R.id.resolution_720)).setChecked(true);
        } else if (TextUtils.equals(resolution, RESO1080)) {
            ((RadioButton) findViewById(R.id.resolution_1080)).setChecked(true);
        }
    }

    private void updateCameraAndMicView(boolean cameraStatus, boolean micCamera) {
        mViewBinding.cameraIv.setImageResource(cameraStatus ? R.drawable.camera_on : R.drawable.camera_off_red);
        mViewBinding.micIv.setImageResource(micCamera ? R.drawable.mic_on : R.drawable.mic_off_red);
    }

    private void updateButtonStatusByRole() {
        if (mUserStatus == LiveDataManager.USER_STATUS_AUDIENCE_INTERACTING) {
            mViewBinding.cameraIv.setAlpha(1F);
            mViewBinding.cameraIv.setEnabled(true);
            mViewBinding.micIv.setAlpha(1F);
            mViewBinding.micIv.setEnabled(true);
            mViewBinding.switchCameraIv.setAlpha(1F);
            mViewBinding.switchCameraIv.setEnabled(true);
            mViewBinding.resolutionIv.setAlpha(0.5F);
            mViewBinding.resolutionIv.setEnabled(false);
            updateCameraAndMicView(LiveRTCManager.ins().isCameraOn(), LiveRTCManager.ins().isMicOn());
        } else {
            mViewBinding.cameraIv.setAlpha(0.5F);
            mViewBinding.cameraIv.setEnabled(false);
            mViewBinding.micIv.setAlpha(0.5F);
            mViewBinding.micIv.setEnabled(false);
            mViewBinding.switchCameraIv.setAlpha(0.5F);
            mViewBinding.switchCameraIv.setEnabled(false);
            mViewBinding.resolutionIv.setAlpha(1F);
            mViewBinding.resolutionIv.setEnabled(true);
            updateCameraAndMicView(true, true);
        }
    }
    /**
     * Click the resolution button
     */
    private void onClickResolution() {
        mViewBinding.settingItemsLayout.setVisibility(View.GONE);
        mViewBinding.resolutionLayout.setVisibility(View.VISIBLE);
        mViewBinding.backIv.setVisibility(View.VISIBLE);
        mViewBinding.dialogTitle.setText(R.string.resolution);
    }
    /**
     * Click the back button
     */
    private void onClickBack() {
        mViewBinding.settingItemsLayout.setVisibility(View.VISIBLE);
        mViewBinding.resolutionLayout.setVisibility(View.GONE);
        mViewBinding.backIv.setVisibility(View.GONE);
        mViewBinding.dialogTitle.setText(R.string.settings);
    }
    /**
     * Click the microphone switch
     */
    private void onClickMic() {
        LiveRTCManager.ins().turnOnMic();
        updateCameraAndMicView(LiveRTCManager.ins().isCameraOn(), LiveRTCManager.ins().isMicOn());
        updateMediaStatus();
    }
    /**
     * Click the camera switch
     */
    private void onClickCamera() {
        LiveRTCManager.ins().turnOnCamera();
        updateCameraAndMicView(LiveRTCManager.ins().isCameraOn(), LiveRTCManager.ins().isMicOn());
        updateMediaStatus();
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
        if (!TextUtils.isEmpty(mRoomId)) {
            int micStatus = LiveRTCManager.ins().isMicOn() ? MEDIA_STATUS_ON : MEDIA_STATUS_OFF;
            int cameraStatus = LiveRTCManager.ins().isCameraOn() ? MEDIA_STATUS_ON : MEDIA_STATUS_OFF;
            LiveRTCManager.ins().getRTSClient().updateMediaStatus(mRoomId, micStatus, cameraStatus, null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMediaChangedEvent(MediaChangedEvent event) {
        if (TextUtils.equals(SolutionDataManager.ins().getUserId(), event.userId)) {
            updateCameraAndMicView(LiveRTCManager.ins().isCameraOn(), LiveRTCManager.ins().isMicOn());
        }
    }
}
