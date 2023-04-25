// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.feature.liveroomlist;

import static com.volcengine.vertcdemo.core.net.rts.RTSInfo.KEY_RTS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vertcdemo.joinrtsparams.bean.JoinRTSRequest;
import com.vertcdemo.joinrtsparams.common.JoinRTSManager;
import com.volcengine.vertcdemo.common.IAction;
import com.volcengine.vertcdemo.common.SolutionBaseActivity;
import com.volcengine.vertcdemo.common.SolutionToast;
import com.volcengine.vertcdemo.core.SolutionDataManager;
import com.volcengine.vertcdemo.core.eventbus.AppTokenExpiredEvent;
import com.volcengine.vertcdemo.core.net.IRequestCallback;
import com.volcengine.vertcdemo.core.net.ServerResponse;
import com.volcengine.vertcdemo.core.net.rts.RTSBaseClient;
import com.volcengine.vertcdemo.core.net.rts.RTSInfo;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.bean.LiveRoomInfo;
import com.volcengine.vertcdemo.interactivelive.bean.LiveRoomListResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;
import com.volcengine.vertcdemo.interactivelive.bean.ReconnectInfo;
import com.volcengine.vertcdemo.interactivelive.core.LiveConstants;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.databinding.ActivityLiveRoomListBinding;
import com.volcengine.vertcdemo.interactivelive.feature.createroom.CreateLiveRoomActivity;
import com.volcengine.vertcdemo.interactivelive.feature.liveroommain.LiveRoomMainActivity;
import com.volcengine.vertcdemo.utils.AppUtil;
import com.volcengine.vertcdemo.utils.DebounceClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
/**
 * interact room list page
 */
public class LiveRoomListActivity extends SolutionBaseActivity {

    private static final String TAG = "LiveRoomListActivity";

    public static final int JOIN_LIVE_ROOM_REQUEST_CODE = 1000;
    public static final String JOIN_LIVE_ROOM_RESULT_MESSAGE = "result_message";

    private RTSInfo mRTSInfo;

    private ActivityLiveRoomListBinding mViewBinding;

    private LiveRoomListAdapter mLiveRoomListAdapter;

    private final IRequestCallback<LiveRoomListResponse> mRequestListRoomList = new IRequestCallback<LiveRoomListResponse>() {

        @Override
        public void onSuccess(LiveRoomListResponse data) {
            setRoomList(data.liveRoomList);
            ReconnectInfo reconnectInfo = data.recoverInfo;
            LiveUserInfo userInfo = data.user;
            if (reconnectInfo != null && userInfo != null) {
                reconnectInfo.liveRoomInfo.status = data.interactStatus;

                LiveRoomMainActivity.startFromReconnect(LiveRoomListActivity.this,
                        reconnectInfo, userInfo, data.interactStatus, data.interactUserList);
            }
        }

        @Override
        public void onError(int errorCode, String message) {
            showToast(message);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLiveRoomListBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initRTSInfo();

        mViewBinding.liveRoomListTitleBarLayout.setLeftBack(v -> finish());
        mViewBinding.liveRoomListTitleBarLayout.setTitle(R.string.interactive_live);
        mViewBinding.liveRoomListTitleBarLayout.setRightRefresh(
                DebounceClickListener.create(v -> requestRoomList()));

        mViewBinding.liveListRv.setLayoutManager(new LinearLayoutManager(this));
        mLiveRoomListAdapter = new LiveRoomListAdapter(info -> enterLiveRoom(info.roomId, info.anchorUserId));
        mViewBinding.liveListRv.setAdapter(mLiveRoomListAdapter);

        mViewBinding.liveRoomListCreateRoom.setOnClickListener(
                DebounceClickListener.create(v -> createLiveRoom()));

        LiveRTCManager.ins().rtcConnect(mRTSInfo);

        LiveRTCManager.ins().getRTSClient().login(mRTSInfo.rtsToken, (resultCode, message) -> {
            if (resultCode == RTSBaseClient.LoginCallBack.SUCCESS) {
                requestRoomList();
            } else {
                SolutionToast.show("Login Rtm Fail Error:" + resultCode + ",Message:" + message);
            }
        });
    }

    private void initRTSInfo() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mRTSInfo = intent.getParcelableExtra(RTSInfo.KEY_RTS);
        if (mRTSInfo == null || !mRTSInfo.isValid()) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveRTCManager.ins().clearRTSEventListener();
        LiveRTCManager.ins().getRTSClient().logout();
        LiveRTCManager.ins().destroyEngine();
    }

    @Override
    protected boolean onMicrophonePermissionClose() {
        Log.d(TAG, "onMicrophonePermissionClose");
        finish();
        return true;
    }

    @Override
    protected boolean onCameraPermissionClose() {
        Log.d(TAG, "onCameraPermissionClose");
        finish();
        return true;
    }
    /**
     * Obtain the list of live rooms from the business server
     */
    private void requestRoomList() {
        LiveRTCManager.ins().getRTSClient().requestLiveClearUser(() -> {
            LiveRTCManager.ins().getRTSClient().requestLiveRoomList(mRequestListRoomList);
        });
    }

    private void setRoomList(List<LiveRoomInfo> roomList) {
        if (roomList == null) {
            return;
        }
        mLiveRoomListAdapter.setLiveRoomList(roomList);
        if (roomList.size() == 0) {
            mViewBinding.noLiveTv.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.noLiveTv.setVisibility(View.GONE);
        }
    }

    private void createLiveRoom() {
        Intent intent = new Intent(this, CreateLiveRoomActivity.class);
        startActivity(intent);
    }

    private void enterLiveRoom(String roomId, String hostId) {
        if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(hostId)) {
            return;
        }
        LiveRoomMainActivity.startFromList(this, roomId, hostId, JOIN_LIVE_ROOM_REQUEST_CODE);
    }

    private void showToast(String toast) {
        SolutionToast.show(toast);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JOIN_LIVE_ROOM_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                showToast(data.getStringExtra(JOIN_LIVE_ROOM_RESULT_MESSAGE));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenExpiredEvent(AppTokenExpiredEvent event) {
        finish();
    }

    @Keep
    @SuppressWarnings("unused")
    public static void prepareSolutionParams(Activity activity, IAction<Object> doneAction) {
        Log.d(TAG, "prepareSolutionParams() invoked");
        IRequestCallback<ServerResponse<RTSInfo>> callback = new IRequestCallback<ServerResponse<RTSInfo>>() {
            @Override
            public void onSuccess(ServerResponse<RTSInfo> response) {
                RTSInfo data = response == null ? null : response.getData();
                if (data == null || !data.isValid()) {
                    onError(-1, "");
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClass(AppUtil.getApplicationContext(), LiveRoomListActivity.class);
                intent.putExtra(KEY_RTS, data);
                activity.startActivity(intent);
                if (doneAction != null) {
                    doneAction.act(null);
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                if (doneAction != null) {
                    doneAction.act(null);
                }
            }
        };
        JoinRTSRequest request = new JoinRTSRequest(LiveConstants.SOLUTION_NAME_ABBR, SolutionDataManager.ins().getToken());
        JoinRTSManager.setAppInfoAndJoinRTM(request, callback);
    }
}
