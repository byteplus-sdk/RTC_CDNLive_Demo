// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.volcengine.vertcdemo.core.SolutionDataManager;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.ViewLiveVideoBinding;
import com.volcengine.vertcdemo.utils.Utils;
/**
 * Handle the RTC layout of interactive live broadcast, single-host live broadcast and host room pk
 *
 * Update data using {@link #setLiveUserInfo(LiveUserInfo, LiveUserInfo)}
 * Use {@link #updateNetStatus(String, boolean)} to update network status
 *
 * Internally uses {@link #muteRemoteUserAudio()} to mute remote audio
 */
public class LiveVideoView extends ConstraintLayout {

    private ViewLiveVideoBinding mViewBinding;
    private LiveUserInfo mHostUserInfo;
    private LiveUserInfo mCoHostUserInfo;
    private String mMuteUserId = null;

    public LiveVideoView(@NonNull Context context) {
        super(context);
        initView();
    }

    public LiveVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LiveVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.view_live_video, this);
        mViewBinding = ViewLiveVideoBinding.bind(view);

        mViewBinding.coHostAudio.setOnClickListener((v) -> muteRemoteUserAudio());

        setLiveUserInfo(null, null);
    }
    /**
     * Mute remote user audio
     */
    private void muteRemoteUserAudio() {
        if (mCoHostUserInfo == null) {
            mMuteUserId = null;
            return;
        }
        String userId = mCoHostUserInfo.userId;
        if (TextUtils.isEmpty(mMuteUserId)) {
            mMuteUserId = userId;
            LiveRTCManager.ins().muteRemoteAudio(userId, true);
        } else {
            mMuteUserId = null;
            LiveRTCManager.ins().muteRemoteAudio(userId, false);
        }

        updateMuteIcon();
    }

    private void updateMuteIcon() {
        int res;
        if (!TextUtils.isEmpty(mMuteUserId)
                && mCoHostUserInfo != null
                && TextUtils.equals(mMuteUserId, mCoHostUserInfo.userId)) {
            res = R.drawable.guest_audio_off;
        } else {
            res = R.drawable.guest_audio_on;
        }
        mViewBinding.coHostAudio.setImageResource(res);
    }
    /**
     * Set single anchor/pk mode user information
     * @param hostUserInfo host information
     * @param coHostUserInfo pk mode peer user information
     */
    public void setLiveUserInfo(@Nullable LiveUserInfo hostUserInfo,
                                @Nullable LiveUserInfo coHostUserInfo) {
        setMuteUserId(coHostUserInfo == null ? null : coHostUserInfo.userId);

        mHostUserInfo = hostUserInfo;
        mCoHostUserInfo = coHostUserInfo;

        boolean isCoHostMode = coHostUserInfo != null;
        setCoHostMode(isCoHostMode);

        if (isCoHostMode) {
            setSingleUserInfo(null);
            setHostUserInfo(hostUserInfo);
            setCoHostUserInfo(coHostUserInfo);
        } else {
            setSingleUserInfo(hostUserInfo);
            setHostUserInfo(null);
            setCoHostUserInfo(null);
        }
    }
    /**
     * Update the userId of the last muted user
     * @param userId user id
     */
    private void setMuteUserId(String userId) {
        if (!TextUtils.equals(mMuteUserId, userId)) {
            mMuteUserId = null;
        }
    }
    /**
     * Set single anchor/pk mode
     * @param isCoHostMode whether it is pk mode
     */
    private void setCoHostMode(boolean isCoHostMode) {
        mViewBinding.hostLayout.setVisibility(isCoHostMode ? VISIBLE : GONE);
    }
    /**
     * Set single anchor user information
     * @param userInfo single anchor user information
     */
    private void setSingleUserInfo(@Nullable LiveUserInfo userInfo) {
        if (userInfo == null) {
            mViewBinding.singleHeadTv.setText("");
            mViewBinding.singleHeadTv.setVisibility(GONE);
            mViewBinding.singleNetworkStatusTv.setVisibility(GONE);
            mViewBinding.singleMicStatusTv.setVisibility(GONE);
            mViewBinding.singleCameraStatusTv.setVisibility(GONE);
            mViewBinding.singleVideoContainer.removeAllViews();
        } else {
            mViewBinding.singleNetworkStatusTv.setVisibility(VISIBLE);
            mViewBinding.singleHeadTv.setVisibility(VISIBLE);
            mViewBinding.singleHeadTv.setText(userInfo.getNamePrefix());

            boolean isCameraOn = userInfo.isCameraOn();
            mViewBinding.singleHeadTv.setVisibility(isCameraOn ? GONE : VISIBLE);
            mViewBinding.singleCameraStatusTv.setVisibility(isCameraOn ? GONE : VISIBLE);

            if (isCameraOn) {
                TextureView view = LiveRTCManager.ins().getUserRenderView(userInfo.userId);
                if (TextUtils.equals(userInfo.userId, SolutionDataManager.ins().getUserId())) {
                    LiveRTCManager.ins().setLocalVideoView(view);
                } else {
                    LiveRTCManager.ins().setRemoteVideoView(userInfo.userId, userInfo.roomId, view);
                }
                Utils.attachViewToViewGroup(mViewBinding.singleVideoContainer, view);
            } else {
                mViewBinding.singleVideoContainer.removeAllViews();
            }

            mViewBinding.singleMicStatusTv.setVisibility(
                    userInfo.isMicOn() ? GONE : VISIBLE);
        }
    }
    /**
     * Set pk mode owner information
     * @param userInfo pk mode owner information
     */
    private void setHostUserInfo(@Nullable LiveUserInfo userInfo) {
        if (userInfo == null) {
            mViewBinding.hostHeadTv.setText("");
            mViewBinding.hostHeadTv.setVisibility(GONE);
            mViewBinding.hostNetworkStatusTv.setVisibility(GONE);
            mViewBinding.hostCameraStatusTv.setVisibility(GONE);
            mViewBinding.hostMicStatusTv.setVisibility(GONE);
            mViewBinding.hostVideoContainer.removeAllViews();
        } else {
            mViewBinding.hostNetworkStatusTv.setVisibility(VISIBLE);
            mViewBinding.hostHeadTv.setVisibility(VISIBLE);
            mViewBinding.hostHeadTv.setText(userInfo.getNamePrefix());

            boolean isCameraOn = userInfo.isCameraOn();
            mViewBinding.hostHeadTv.setVisibility(isCameraOn ? GONE : VISIBLE);
            mViewBinding.hostCameraStatusTv.setVisibility(isCameraOn ? GONE : VISIBLE);
            if (isCameraOn) {
                TextureView view = LiveRTCManager.ins().getUserRenderView(userInfo.userId);
                if (TextUtils.equals(userInfo.userId, SolutionDataManager.ins().getUserId())) {
                    LiveRTCManager.ins().setLocalVideoView(view);
                } else {
                    LiveRTCManager.ins().setRemoteVideoView(userInfo.userId, userInfo.roomId, view);
                }
                Utils.attachViewToViewGroup(mViewBinding.hostVideoContainer, view);
            } else {
                mViewBinding.hostVideoContainer.removeAllViews();
            }

            mViewBinding.hostMicStatusTv.setVisibility(
                    userInfo.isMicOn() ? GONE : VISIBLE);
        }
    }
    /**
     * Set pk mode peer information
     * @param userInfo pk mode peer information
     */
    private void setCoHostUserInfo(@Nullable LiveUserInfo userInfo) {
        if (userInfo == null) {
            mViewBinding.coHostHeadTv.setText("");
            mViewBinding.coHostHeadTv.setVisibility(GONE);
            mViewBinding.coHostNetworkStatusTv.setVisibility(GONE);
            mViewBinding.coHostCameraStatusTv.setVisibility(GONE);
            mViewBinding.coHostMicStatusTv.setVisibility(GONE);
            mViewBinding.coHostAvatar.setUserName("");
            mViewBinding.coHostVideoContainer.removeAllViews();
        } else {
            mViewBinding.coHostNetworkStatusTv.setVisibility(VISIBLE);
            mViewBinding.coHostHeadTv.setVisibility(VISIBLE);
            mViewBinding.coHostAvatar.setUserName(userInfo.userName);
            mViewBinding.coHostHeadTv.setText(userInfo.getNamePrefix());

            boolean isCameraOn = userInfo.isCameraOn();
            mViewBinding.coHostHeadTv.setVisibility(isCameraOn ? GONE : VISIBLE);
            mViewBinding.coHostCameraStatusTv.setVisibility(isCameraOn ? GONE : VISIBLE);
            if (isCameraOn) {
                TextureView view = LiveRTCManager.ins().getUserRenderView(userInfo.userId);
                if (TextUtils.equals(userInfo.userId, SolutionDataManager.ins().getUserId())) {
                    LiveRTCManager.ins().setLocalVideoView(view);
                } else {
                    LiveRTCManager.ins().setRemoteVideoView(userInfo.userId, userInfo.roomId, view);
                }
                Utils.attachViewToViewGroup(mViewBinding.coHostVideoContainer, view);
            } else {
                mViewBinding.coHostVideoContainer.removeAllViews();
            }

            mViewBinding.coHostMicStatusTv.setVisibility(
                    userInfo.isMicOn() ? GONE : VISIBLE);
        }

        updateMuteIcon();
    }
    /**
     * Update user network status
     * @param userId user id
     * @param isGood Network status is good or bad
     */
    public void updateNetStatus(String userId, boolean isGood) {
        TextView tv;
        if (mHostUserInfo != null && mCoHostUserInfo != null) {
            if (TextUtils.equals(userId, mHostUserInfo.userId)) {
                tv = mViewBinding.hostNetworkStatusTv;
            } else if (TextUtils.equals(userId, mCoHostUserInfo.userId)) {
                tv = mViewBinding.coHostNetworkStatusTv;
            } else {
                return;
            }
        } else {
            if (mHostUserInfo != null && TextUtils.equals(userId, mHostUserInfo.userId)) {
                tv = mViewBinding.singleNetworkStatusTv;
            } else {
                return;
            }
        }

        tv.setVisibility(VISIBLE);
        if (isGood) {
            tv.setText(R.string.net_excellent);
            tv.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(getContext(), R.drawable.net_status_good),
                    null, null, null);
        } else {
            tv.setText(R.string.net_stuck_stopped);
            tv.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(getContext(), R.drawable.net_status_bad),
                    null, null, null);
        }
    }
}
