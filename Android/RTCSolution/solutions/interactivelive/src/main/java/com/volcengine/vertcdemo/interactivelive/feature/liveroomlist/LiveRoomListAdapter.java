// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.feature.liveroomlist;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.bean.LiveRoomInfo;
import com.volcengine.vertcdemo.utils.DebounceClickListener;

import java.util.ArrayList;
import java.util.List;
/**
 * Live room list adapter
 */
public class LiveRoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<LiveRoomInfo> mLiveRoomList = new ArrayList<>();
    private final OnLiveItemClickListener mOnLiveClickListener;

    public LiveRoomListAdapter(OnLiveItemClickListener itemClickListener) {
        mOnLiveClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_room_layout, parent, false);
        return new LiveRoomListViewHolder(view, mOnLiveClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LiveRoomListViewHolder) {
            ((LiveRoomListViewHolder) holder).bind(mLiveRoomList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mLiveRoomList.size();
    }


    public void setLiveRoomList(List<LiveRoomInfo> list) {
        if (list == null) {
            return;
        }
        mLiveRoomList.clear();
        mLiveRoomList.addAll(list);
        notifyDataSetChanged();
    }

    private static class LiveRoomListViewHolder extends RecyclerView.ViewHolder {

        private final TextView mNamePrefix;
        private final TextView mHostname;
        private final TextView mLiveRoomId;
        private LiveRoomInfo mLiveRoomInfo;

        public LiveRoomListViewHolder(@NonNull View itemView, OnLiveItemClickListener onClassItemClickListener) {
            super(itemView);
            mNamePrefix = itemView.findViewById(R.id.item_live_room_layout_avatar);
            mHostname = itemView.findViewById(R.id.item_live_room_layout_user_name);
            mLiveRoomId = itemView.findViewById(R.id.item_live_room_layout_room_id);
            itemView.setOnClickListener(DebounceClickListener.create(v -> {
                if (onClassItemClickListener != null) {
                    onClassItemClickListener.onLiveClick(mLiveRoomInfo);
                }
            }));
        }

        public void bind(LiveRoomInfo info) {
            mLiveRoomInfo = info;
            if (info == null) {
                mNamePrefix.setText("");
                mHostname.setText("");
                mLiveRoomId.setText("");
                return;
            }
            String hostName = info.anchorUserName;
            if (!TextUtils.isEmpty(hostName)) {
                mNamePrefix.setText(hostName.substring(0, 1));
            } else {
                mNamePrefix.setText("");
            }
            mHostname.setText(hostName);
            if (info.roomId == null) {
                info.roomId = "";
            }
            mLiveRoomId.setText(String.format("ID: %s", info.roomId));
        }
    }

    public interface OnLiveItemClickListener {
        void onLiveClick(LiveRoomInfo info);
    }
}
