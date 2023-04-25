// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.databinding.UserAvatorViewBinding;
/**
 * Username display view
 */
public class AvatarView extends LinearLayout {

    private UserAvatorViewBinding mViewBinding;

    public AvatarView(Context context) {
        super(context);
        initView();
    }

    public AvatarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.user_avator_view, this);
        mViewBinding = UserAvatorViewBinding.bind(view);

        setBackgroundResource(R.drawable.button_black_rec_bg);
    }

    public void setUserName(String name) {
        if (TextUtils.isEmpty(name)) {
            mViewBinding.liveUserAvatar.setText("");
            mViewBinding.liveUserName.setText("");
        } else {
            mViewBinding.liveUserAvatar.setText(name.substring(0,1));
            mViewBinding.liveUserName.setText(name);
        }
    }
}
