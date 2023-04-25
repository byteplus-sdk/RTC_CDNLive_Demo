// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.feature.liveroommain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.volcengine.vertcdemo.interactivelive.R;

import java.util.ArrayList;
import java.util.List;
/**
 * chat message list adapter
 */
public class LiveChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CharSequence> mMsgList = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_chat_layout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatViewHolder) {
            ((ChatViewHolder) holder).bind(mMsgList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    public void addChatMsg(CharSequence info) {
        if (info == null) {
            return;
        }
        mMsgList.add(info);
        notifyItemInserted(mMsgList.size() - 1);
    }

    private static class ChatViewHolder extends RecyclerView.ViewHolder {

        private final TextView mChatTv;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mChatTv = (TextView) itemView;
        }

        public void bind(CharSequence msg) {
            mChatTv.setText(msg);
        }
    }
}
