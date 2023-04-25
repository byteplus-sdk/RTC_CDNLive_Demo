// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_ON;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.volcengine.vertcdemo.core.SolutionDataManager;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.ItemAudienceListBinding;
import com.volcengine.vertcdemo.utils.DebounceClickListener;
import com.volcengine.vertcdemo.utils.Utils;

/**
 * 单个嘉宾展示控件
 */
public class AudienceLayout extends FrameLayout {

    private LiveUserInfo mUserInfo;
    
    private ItemAudienceListBinding mViewBinding;
    private GuestGroupLayout.OnUserClickListener mOnUserClickListener;

    public AudienceLayout(Context context) {
        super(context);
        initView();
    }

    public AudienceLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AudienceLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.item_audience_list, this);
        mViewBinding = ItemAudienceListBinding.bind(view);

        bind(null, null);
        setOnClickListener(DebounceClickListener.create(v -> {
            if (mUserInfo != null && mOnUserClickListener != null) {
                mOnUserClickListener.onClick(mUserInfo);
            }
        }));
    }

    /**
     * 用户数据和UI绑定
     * @param userInfo 用户信息
     * @param userClickListener 用户点击事件
     */
    public void bind(LiveUserInfo userInfo, GuestGroupLayout.OnUserClickListener userClickListener) {
        if (userInfo == null) {
            setNetStatus(null , true);
            mViewBinding.audienceVideoViewContainer.removeAllViews();
            mViewBinding.audienceBackground.setVisibility(GONE);
            mViewBinding.audienceName.setText("");
            mViewBinding.audienceNamePrefix.setText("");
        } else {
            if (userInfo.cameraStatus == MEDIA_STATUS_ON) {
                mViewBinding.audienceBackground.setVisibility(GONE);
                mViewBinding.audienceNamePrefix.setVisibility(GONE);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                TextureView renderView = LiveRTCManager.ins().getUserRenderView(userInfo.userId);
                Utils.attachViewToViewGroup(mViewBinding.audienceVideoViewContainer, renderView, params);
                if (TextUtils.equals(userInfo.userId, SolutionDataManager.ins().getUserId())) {
                    LiveRTCManager.ins().setLocalVideoView(renderView);
                } else {
                    LiveRTCManager.ins().setRemoteVideoView(userInfo.userId,userInfo.roomId, renderView);
                }
            } else {
                mViewBinding.audienceVideoViewContainer.removeAllViews();
                mViewBinding.audienceBackground.setVisibility(VISIBLE);
                mViewBinding.audienceNamePrefix.setVisibility(VISIBLE);
                mViewBinding.audienceNamePrefix.setText(TextUtils.isEmpty(userInfo.userName) ? "" : userInfo.userName.substring(0, 1));
            }
            Drawable micRes;
            if (userInfo.micStatus == MEDIA_STATUS_ON) {
                micRes = null;
            } else {
                micRes = ContextCompat.getDrawable(getContext(), R.drawable.mic_off_red);
            }
            if (micRes != null) {
                micRes.setBounds(0, 0, (int) Utils.dp2Px(10), (int) Utils.dp2Px(10));
            }
            mViewBinding.audienceName.setCompoundDrawables(micRes, null, null, null);
            mViewBinding.audienceName.setText(userInfo.userName);
        }
        mUserInfo = userInfo == null ? null : userInfo.getDeepCopy();
        mOnUserClickListener = userClickListener;
    }

    /**
     * 网络状态显示
     * @param uid 用户id
     * @param isGood 网络状态
     */
    public void setNetStatus(String uid, boolean isGood) {
        if (mUserInfo == null || !TextUtils.equals(uid, mUserInfo.userId)) {
            return;
        }
        Drawable res = isGood
                ? ContextCompat.getDrawable(getContext(), R.drawable.net_status_good)
                : ContextCompat.getDrawable(getContext(), R.drawable.net_status_bad);
        mViewBinding.netStatusTv.setCompoundDrawablesWithIntrinsicBounds(res, null, null, null);
        mViewBinding.netStatusTv.setText(isGood
                ? getContext().getString(R.string.net_excellent)
                : getContext().getString(R.string.net_stuck_stopped));
    }
}
