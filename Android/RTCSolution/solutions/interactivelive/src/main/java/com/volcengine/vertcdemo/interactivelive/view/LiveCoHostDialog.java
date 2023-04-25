// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.volcengine.vertcdemo.common.BaseDialog;
import com.volcengine.vertcdemo.core.eventbus.SolutionDemoEventManager;
import com.volcengine.vertcdemo.core.net.IRequestCallback;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.bean.GetActiveAnchorListResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.DialogLiveCoHostListBinding;
import com.volcengine.vertcdemo.interactivelive.event.AnchorLinkInviteEvent;
import com.volcengine.vertcdemo.interactivelive.event.AnchorLinkReplyEvent;
import com.volcengine.vertcdemo.utils.DebounceClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
/**
 * Anchor dialog
 */
public class LiveCoHostDialog extends BaseDialog {

    private final int mHighLightColor = Color.parseColor("#ffffff");
    private final int mNormalColor = Color.parseColor("#86909C");

    private final List<LiveUserInfo> mCoHostData = new ArrayList<>();

    private DialogLiveCoHostListBinding mViewBinding;

    private CoHostAdapter mCoHostAdapter;
    private final CoHostCallback mCoHostCallback;

    private final IRequestCallback<GetActiveAnchorListResponse> mGetActiveHostCallback = new IRequestCallback<GetActiveAnchorListResponse>() {
        @Override
        public void onSuccess(GetActiveAnchorListResponse data) {
            setCoHostList(data.anchorList);
        }

        @Override
        public void onError(int errorCode, String message) {

        }
    };

    public LiveCoHostDialog(Context context, CoHostCallback coHostCallback) {
        super(context, R.style.SolutionCommonDialog);
        this.mCoHostCallback = info -> {
            LiveCoHostDialog.this.dismiss();
            if (coHostCallback != null) {
                coHostCallback.onClick(info);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DialogLiveCoHostListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());
        
        initWindow();
        initUI(mCoHostCallback);
        setCanceledOnTouchOutside(true);
    }

    public void setCoHostList(List<LiveUserInfo> infoList) {
        if (infoList == null || infoList.size() == 0) {
            mViewBinding.noContentTip.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.noContentTip.setVisibility(View.GONE);
            mCoHostAdapter.setData(infoList);
        }
    }

    private void initUI(CoHostCallback coHostCallback) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mViewBinding.liveCoHostListRv.setLayoutManager(manager);
        mCoHostAdapter = new CoHostAdapter(mCoHostData, coHostCallback);
        mViewBinding.liveCoHostListRv.setAdapter(mCoHostAdapter);
        
        onClickCoHostTab();
        
        mViewBinding.liveCoHostList.setOnClickListener(DebounceClickListener.create(v -> onClickCoHostTab()));
        mViewBinding.liveHostPkList.setOnClickListener(DebounceClickListener.create(v -> onClickHostPKTab()));
    }

    private void initWindow() {
        Window window = getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window.setDimAmount(0);
        initData();
    }
    
    private void onClickCoHostTab() {
        mViewBinding.liveCoHostListRv.setVisibility(View.VISIBLE);
        mViewBinding.liveCoHostList.setTextColor(mHighLightColor);
        mViewBinding.liveHostPkList.setTextColor(mNormalColor);
        mViewBinding.liveCoHostListIndicator.setVisibility(View.VISIBLE);
        mViewBinding.liveHostPkListIndicator.setVisibility(View.GONE);
        mViewBinding.noContentTip.setVisibility(mCoHostAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        mViewBinding.noContentTip.setText(R.string.no_hosts_live);
    }

    private void onClickHostPKTab() {
        mViewBinding.liveCoHostListRv.setVisibility(View.GONE);
        mViewBinding.liveCoHostList.setTextColor(mNormalColor);
        mViewBinding.liveHostPkList.setTextColor(mHighLightColor);
        mViewBinding.liveCoHostListIndicator.setVisibility(View.GONE);
        mViewBinding.liveHostPkListIndicator.setVisibility(View.VISIBLE);
        mViewBinding.noContentTip.setVisibility(View.VISIBLE);
        mViewBinding.noContentTip.setText(R.string.host_arriving_soon);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnchorLinkInviteEvent(AnchorLinkInviteEvent event) {
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnchorLinkReplyEvent(AnchorLinkReplyEvent event) {
        dismiss();
    }

    private void initData() {
        LiveRTCManager.ins().getRTSClient().requestActiveHostList(mGetActiveHostCallback);
    }

    private static class CoHostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<LiveUserInfo> mData = new ArrayList<>();
        private final CoHostCallback mCoHostCallback;

        public CoHostAdapter(List<LiveUserInfo> data, CoHostCallback coHostCallback) {
            mData.addAll(data);
            mCoHostCallback = coHostCallback;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_host_list, parent, false);
            return new CoHostViewHolder(view, mCoHostCallback);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CoHostViewHolder) {
                ((CoHostViewHolder) holder).bind(mData.get(position));
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
    }

    private static class CoHostViewHolder extends RecyclerView.ViewHolder {

        private final TextView mUserPrefixTv;
        private final TextView mUserNameTv;
        private final TextView mOptionTv;
        private final CoHostCallback mCoHostCallback;
        private LiveUserInfo mInfo;

        public CoHostViewHolder(@NonNull View itemView, CoHostCallback coHostCallback) {
            super(itemView);
            mUserPrefixTv = itemView.findViewById(R.id.item_voice_user_prefix);
            mUserNameTv = itemView.findViewById(R.id.item_voice_user_name);
            mOptionTv = itemView.findViewById(R.id.item_voice_user_option);
            mCoHostCallback = coHostCallback;
            mOptionTv.setOnClickListener(DebounceClickListener.create(v -> {
                if (mInfo != null && mCoHostCallback != null) {
                    LiveUserInfo info = mInfo;
                    mCoHostCallback.onClick(info);
                }
            }));
        }

        public void bind(LiveUserInfo info) {
            mInfo = info;
            mUserPrefixTv.setText(info.userName.substring(0, 1));
            mUserNameTv.setText(info.userName);
            if (info.status == LiveDataManager.USER_STATUS_HOST_INVITING) {
                mOptionTv.setText(R.string.Initiate_send);
                mOptionTv.setAlpha(0.5F);
            } else if (info.status == LiveDataManager.USER_STATUS_CO_HOSTING) {
                mOptionTv.setText(R.string.connecting);
                mOptionTv.setAlpha(0.5F);
            } else {
                mOptionTv.setText(R.string.invite);
                mOptionTv.setAlpha(1F);
            }
        }
    }

    public interface CoHostCallback {
        void onClick(LiveUserInfo info);
    }
}