// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_OFF;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_ON;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.volcengine.vertcdemo.common.BaseDialog;
import com.volcengine.vertcdemo.core.net.IRequestCallback;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.bean.LiveResponse;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.core.ViewUtils;
import com.volcengine.vertcdemo.interactivelive.databinding.LayoutSettingDialogBinding;
import com.volcengine.vertcdemo.utils.DebounceClickListener;

import java.util.Locale;
/**
 * setting dialog
 */
public class LiveSettingDialog extends BaseDialog implements SeekBar.OnSeekBarChangeListener {

    private LayoutSettingDialogBinding mViewBinding;

    private final boolean isInLive;
    private final String mRoomId;

    private int minBitRate = 800;
    private int maxBitRate = 1200;
    private int currentBitRate = 0;

    public LiveSettingDialog(@NonNull Context context, boolean isInLive, String roomId) {
        super(context);
        this.isInLive = isInLive;
        this.mRoomId = roomId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViewBinding = LayoutSettingDialogBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        mViewBinding.switchCameraIv.setOnClickListener(DebounceClickListener.create(v -> LiveRTCManager.ins().switchCamera()));

        mViewBinding.micIv.setOnClickListener(DebounceClickListener.create(v -> {
            boolean turnOn = LiveRTCManager.ins().isMicOn();
            if (turnOn) {
                LiveRTCManager.ins().unPublishAudio();
            } else {
                LiveRTCManager.ins().publishAudio();
            }
            updateMediaStatus();
            updateCameraAndMicView();
        }));
        
        mViewBinding.cameraIv.setOnClickListener(DebounceClickListener.create(v -> {
            LiveRTCManager.ins().turnOnCamera();
            updateMediaStatus();
            updateCameraAndMicView();
        }));
        updateCameraAndMicView();
        
        mViewBinding.frameRateRg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.frame_rate_15) {
                LiveRTCManager.ins().setFrameRate(LiveDataManager.USER_ROLE_HOST, 15);
            } else if (checkedId == R.id.frame_rate_20) {
                LiveRTCManager.ins().setFrameRate(LiveDataManager.USER_ROLE_HOST, 20);
            }
        });
        int frameRateCheckedId;
        if (LiveRTCManager.ins().getFrameRate(LiveDataManager.USER_ROLE_HOST) == 15) {
            frameRateCheckedId = R.id.frame_rate_15;
        } else {
            frameRateCheckedId = R.id.frame_rate_20;
        }
        mViewBinding.frameRateRg.check(frameRateCheckedId);
        
        int rCheckedId;
        if (LiveRTCManager.ins().getWidth(LiveDataManager.USER_ROLE_HOST) == 540) {
            rCheckedId = R.id.resolution_540;
        } else if (LiveRTCManager.ins().getWidth(LiveDataManager.USER_ROLE_HOST) == 1080) {
            rCheckedId = R.id.resolution_1080;
        } else {
            rCheckedId = R.id.resolution_720;
        }
        mViewBinding.resolutionRg.check(rCheckedId);
        mViewBinding.resolutionRg.setOnCheckedChangeListener((group, checkedId) -> {
            int width = 0;
            int height = 0;
            if (checkedId == R.id.resolution_540) {
                width = 540;
                height = 960;
            } else if (checkedId == R.id.resolution_720) {
                width = 720;
                height = 1280;
            } else if (checkedId == R.id.resolution_1080) {
                width = 1080;
                height = 1920;
            }
            if (width != 0) {
                updateMinMaxBitrate(width);
                LiveRTCManager.ins().setResolution(LiveDataManager.USER_ROLE_HOST, width, height);
                if (isInLive && !TextUtils.isEmpty(mRoomId)) {
                    LiveRTCManager.ins().getRTSClient().updateResolution(mRoomId, width, height,
                            new IRequestCallback<LiveResponse>() {
                                @Override
                                public void onSuccess(LiveResponse data) {

                                }

                                @Override
                                public void onError(int errorCode, String message) {

                                }
                            });
                }
            }
            updateBitRate();
        });
        
        currentBitRate = LiveRTCManager.ins().getBitrate(LiveDataManager.USER_ROLE_HOST);
        updateMinMaxBitrate(LiveRTCManager.ins().getWidth(LiveDataManager.USER_ROLE_HOST));
        updateBitRate();
        mViewBinding.bitRateSeekbar.setOnSeekBarChangeListener(this);

        if (isInLive) {
            mViewBinding.videoSettingLayout.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.videoSettingLayout.setVisibility(View.GONE);
            LiveRTCManager.ins().setLiveTranscodingType(LiveRTCManager.ins().getLiveTranscodingType());
        }

        if (LiveRTCManager.ins().isLiveTranscoding()) {
            disableVideoSetting();
        } else {
            enableVideoSetting();
        }
    }

    private void updateBitRate() {
        if (currentBitRate < minBitRate) {
            currentBitRate = minBitRate;
        } else if (currentBitRate > maxBitRate) {
            currentBitRate = maxBitRate;
        }

        float progress = 100 * (currentBitRate - minBitRate) * 1f / (maxBitRate - minBitRate);
        mViewBinding.bitRateSeekbar.setProgress((int) progress);
        LiveRTCManager.ins().setBitrate(LiveDataManager.USER_ROLE_HOST, currentBitRate);
        mViewBinding.bitRateTv.setText(String.format(Locale.US, "%d kbps", currentBitRate));
    }

    private void updateCameraAndMicView() {
        mViewBinding.cameraIv.setImageResource(LiveRTCManager.ins().isCameraOn() ? R.drawable.camera_on : R.drawable.camera_off_red);
        mViewBinding.micIv.setImageResource(LiveRTCManager.ins().isMicOn() ? R.drawable.mic_on : R.drawable.mic_off_red);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            currentBitRate = (int) (minBitRate + (progress / 100f) * (maxBitRate - minBitRate));
            mViewBinding.bitRateTv.setText(String.format(Locale.US, "%d kbps", currentBitRate));
            LiveRTCManager.ins().setBitrate(LiveDataManager.USER_ROLE_HOST, currentBitRate);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void updateMediaStatus() {
        if (isInLive && !TextUtils.isEmpty(mRoomId)) {
            int micStatus = LiveRTCManager.ins().isMicOn() ? MEDIA_STATUS_ON : MEDIA_STATUS_OFF;
            int cameraStatus = LiveRTCManager.ins().isCameraOn() ? MEDIA_STATUS_ON : MEDIA_STATUS_OFF;
            LiveRTCManager.ins().getRTSClient().updateMediaStatus(mRoomId, micStatus, cameraStatus, null);
        }
    }

    private void disableVideoSetting() {
        ViewUtils.setGroupAlpha(mViewBinding.videoSettingGroup, 0.5f);
        ViewUtils.setGroupEnable(mViewBinding.videoSettingGroup, false);

        for (int i = 0, count = mViewBinding.frameRateRg.getChildCount(); i < count; i++) {
            mViewBinding.frameRateRg.getChildAt(i).setEnabled(false);
        }

        for (int i = 0, count = mViewBinding.resolutionRg.getChildCount(); i < count; i++) {
            mViewBinding.resolutionRg.getChildAt(i).setEnabled(false);
        }
    }

    private void enableVideoSetting() {
        ViewUtils.setGroupAlpha(mViewBinding.videoSettingGroup, 1f);
        ViewUtils.setGroupEnable(mViewBinding.videoSettingGroup, true);

        for (int i = 0, count = mViewBinding.frameRateRg.getChildCount(); i < count; i++) {
            mViewBinding.frameRateRg.getChildAt(i).setEnabled(true);
        }

        for (int i = 0, count = mViewBinding.resolutionRg.getChildCount(); i < count; i++) {
            mViewBinding.resolutionRg.getChildAt(i).setEnabled(true);
        }
    }

    private void updateMinMaxBitrate(int currentWidth) {
        if (currentWidth == 540) {
            minBitRate = 500;
            maxBitRate = 1520;
        } else if (currentWidth == 1080) {
            minBitRate = 1000;
            maxBitRate = 3800;
        } else {
            minBitRate = 800;
            maxBitRate = 1900;
        }
    }
}
