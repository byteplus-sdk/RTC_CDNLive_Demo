// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.volcengine.vertcdemo.common.BaseDialog;
import com.volcengine.vertcdemo.common.SolutionToast;
import com.volcengine.vertcdemo.core.SolutionDataManager;
import com.volcengine.vertcdemo.core.net.ErrorTool;
import com.volcengine.vertcdemo.core.net.IRequestCallback;
import com.volcengine.vertcdemo.interactivelive.bean.LiveResponse;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.DialogGiftLayoutBinding;
/**
 * Gift dialog
 */
public class GiftDialog extends BaseDialog {

    private final String mRoomId;
    private final IRequestCallback<LiveResponse> mCallback = new IRequestCallback<LiveResponse>() {
        @Override
        public void onSuccess(LiveResponse data) {

        }

        @Override
        public void onError(int errorCode, String message) {
            SolutionToast.show(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
        }
    };

    public GiftDialog(@NonNull Context context, String roomId) {
        super(context);
        mRoomId = roomId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DialogGiftLayoutBinding viewBinding = DialogGiftLayoutBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        super.onCreate(savedInstanceState);

        viewBinding.flowerClick.setOnClickListener((v) -> sendGift("flower"));
        viewBinding.rocketClick.setOnClickListener((v) -> sendGift("rocket"));
    }

    private void sendGift(String giftType) {
        String userName = SolutionDataManager.ins().getUserName();
        LiveRTCManager.ins().getRTSClient().sendMessage(mRoomId, userName, giftType, mCallback);
        dismiss();
    }
}
