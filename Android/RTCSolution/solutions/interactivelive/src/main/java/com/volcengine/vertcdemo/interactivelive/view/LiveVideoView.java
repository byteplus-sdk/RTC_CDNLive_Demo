// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.ViewLiveVideoBinding;
import com.volcengine.vertcdemo.utils.Utils;
/**
 * Handle the RTC layout of interactive live broadcast, single-host live broadcast and host room pk
 *
 * Update data using {@link #setLiveUserInfo(LiveUserInfo, LiveUserInfo, LiveUserInfo)}
 * Use {@link #updateNetStatus(String, boolean)} to update network status
 *
 * Internally uses {@link #muteRemoteUserAudio()} to mute remote audio
 */
public class LiveVideoView extends ConstraintLayout {

    private ViewLiveVideoBinding mViewBinding;
    private LiveUserInfo mLocalUserInfo;
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

        setLiveUserInfo(null, null, null);

        setCompoundDrawableSize(R.drawable.mic_off_1x, mViewBinding.singleMicStatusTv);
        setCompoundDrawableSize(R.drawable.camera_off_1x, mViewBinding.singleCameraStatusTv);
        setCompoundDrawableSize(R.drawable.mic_off_1x, mViewBinding.hostMicStatusTv);
        setCompoundDrawableSize(R.drawable.camera_off_1x, mViewBinding.hostCameraStatusTv);
        setCompoundDrawableSize(R.drawable.mic_off_1x, mViewBinding.coHostMicStatusTv);
        setCompoundDrawableSize(R.drawable.camera_off_1x, mViewBinding.coHostCameraStatusTv);
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
     * @param hostUserInfo   host information
     * @param coHostUserInfo pk mode peer user information
     */
    public void setLiveUserInfo(@Nullable LiveUserInfo hostUserInfo,
                                @Nullable LiveUserInfo coHostUserInfo,
                                @NonNull LiveUserInfo localUserInfo) {
        setMuteUserId(coHostUserInfo == null ? null : coHostUserInfo.userId);

        mLocalUserInfo = localUserInfo;
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
            boolean localIsHost = TextUtils.equals(userInfo.userId, SolutionDataManager.ins().getUserId());
            boolean localIsGuest = !localIsHost && mLocalUserInfo.linkMicStatus == LiveDataManager.LINK_MIC_STATUS_AUDIENCE_INTERACTING;
            mViewBinding.singleNetworkStatusTv.setVisibility(localIsHost || localIsGuest ? VISIBLE : GONE);
            mViewBinding.singleHeadTv.setVisibility(VISIBLE);
            mViewBinding.singleHeadTv.setText(userInfo.getNamePrefix());

            boolean isCameraOn = userInfo.isCameraOn();
            mViewBinding.singleHeadTv.setVisibility(isCameraOn ? GONE : VISIBLE);
            mViewBinding.singleCameraStatusTv.setVisibility(!isCameraOn && (localIsHost || localIsGuest) ? VISIBLE : GONE);

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
            boolean isMicOn = userInfo.isMicOn();
            mViewBinding.singleMicStatusTv.setVisibility(
                    !isMicOn && (localIsHost || localIsGuest) ? VISIBLE : GONE);
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
            boolean localIsHost = TextUtils.equals(userInfo.userId, SolutionDataManager.ins().getUserId());
            mViewBinding.hostNetworkStatusTv.setVisibility(localIsHost ? VISIBLE : GONE);
            mViewBinding.hostHeadTv.setVisibility(VISIBLE);
            mViewBinding.hostHeadTv.setText(userInfo.getNamePrefix());

            boolean isCameraOn = userInfo.isCameraOn();
            mViewBinding.hostHeadTv.setVisibility(isCameraOn ? GONE : VISIBLE);
            mViewBinding.hostCameraStatusTv.setVisibility(!isCameraOn && localIsHost ? VISIBLE : GONE);
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
                    !userInfo.isMicOn() && localIsHost ? VISIBLE : GONE);
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
            mViewBinding.coHostCameraStatusTv.setVisibility(!isCameraOn ? VISIBLE : GONE);
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

            mViewBinding.coHostMicStatusTv.setVisibility(!userInfo.isMicOn() ? VISIBLE : GONE);
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
        int resId = isGood ? R.drawable.net_status_good : R.drawable.net_status_bad;
        setCompoundDrawableSize(resId, tv);
        tv.setText(isGood ? R.string.net_excellent : R.string.net_stuck_stopped);
    }

    private void setCompoundDrawableSize(int drawableResId, TextView textView) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), drawableResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, (int) Utils.dp2Px(12), (int) Utils.dp2Px(12));
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setCompoundDrawablePadding((int) Utils.dp2Px(2));
        }
    }

}
