// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.core.net.rts;

import android.text.TextUtils;

import androidx.annotation.CallSuper;

import com.ss.bytertc.engine.handler.IRTCRoomEventHandler;
import com.ss.bytertc.engine.type.ErrorCode;
import com.volcengine.vertcdemo.core.eventbus.RTSLogoutEvent;
import com.volcengine.vertcdemo.core.eventbus.SolutionDemoEventManager;

import org.json.JSONObject;

public class RTCRoomEventHandlerWithRTS extends IRTCRoomEventHandler {

    private static final String UID_BIZ_SERVER = "server";
    private RTSBaseClient mBaseClient;

    public void setBaseClient(RTSBaseClient baseClient) {
        this.mBaseClient = baseClient;
    }

    /**
     * 判断 onRoomStateChanged 中的extraInfo，是不是首次加入房间成功
     *
     * @param extraInfo 额外信息
     * @return true:加入房间成功
     */
    protected boolean isFirstJoinRoomSuccess(int state, String extraInfo) {
        return joinRoomType(extraInfo) == 0 && state == 0;
    }

    /**
     * 判断 onRoomStateChanged 中的extraInfo，是不是重连加入房间成功
     *
     * @param extraInfo 额外信息
     * @return true:重连加入房间成功
     */
    protected boolean isReconnectSuccess(int state, String extraInfo) {
        return joinRoomType(extraInfo) == 1 && state == 0;
    }

    /**
     * 获取进房类型
     *
     * @param extraInfo 进房回调接口返回的额外信息
     *                  joinType表示加入房间的类型，0为首次进房，1为重连进房。
     */
    protected final int joinRoomType(String extraInfo) {
        int joinType = -1;
        try {
            // 341后 SDK传的固定键
            JSONObject json = new JSONObject(extraInfo);
            joinType = json.getInt("join_type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return joinType;
    }

    private void onMessageReceived(String fromUid, String message) {
        //来自业务服务器的响应或者通知
        if (TextUtils.equals(UID_BIZ_SERVER, fromUid)) {
            if (mBaseClient != null) {
                mBaseClient.onMessageReceived(fromUid, message);
            }
            return;
        }
        //客户端直接的通讯
        onRTCMessageReceived(fromUid, message);
    }

    /**
     * 非来自业务服务器的消息
     *
     * @param fromUid 发送消息uid
     * @param message 发送内容
     */
    protected void onRTCMessageReceived(String fromUid, String message) {
    }

    @Override
    public void onRoomMessageReceived(String uid, String message) {
        onMessageReceived(uid, message);
    }

    @Override
    public void onUserMessageReceived(String uid, String message) {
        onMessageReceived(uid, message);
    }

    @CallSuper
    @Override
    public void onRoomStateChanged(String roomId, String uid, int state, String extraInfo) {
        if (state == ErrorCode.ERROR_CODE_DUPLICATE_LOGIN) {
            SolutionDemoEventManager.post(new RTSLogoutEvent());
        }
    }
}