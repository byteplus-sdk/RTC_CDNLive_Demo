// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.INVITE_REPLY_ACCEPT;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.LINK_MIC_STATUS_AUDIENCE_INTERACTING;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.LINK_MIC_STATUS_OTHER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.volcengine.vertcdemo.common.BaseDialog;
import com.volcengine.vertcdemo.common.SolutionCommonDialog;
import com.volcengine.vertcdemo.core.eventbus.SolutionDemoEventManager;
import com.volcengine.vertcdemo.core.net.IRequestCallback;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.bean.GetAudienceListResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.DialogAudienceListLayoutBinding;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkFinishEvent;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkReplyEvent;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkStatusEvent;
import com.volcengine.vertcdemo.interactivelive.event.InviteAudienceEvent;
import com.volcengine.vertcdemo.interactivelive.event.LiveRoomUserEvent;
import com.volcengine.vertcdemo.utils.DebounceClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
/**
 * Invite guest dialog
 */
public class InviteGuestsDialog extends BaseDialog {
    
    private DialogAudienceListLayoutBinding mViewBinding;

    private final String mRoomId;
    private GuestListAdapter mAdapter;
    private final AddGuestClickListener mAddGuestClickListener;

    private final IRequestCallback<GetAudienceListResponse> mRequestAudienceList = new IRequestCallback<GetAudienceListResponse>() {
        @Override
        public void onSuccess(GetAudienceListResponse data) {
            setAddGuestList(data.audienceList);
        }

        @Override
        public void onError(int errorCode, String message) {
            setAddGuestList(null);
        }
    };

    public InviteGuestsDialog(@NonNull Context context, String roomId, AddGuestClickListener listener) {
        super(context);
        mRoomId = roomId;
        this.mAddGuestClickListener = info -> {
            listener.onClick(info);
            dismiss();
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViewBinding = DialogAudienceListLayoutBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        
        super.onCreate(savedInstanceState);

        mViewBinding.dialogTitle.setText(R.string.add_guests);
        mViewBinding.noContentTip.setText(R.string.no_audience);

        mViewBinding.dialogItemListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new GuestListAdapter(mAddGuestClickListener);
        mViewBinding.dialogItemListRv.setAdapter(mAdapter);
        
        mViewBinding.dialogAudienceListCloseAll.setOnClickListener(
                DebounceClickListener.create(v -> openCloseAllGuestInteractDialog()));
        initData();
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

    private void initData() {
        LiveRTCManager.ins().getRTSClient().requestAudienceList(mRoomId, mRequestAudienceList);
    }

    public void setAddGuestList(List<LiveUserInfo> infoList) {
        if (infoList == null || infoList.size() == 0) {
            mAdapter.setData(null);
            mViewBinding.noContentTip.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.noContentTip.setVisibility(View.GONE);
            mAdapter.setData(infoList);
        }
        updateCloseAllStatus();
    }

    private int getInteractUserCount() {
        int i = 0;
        for (LiveUserInfo info : mAdapter.mData) {
            if (info.linkMicStatus == LINK_MIC_STATUS_AUDIENCE_INTERACTING) {
                i++;
            }
        }
        return i;
    }

    @SuppressLint("DefaultLocale")
    private void openCloseAllGuestInteractDialog() {
        final SolutionCommonDialog dialog = new SolutionCommonDialog(getContext());
        dialog.setCancelable(true);
        dialog.setPositiveBtnText(R.string.exit);
        dialog.setMessage(getContext().getString(R.string.xxxsure_stop_live, String.valueOf(getInteractUserCount())));
        dialog.setNegativeListener((v) -> dialog.dismiss());
        dialog.setPositiveListener((v) -> {
            dialog.dismiss();
            LiveRTCManager.ins().getRTSClient().finishAudienceLinkByHost(
                    mRoomId,
                    new IRequestCallback<LiveResponse>() {
                        @Override
                        public void onSuccess(LiveResponse data) {

                        }

                        @Override
                        public void onError(int errorCode, String message) {

                        }
                    });
        });
        dialog.show();
    }

    private static class GuestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<LiveUserInfo> mData = new ArrayList<>();
        private final AddGuestClickListener mAddGuestCallback;

        public GuestListAdapter(AddGuestClickListener coHostCallback) {
            mAddGuestCallback = coHostCallback;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_host_list, parent, false);
            return new GuestListViewHolder(view, mAddGuestCallback);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof GuestListViewHolder) {
                ((GuestListViewHolder) holder).bind(mData.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void setData(List<LiveUserInfo> userInfo) {
            mData.clear();
            if (userInfo != null) {
                mData.addAll(userInfo);
            }
            notifyDataSetChanged();
        }

        public void updateUserStatus(String uid, @LiveDataManager.LiveLinkMicStatus int micStatus) {
            if (TextUtils.isEmpty(uid)) {
                return;
            }
            for (int i = 0; i < mData.size(); i++) {
                if (TextUtils.equals(uid, mData.get(i).userId)) {
                    mData.get(i).linkMicStatus = micStatus;
                    notifyItemChanged(i);
                }
            }
        }
    }

    private void updateCloseAllStatus() {
        boolean showCloseAll = false;
        for (LiveUserInfo info : mAdapter.mData) {
            if (info.linkMicStatus == LINK_MIC_STATUS_AUDIENCE_INTERACTING) {
                showCloseAll = true;
                break;
            }
        }
        mViewBinding.dialogAudienceListCloseAll.setVisibility(showCloseAll ? View.VISIBLE : View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveRoomUserEvent(LiveRoomUserEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkStatusEvent(AudienceLinkStatusEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkReplyEvent(AudienceLinkReplyEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkFinishEvent(AudienceLinkFinishEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInviteAudienceEvent(InviteAudienceEvent event) {
        int status;
        if (event.inviteReply == LiveDataManager.INVITE_REPLY_WAITING) {
            status = LiveDataManager.LINK_MIC_STATUS_AUDIENCE_INVITING;
        } else if (event.inviteReply == INVITE_REPLY_ACCEPT) {
            status = LiveDataManager.LINK_MIC_STATUS_AUDIENCE_INVITING;
        } else {
            status = LINK_MIC_STATUS_OTHER;
        }
        mAdapter.updateUserStatus(event.userId, status);
    }

    private static class GuestListViewHolder extends RecyclerView.ViewHolder {

        private final TextView mUserPrefixTv;
        private final TextView mUserNameTv;
        private final TextView mOptionTv;
        private final AddGuestClickListener mCoHostCallback;
        private LiveUserInfo mInfo;

        public GuestListViewHolder(@NonNull View itemView, AddGuestClickListener coHostCallback) {
            super(itemView);
            mUserPrefixTv = itemView.findViewById(R.id.item_voice_user_prefix);
            mUserNameTv = itemView.findViewById(R.id.item_voice_user_name);
            mOptionTv = itemView.findViewById(R.id.item_voice_user_option);
            mCoHostCallback = coHostCallback;
            mOptionTv.setOnClickListener(DebounceClickListener.create(v -> {
                if (mInfo != null && mCoHostCallback != null) {
                    LiveUserInfo info = mInfo;
                    if (info.linkMicStatus == LINK_MIC_STATUS_OTHER) {
                        mCoHostCallback.onClick(info);
                    }
                }
            }));
        }

        public void bind(LiveUserInfo info) {
            mInfo = info;
            mUserPrefixTv.setText(info.userName.substring(0, 1));
            mUserNameTv.setText(info.userName);
            if (info.linkMicStatus == LiveDataManager.LINK_MIC_STATUS_AUDIENCE_INVITING) {
                mOptionTv.setText(R.string.Initiate_send);
                mOptionTv.setAlpha(0.5F);
            } else if (info.linkMicStatus == LINK_MIC_STATUS_AUDIENCE_INTERACTING) {
                mOptionTv.setText(R.string.connecting);
                mOptionTv.setAlpha(0.5F);
            } else {
                mOptionTv.setText(R.string.invite);
                mOptionTv.setAlpha(1F);
            }
        }
    }

    public interface AddGuestClickListener {
        void onClick(LiveUserInfo info);
    }
}
