// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.databinding.LayoutLiveRoomControlsBinding;
import com.volcengine.vertcdemo.interactivelive.feature.liveroommain.IMainOption;
import com.volcengine.vertcdemo.utils.DebounceClickListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * Interactive live operation view
 */
public class LiveRoomControlsView extends LinearLayout {

    private LayoutLiveRoomControlsBinding mViewBinding;

    private IMainOption mainOption;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_DISABLE = 1;
    public static final int STATUS_IN_PROCESS = 2;

    @IntDef({STATUS_DISABLE, STATUS_IN_PROCESS, STATUS_NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ButtonStatus {
    }

    public LiveRoomControlsView(Context context) {
        super(context);
        initView();
    }

    public LiveRoomControlsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LiveRoomControlsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.layout_live_room_controls, this);
        mViewBinding = LayoutLiveRoomControlsBinding.bind(view);

        mViewBinding.liveRoomGift.setOnClickListener(DebounceClickListener.create(v -> {
            if (mainOption != null) {
                mainOption.onGiftClick();
            }
        }));
        mViewBinding.liveRoomPk.setOnClickListener(DebounceClickListener.create(v -> {
            if (mainOption != null) {
                mainOption.onCoHostClick();
            }
        }));
        mViewBinding.liveRoomLianmai.setOnClickListener(DebounceClickListener.create(v -> {
            if (mainOption != null) {
                mainOption.onAddGuestClick();
            }
        }));
        mViewBinding.liveRoomEffect.setOnClickListener(DebounceClickListener.create(v -> {
            if (mainOption != null) {
                mainOption.onVideoEffectClick();
            }
        }));
        mViewBinding.liveRoomSetting.setOnClickListener(DebounceClickListener.create(v -> {
            if (mainOption != null) {
                mainOption.onSettingClick();
            }
        }));
        mViewBinding.liveRoomExit.setOnClickListener(DebounceClickListener.create(v -> {
            if (mainOption != null) {
                mainOption.onExitClick();
            }
        }));
    }

    public void setRole(@LiveDataManager.LiveRoleType int role,
                        @LiveDataManager.LiveLinkMicStatus int status) {
        if (role == LiveDataManager.USER_ROLE_HOST) {
            mViewBinding.liveRoomPk.setVisibility(VISIBLE);
            mViewBinding.liveRoomGift.setVisibility(GONE);
        } else {
            mViewBinding.liveRoomPk.setVisibility(GONE);
            mViewBinding.liveRoomGift.setVisibility(VISIBLE);
        }
        if (role == LiveDataManager.USER_ROLE_HOST || status == LiveDataManager.LINK_MIC_STATUS_AUDIENCE_INTERACTING) {
            mViewBinding.liveRoomEffect.setVisibility(VISIBLE);
        } else {
            mViewBinding.liveRoomEffect.setVisibility(GONE);
        }
    }

    public void setMainOption(IMainOption mainOption) {
        this.mainOption = mainOption;
    }

    public void setAddGuestBtnStatus(@ButtonStatus int status) {
        mViewBinding.liveRoomLianmai.setAlpha(1f);
        mViewBinding.liveRoomLianmai.setClickable(true);
        if (status == STATUS_NORMAL) {
            mViewBinding.liveRoomLianmai.setImageResource(R.drawable.icon_live_lianmai);
        } else if (status == STATUS_DISABLE) {
            mViewBinding.liveRoomLianmai.setClickable(true);
            mViewBinding.liveRoomLianmai.setImageResource(R.drawable.icon_live_lianmai_disable);
        } else if (status == STATUS_IN_PROCESS) {
            mViewBinding.liveRoomLianmai.setClickable(false);
            mViewBinding.liveRoomLianmai.setAlpha(0.5f);
        }
    }

    public void setCoHostBtnStatus(@ButtonStatus int status) {
        mViewBinding.liveRoomPk.setAlpha(1f);
        mViewBinding.liveRoomPk.setClickable(true);
        if (status == STATUS_NORMAL) {
            mViewBinding.liveRoomPk.setImageResource(R.drawable.icon_live_pk);
        } else if (status == STATUS_DISABLE) {
            mViewBinding.liveRoomPk.setImageResource(R.drawable.icon_live_pk_disable);
            mViewBinding.liveRoomPk.setClickable(true);
        } else if (status == STATUS_IN_PROCESS) {
            mViewBinding.liveRoomPk.setAlpha(0.5f);
            mViewBinding.liveRoomPk.setClickable(false);
        }
    }
}
