// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.feature.createroom;

import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.Nullable;

import com.volcengine.vertcdemo.common.SolutionBaseActivity;
import com.volcengine.vertcdemo.common.SolutionToast;
import com.volcengine.vertcdemo.core.SolutionDataManager;
import com.volcengine.vertcdemo.core.eventbus.AppTokenExpiredEvent;
import com.volcengine.vertcdemo.core.net.ErrorTool;
import com.volcengine.vertcdemo.core.net.IRequestCallback;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.bean.CreateLiveRoomResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveRoomInfo;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.ActivityCreateLiveRoomBinding;
import com.volcengine.vertcdemo.interactivelive.feature.liveroommain.LiveRoomMainActivity;
import com.volcengine.vertcdemo.interactivelive.view.LiveSettingDialog;
import com.volcengine.vertcdemo.utils.DebounceClickListener;
import com.volcengine.vertcdemo.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/**
 * Create an interactive live room page
 */
public class CreateLiveRoomActivity extends SolutionBaseActivity {

    private static final String TAG = "CreateLiveRoom";

    private ActivityCreateLiveRoomBinding mViewBinding;

    private boolean mEnableStartLive = false;
    private LiveRoomInfo mRoomInfo;
    private LiveUserInfo mSelfInfo;
    private String mPushUrl;
    private String mRTMToken;
    private String mRTCRoomId;
    private String mRTCToken;

    private boolean mRequestingStartLive = false;

    private final IRequestCallback<CreateLiveRoomResponse> mCreateLiveRoomCallback = new IRequestCallback<CreateLiveRoomResponse>() {
        @Override
        public void onSuccess(CreateLiveRoomResponse data) {
            mEnableStartLive = true;
            mRoomInfo = data.liveRoomInfo;
            mSelfInfo = data.userInfo;
            mPushUrl = data.streamPushUrl;
            mRTMToken = data.rtmToken;
            mRTCRoomId = data.rtcRoomId;
            mRTCToken = data.rtcToken;
            LiveRTCManager.ins().startCaptureVideo(true);
            LiveRTCManager.ins().startCaptureAudio(true);
            dismissLoadingDialog();
        }

        @Override
        public void onError(int errorCode, String message) {
            SolutionToast.show(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
            dismissLoadingDialog();
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityCreateLiveRoomBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        requestCreateLiveRoom();
        LiveRTCManager.ins().switchCamera(true);
        LiveRTCManager.ins().startCaptureVideo(true);
        LiveRTCManager.ins().startCaptureAudio(true);
        LiveRTCManager.ins().setResolution(LiveDataManager.USER_ROLE_HOST,
                LiveRTCManager.ins().getWidth(LiveDataManager.USER_ROLE_HOST),
                LiveRTCManager.ins().getHeight(LiveDataManager.USER_ROLE_HOST));

        String hint = getString(R.string.application_experiencing_xxx_title, "20");
        mViewBinding.experienceTimeHint.setText(hint);
        mViewBinding.startLive.setOnClickListener(
                DebounceClickListener.create(v -> startLive()));
        mViewBinding.exitCreateLive.setOnClickListener(
                DebounceClickListener.create(v -> onBackPressed()));
        mViewBinding.switchCameraIv.setOnClickListener(
                DebounceClickListener.create(v -> switchCamera()));
        mViewBinding.effectIv.setOnClickListener(
                DebounceClickListener.create(v -> openVideoEffectDialog()));
        mViewBinding.settingsIv.setOnClickListener(
                DebounceClickListener.create(v -> openVideoVideoSettingDialog()));

        setLocalVideoView();
    }

    private void setLocalVideoView() {
        TextureView localVideoView = LiveRTCManager.ins().getUserRenderView(SolutionDataManager.ins().getUserId());
        Utils.attachViewToViewGroup(mViewBinding.previewViewContainer, localVideoView);
        LiveRTCManager.ins().setLocalVideoView(localVideoView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LiveRTCManager.ins().startCaptureVideo(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LiveRTCManager.ins().startCaptureVideo(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveRTCManager.ins().removeAllUserRenderView();
    }

    @Override
    protected boolean onMicrophonePermissionClose() {
        Log.d(TAG, "onMicrophonePermissionClose");
        finish();
        return true;
    }

    @Override
    protected boolean onCameraPermissionClose() {
        Log.d(TAG, "onCameraPermissionClose");
        finish();
        return true;
    }

    private void switchCamera() {
        LiveRTCManager.ins().switchCamera();
    }

    private void openVideoEffectDialog() {
        LiveRTCManager.ins().openEffectDialog(this);
    }

    private void openVideoVideoSettingDialog() {
        LiveSettingDialog settingDialog = new LiveSettingDialog(this, false, null);
        settingDialog.show();
    }

    private void startLive() {
        if (!mEnableStartLive) {
            SolutionToast.show(R.string.request_failed);
            return;
        }
        if (mRequestingStartLive) {
            return;
        }
        mRequestingStartLive = true;
        LiveRTCManager.ins().getRTSClient().requestStartLive(mRoomInfo.roomId, new IRequestCallback<LiveUserInfo>() {
            @Override
            public void onSuccess(LiveUserInfo userInfo) {
                LiveRoomMainActivity.startFromCreate(CreateLiveRoomActivity.this,
                        mRoomInfo, mSelfInfo, mPushUrl, mRTMToken, mRTCToken, mRTCRoomId);
                finish();
                mRequestingStartLive = false;
            }

            @Override
            public void onError(int errorCode, String message) {
                SolutionToast.show(R.string.request_failed);
                mRequestingStartLive = false;
            }
        });
    }

    private void requestCreateLiveRoom() {
        showLoadingDialog();
        LiveRTCManager.ins().getRTSClient().requestCreateLiveRoom(mCreateLiveRoomCallback);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenExpiredEvent(AppTokenExpiredEvent event) {
        finish();
    }
}
