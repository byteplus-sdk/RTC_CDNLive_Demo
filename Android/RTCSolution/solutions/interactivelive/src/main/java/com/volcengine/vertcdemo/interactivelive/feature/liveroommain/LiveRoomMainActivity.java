// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.feature.liveroommain;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.ss.bytertc.engine.type.NetworkQuality.NETWORK_QUALITY_EXCELLENT;
import static com.ss.bytertc.engine.type.NetworkQuality.NETWORK_QUALITY_GOOD;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.INVITE_REPLY_ACCEPT;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.INVITE_REPLY_REJECT;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.LINK_MIC_STATUS_AUDIENCE_INTERACTING;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.LINK_MIC_STATUS_HOST_INTERACTING;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.LIVE_PERMIT_TYPE_ACCEPT;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.LIVE_PERMIT_TYPE_REJECT;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_OFF;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.MEDIA_STATUS_ON;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.USER_ROLE_AUDIENCE;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.USER_ROLE_HOST;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.USER_STATUS_AUDIENCE_INTERACTING;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.USER_STATUS_CO_HOSTING;
import static com.volcengine.vertcdemo.interactivelive.core.LiveDataManager.USER_STATUS_OTHER;
import static com.volcengine.vertcdemo.interactivelive.view.LiveRoomControlsView.STATUS_DISABLE;
import static com.volcengine.vertcdemo.interactivelive.view.LiveRoomControlsView.STATUS_NORMAL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.volcengine.vertcdemo.common.GsonUtils;
import com.volcengine.vertcdemo.common.SolutionBaseActivity;
import com.volcengine.vertcdemo.common.SolutionCommonDialog;
import com.volcengine.vertcdemo.common.SolutionToast;
import com.volcengine.vertcdemo.common.WindowUtils;
import com.volcengine.vertcdemo.core.SolutionDataManager;
import com.volcengine.vertcdemo.core.eventbus.AppTokenExpiredEvent;
import com.volcengine.vertcdemo.core.eventbus.SolutionDemoEventManager;
import com.volcengine.vertcdemo.core.net.ErrorTool;
import com.volcengine.vertcdemo.core.net.IRequestCallback;
import com.volcengine.vertcdemo.interactivelive.R;
import com.volcengine.vertcdemo.interactivelive.bean.JoinLiveRoomResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveAnchorPermitAudienceResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveInviteResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveReconnectResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveReplyResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveResponse;
import com.volcengine.vertcdemo.interactivelive.bean.LiveRoomInfo;
import com.volcengine.vertcdemo.interactivelive.bean.LiveUserInfo;
import com.volcengine.vertcdemo.interactivelive.bean.ReconnectInfo;
import com.volcengine.vertcdemo.interactivelive.core.LiveDataManager;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTCManager;
import com.volcengine.vertcdemo.interactivelive.core.LiveRTSClient;
import com.volcengine.vertcdemo.interactivelive.databinding.ActivityLiveRoomMainBinding;
import com.volcengine.vertcdemo.interactivelive.event.AnchorLinkFinishEvent;
import com.volcengine.vertcdemo.interactivelive.event.AnchorLinkInviteEvent;
import com.volcengine.vertcdemo.interactivelive.event.AnchorLinkReplyEvent;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkApplyEvent;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkFinishEvent;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkInviteEvent;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkPermitEvent;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkReplyEvent;
import com.volcengine.vertcdemo.interactivelive.event.AudienceLinkStatusEvent;
import com.volcengine.vertcdemo.interactivelive.event.AudienceMediaUpdateEvent;
import com.volcengine.vertcdemo.interactivelive.event.GiftEvent;
import com.volcengine.vertcdemo.interactivelive.event.InviteAudienceEvent;
import com.volcengine.vertcdemo.interactivelive.event.LinkMicStatusEvent;
import com.volcengine.vertcdemo.interactivelive.event.LiveFinishEvent;
import com.volcengine.vertcdemo.interactivelive.event.LiveKickUserEvent;
import com.volcengine.vertcdemo.interactivelive.event.LiveRoomUserEvent;
import com.volcengine.vertcdemo.interactivelive.event.LocalKickUserEvent;
import com.volcengine.vertcdemo.interactivelive.event.LocalUpdatePullStreamEvent;
import com.volcengine.vertcdemo.interactivelive.event.MediaChangedEvent;
import com.volcengine.vertcdemo.interactivelive.event.SDKNetworkQualityEvent;
import com.volcengine.vertcdemo.interactivelive.event.SDKReconnectToRoomEvent;
import com.volcengine.vertcdemo.interactivelive.event.UserTemporaryLeaveEvent;
import com.volcengine.vertcdemo.interactivelive.view.GiftDialog;
import com.volcengine.vertcdemo.interactivelive.view.GuestSettingDialog;
import com.volcengine.vertcdemo.interactivelive.view.InviteGuestsDialog;
import com.volcengine.vertcdemo.interactivelive.view.InviteResultDialog;
import com.volcengine.vertcdemo.interactivelive.view.LiveCoHostDialog;
import com.volcengine.vertcdemo.interactivelive.view.LiveRoomControlsView;
import com.volcengine.vertcdemo.interactivelive.view.LiveSettingDialog;
import com.volcengine.vertcdemo.interactivelive.view.RequestInteractDialog;
import com.volcengine.vertcdemo.protocol.IVideoPlayer;
import com.volcengine.vertcdemo.protocol.ProtocolUtil;
import com.volcengine.vertcdemo.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * Interactive live room home page
 */
public class LiveRoomMainActivity extends SolutionBaseActivity {

    private static final String TAG = "LiveRoomMainActivity";

    private static final String EXTRA_REFER = "refer";
    private static final String EXTRA_ROOM_ID = "roomId";
    private static final String EXTRA_HOST_ID = "hostId";
    private static final String EXTRA_ROOM_INFO = "roomInfo";
    private static final String EXTRA_USER_INFO = "userInfo";
    private static final String EXTRA_PUSH_URL = "pushUrl";
    private static final String EXTRA_RTM_TOKEN = "rtmToken";
    private static final String EXTRA_RTC_TOKEN = "rtcToken";
    private static final String EXTRA_RTC_ROOM_ID = "rtcRoomId";
    private static final String EXTRA_RECONNECT_INFO = "reconnectInfo";
    private static final String EXTRA_INTERACT_STATUS = "interactStatus";
    private static final String EXTRA_INTERACT_USERS = "interactUsers";

    private static final String REFER_LIST = "list";
    private static final String REFER_CREATE = "create";
    private static final String REFER_RECONNECT = "reconnect";

    @RoomStatus
    private int mRoomStatus = ROOM_STATUS_LIVE;

    @IntDef({ROOM_STATUS_LIVE, ROOM_STATUS_GUEST_INTERACT, ROOM_STATUS_CO_HOST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RoomStatus {
    }

    public static final int ROOM_STATUS_LIVE = 0;
    public static final int ROOM_STATUS_GUEST_INTERACT = 3;
    public static final int ROOM_STATUS_CO_HOST = 4;
    // Under the identity of the audience, record the time of each initiation of the
    // application,and cannot apply again within 4S
    private long mLastApplyTs = 0;
    private String mPushUrl;
    private String mRTCToken;
    private String mRTSToken;
    private String mRTCRoomId;
    private String mLinkId;
    // Own information
    private LiveUserInfo mSelfInfo;
    // The user information of the current room owner
    private LiveUserInfo mHostInfo;
    // The user information of the connected host
    private LiveUserInfo mCoHostInfo;
    // room information
    private LiveRoomInfo mLiveRoomInfo;
    private final List<LiveUserInfo> mGuestList = new ArrayList<>();
    private int mAudienceCount = 0;
    private final Map<String, String> mPullStreamMap = new HashMap<>();

    private ActivityLiveRoomMainBinding mViewBinding;

    private IVideoPlayer mVideoPlayer;

    private LiveChatAdapter mLiveChatAdapter;

    private boolean hasLayouted = false;
    private boolean isLeaveByKickOut = false;
    // join room callback
    private final IRequestCallback<JoinLiveRoomResponse> mJoinRoomCallback =
            new IRequestCallback<JoinLiveRoomResponse>() {
                @Override
                public void onSuccess(JoinLiveRoomResponse data) {
                    mRoomStatus = data.liveRoomInfo.status;
                    mGuestList.clear();
                    mLiveRoomInfo = data.liveRoomInfo;
                    mSelfInfo = data.liveUserInfo;
                    mRTSToken = data.rtmToken;
                    mHostInfo = data.liveHostUserInfo;
                    updateOnlineGuestList(null);
                    initByJoinResponse(data.liveRoomInfo, data.liveUserInfo, data.liveRoomInfo.audienceCount,
                            data.liveRoomInfo.streamPullStreamList);
                }

                @Override
                public void onError(int errorCode, String message) {
                    String msg;
                    if (errorCode != 200) {
                        msg = getString(R.string.joining_room_failed);
                    } else {
                        msg = message;
                    }
                    SolutionCommonDialog dialog = new SolutionCommonDialog(LiveRoomMainActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage(msg);
                    dialog.setPositiveListener((v) -> finish());
                    dialog.show();
                }
            };
    // Respond to the invite callback
    private final IRequestCallback<LiveInviteResponse> mAnchorReplyInviteCallback =
            new IRequestCallback<LiveInviteResponse>() {

                @Override
                public void onSuccess(LiveInviteResponse data) {
                    mRoomStatus = ROOM_STATUS_CO_HOST;
                    for (LiveUserInfo info : data.userList) {
                        if (!TextUtils.equals(info.userId, SolutionDataManager.ins().getUserId())) {
                            mCoHostInfo = info;
                            break;
                        }
                    }
                    mViewBinding.mainControls.setCoHostBtnStatus(STATUS_DISABLE);

                    LiveRTCManager.ins().startCaptureVideo(mSelfInfo.isCameraOn());
                    LiveRTCManager.ins().startCaptureAudio(mSelfInfo.isMicOn());
                    mViewBinding.mainContainer.setLiveUserInfo(mSelfInfo, mCoHostInfo, mSelfInfo);
                    setCoHostVideoConfig(mCoHostInfo);

                    LiveRTCManager.ins().startForwardStreamToRooms(data.rtcRoomId,
                            mCoHostInfo.userId, data.rtcToken, mRTCRoomId,
                            mSelfInfo.userId, mPushUrl);
                }

                @Override
                public void onError(int errorCode, String message) {
                    showToast(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
                }
            };
    // Audience application connection callback
    private final IRequestCallback<LiveInviteResponse> mGuestApplyResponse =
            new IRequestCallback<LiveInviteResponse>() {
                @Override
                public void onSuccess(LiveInviteResponse data) {
                    mLastApplyTs = System.currentTimeMillis();
                    showToast(getString(R.string.request_sent_waiting));
                    mViewBinding.mainControls.setAddGuestBtnStatus(LiveRoomControlsView.STATUS_NORMAL);
                }

                @Override
                public void onError(int errorCode, String message) {
                    if (errorCode == 622) {
                        showToast(getString(R.string.request_sent_waiting));
                    } else {
                        mLastApplyTs = 0;
                        showToast(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
                        SolutionDemoEventManager.post(new InviteAudienceEvent(
                                SolutionDataManager.ins().getUserId(),
                                LiveDataManager.INVITE_REPLY_TIMEOUT));
                        mViewBinding.mainControls.setAddGuestBtnStatus(LiveRoomControlsView.STATUS_NORMAL);
                    }
                }
            };
    // finish interact callback
    private final IRequestCallback<LiveResponse> mInteractResponse =
            new IRequestCallback<LiveResponse>() {
                @Override
                public void onSuccess(LiveResponse data) {
                }

                @Override
                public void onError(int errorCode, String message) {
                    showToast(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
                }
            };
    // Audience leaves the live broadcast room callback
    private final IRequestCallback<LiveResponse> mLeaveLiveCallback =
            new IRequestCallback<LiveResponse>() {
                @Override
                public void onSuccess(LiveResponse data) {

                }

                @Override
                public void onError(int errorCode, String message) {

                }
            };
    // anchor finishes the live callback
    private final IRequestCallback<LiveResponse> mFinishLiveCallback =
            new IRequestCallback<LiveResponse>() {
                @Override
                public void onSuccess(LiveResponse data) {

                }

                @Override
                public void onError(int errorCode, String message) {

                }
            };
    // Audience side, Audience responds to host invitation callback
    private final IRequestCallback<LiveReplyResponse> mReplyInviteCallbackByAudience =
            new IRequestCallback<LiveReplyResponse>() {
                @Override
                public void onSuccess(LiveReplyResponse data) {
                    stopPlayLiveStream();

                    updateOnlineGuestList(data.rtcUserList);
                    LiveRTCManager.ins().joinRoom(data.rtcRoomId, mSelfInfo.userId, data.rtcToken);
                    LiveRTCManager.ins().startCaptureVideo(mSelfInfo.isCameraOn());
                    LiveRTCManager.ins().startCaptureAudio(mSelfInfo.isMicOn());

                    mRoomStatus = ROOM_STATUS_GUEST_INTERACT;
                    mSelfInfo.status = USER_STATUS_AUDIENCE_INTERACTING;
                    mSelfInfo.linkMicStatus = LINK_MIC_STATUS_AUDIENCE_INTERACTING;
                    mViewBinding.mainContainer.setLiveUserInfo(mHostInfo, null, mSelfInfo);
                    mViewBinding.mainControls.setAddGuestBtnStatus(STATUS_DISABLE);
                    mViewBinding.mainControls.setRole(mSelfInfo.role, LINK_MIC_STATUS_AUDIENCE_INTERACTING);
                    updatePlayerStatus();
                }

                @Override
                public void onError(int errorCode, String message) {
                    showToast(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
                }
            };

    private final IRequestCallback<LiveAnchorPermitAudienceResponse> mReplyInviteCallbackByHost =
            new IRequestCallback<LiveAnchorPermitAudienceResponse>() {

                @Override
                public void onSuccess(LiveAnchorPermitAudienceResponse data) {
                    mRoomStatus = ROOM_STATUS_GUEST_INTERACT;
                    updateOnlineGuestList(data.userList);
                    if (mGuestList.size() == 1) {
                        showToast(getString(R.string.click_seat_title));
                        LiveRTCManager.ins().joinRoom(data.rtcRoomId, mSelfInfo.userId, data.rtcToken);
                        LiveRTCManager.ins().updateLiveTranscodingWithAudience(data.rtcRoomId,
                                mSelfInfo.userId, mPushUrl, sortUserList(data.userList));

                    } else {
                        List<LiveUserInfo> audienceUserList = new ArrayList<>(mGuestList);
                        if (mHostInfo != null) {
                            audienceUserList.add(mHostInfo.getDeepCopy());
                        }
                        LiveRTCManager.ins().updateLiveTranscodingWithAudience(data.rtcRoomId, mSelfInfo.userId,
                                mPushUrl, sortUserList(audienceUserList));
                    }

                    for (LiveUserInfo info : mGuestList) {
                        SolutionDemoEventManager.post(new InviteAudienceEvent(info.userId,
                                LiveDataManager.INVITE_REPLY_ACCEPT));
                    }
                }

                @Override
                public void onError(int errorCode, String message) {
                    showToast(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
                }
            };
    // reconnect callback
    private final IRequestCallback<LiveReconnectResponse> mLiveReconnectCallback =
            new IRequestCallback<LiveReconnectResponse>() {
                @Override
                public void onSuccess(LiveReconnectResponse data) {
                    if (data.recoverInfo == null) {
                        SolutionToast.show(R.string.live_ended_title);
                        finish();
                    } else {
                        mSelfInfo = data.userInfo;
                        mRoomStatus = data.interactStatus;
                        setAudienceCount(data.recoverInfo.audienceCount);
                        updateOnlineGuestList(data.interactUserList);
                        initByJoinResponse(data.recoverInfo.liveRoomInfo, data.userInfo,
                                data.recoverInfo.audienceCount, data.recoverInfo.streamPullUrl);
                    }
                }

                @Override
                public void onError(int errorCode, String message) {
                    SolutionToast.show(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
                    finish();
                }
            };

    private final IMainOption mMainOption = new IMainOption() {

        @Override
        public void onGiftClick() {
            GiftDialog dialog = new GiftDialog(LiveRoomMainActivity.this, mLiveRoomInfo.roomId);
            dialog.show();
        }

        @Override
        public void onCoHostClick() {
            if (mRoomStatus == ROOM_STATUS_CO_HOST) {
                openFinishCoHostDialog();
            } else {
                openInviteCoHostDialog();
            }
        }

        @Override
        public void onAddGuestClick() {
            onGuestClick();
        }

        @Override
        public void onVideoEffectClick() {
            openVideoEffectDialog();
        }

        @Override
        public void onSettingClick() {
            openSettingDialog();
        }

        @Override
        public void onExitClick() {
            openLeaveDialog();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityLiveRoomMainBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.mainControls.setMainOption(mMainOption);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_audience);
        drawable.setBounds(0, 0,
                (int) Utils.dp2Px(22), (int) Utils.dp2Px(20));
        mViewBinding.audienceNumTv.setCompoundDrawables(drawable, null, null, null);

        mLiveChatAdapter = new LiveChatAdapter();
        mViewBinding.messageRv.setLayoutManager(new LinearLayoutManager(LiveRoomMainActivity.this,
                RecyclerView.VERTICAL, false));
        mViewBinding.messageRv.setAdapter(mLiveChatAdapter);

        if (hasLayouted) {
            return;
        }
        hasLayouted = true;

        if (!checkArgs()) {
            finish();
        }

        SolutionDemoEventManager.register(this);

        mVideoPlayer = ProtocolUtil.getPlayerInstance();
        if (mVideoPlayer != null) {
            mVideoPlayer.startWithConfiguration(LiveRoomMainActivity.this);
            mVideoPlayer.setSEICallback(s -> {
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                if (s.contains(LiveRTCManager.KEY_SEI_KEY_SOURCE)) {
                    if (s.contains(LiveRTCManager.KEY_SEI_VALUE_SOURCE_NONE)) {
                        togglePkLabel4Audience(false);
                        return;
                    }
                    if (s.contains(LiveRTCManager.KEY_SEI_VALUE_SOURCE_CO_HOST)) {
                        togglePkLabel4Audience(true);
                    }
                }
            });
        }
    }
    /**
     * check parameters
     * @return false: Failed the inspection, need to exit the room
     */
    private boolean checkArgs() {
        Intent intent = getIntent();
        if (intent == null) {
            return false;
        }
        String refer = intent.getStringExtra(EXTRA_REFER);
        if (TextUtils.equals(REFER_CREATE, refer)) {
            LiveRoomInfo roomInfo = GsonUtils.gson().fromJson(intent.getStringExtra(EXTRA_ROOM_INFO), LiveRoomInfo.class);
            LiveUserInfo userInfo = GsonUtils.gson().fromJson(intent.getStringExtra(EXTRA_USER_INFO), LiveUserInfo.class);
            mPushUrl = intent.getStringExtra(EXTRA_PUSH_URL);
            mRTSToken = intent.getStringExtra(EXTRA_RTM_TOKEN);
            mRTCToken = intent.getStringExtra(EXTRA_RTC_TOKEN);
            mRTCRoomId = intent.getStringExtra(EXTRA_RTC_ROOM_ID);
            if (roomInfo != null && userInfo != null) {
                mSelfInfo = userInfo;
                mLiveRoomInfo = roomInfo;
                LiveRTCManager.ins().startCaptureAudio(true);
                LiveRTCManager.ins().startCaptureVideo(true);
                initByJoinResponse(mLiveRoomInfo, mSelfInfo, 0, null);
                return true;
            } else {
                return false;
            }
        } else if (TextUtils.equals(REFER_LIST, refer)) {
            String roomId = intent.getStringExtra(EXTRA_ROOM_ID);
            String hostId = intent.getStringExtra(EXTRA_HOST_ID);
            if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(hostId)) {
                return false;
            } else {
                LiveRTCManager.ins().getRTSClient().requestJoinLiveRoom(roomId, mJoinRoomCallback);
                return true;
            }
        } else if (TextUtils.equals(refer, REFER_RECONNECT)) {
            ReconnectInfo reconnectInfo = GsonUtils.gson().fromJson(
                    intent.getStringExtra(EXTRA_RECONNECT_INFO), ReconnectInfo.class);
            LiveUserInfo userInfo = GsonUtils.gson().fromJson(
                    intent.getStringExtra(EXTRA_USER_INFO), LiveUserInfo.class);
            int status = intent.getIntExtra(EXTRA_INTERACT_STATUS, ROOM_STATUS_LIVE);
            String usersJson = intent.getStringExtra(EXTRA_INTERACT_USERS);
            if (reconnectInfo != null && userInfo != null) {
                mLiveRoomInfo = reconnectInfo.liveRoomInfo;
                mSelfInfo = userInfo;
                mRoomStatus = status;
                setAudienceCount(reconnectInfo.audienceCount);
                mPushUrl = reconnectInfo.streamPushUrl;
                mRTCToken = reconnectInfo.rtcToken;
                LiveRTCManager.ins().turnOnMic(mSelfInfo.isMicOn());
                LiveRTCManager.ins().turnOnCamera(mSelfInfo.isCameraOn());
                if (mSelfInfo.status != USER_STATUS_OTHER) {
                    LiveRTCManager.ins().startCaptureAudio(mSelfInfo.isMicOn());
                    LiveRTCManager.ins().startCaptureVideo(mSelfInfo.isCameraOn());
                    mGuestList.clear();
                    if (!TextUtils.isEmpty(usersJson)) {
                        List<LiveUserInfo> users = GsonUtils.gson().fromJson(usersJson,
                                new TypeToken<List<LiveUserInfo>>() {
                                }.getType());
                        if (status == ROOM_STATUS_CO_HOST && users != null) {
                            for (LiveUserInfo info : users) {
                                if (!TextUtils.equals(info.userId, SolutionDataManager.ins().getUserId())) {
                                    mCoHostInfo = info;
                                    break;
                                }
                            }
                        } else if (status == ROOM_STATUS_GUEST_INTERACT) {
                            updateOnlineGuestList(users);
                        }
                    }
                }
                initByJoinResponse(mLiveRoomInfo, mSelfInfo, mAudienceCount, reconnectInfo.streamPullUrl);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        openLeaveDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SolutionDemoEventManager.unregister(this);

        stopPlayLiveStream();
        if (mVideoPlayer != null) {
            mVideoPlayer.destroy();
        }

        if (mLiveRoomInfo != null && mSelfInfo != null) {
            String roomId = mLiveRoomInfo.roomId;
            LiveRTSClient rtsClient = LiveRTCManager.ins().getRTSClient();
            if (mSelfInfo.role == USER_ROLE_AUDIENCE) {
                if (mRoomStatus == ROOM_STATUS_GUEST_INTERACT) {
                    showToast(getString(R.string.host_disconnected_live));
                }
                if (!isLeaveByKickOut && rtsClient != null) {
                    rtsClient.requestLeaveLiveRoom(roomId, mLeaveLiveCallback);
                }
            } else if (mSelfInfo.role == USER_ROLE_HOST) {
                if (mRoomStatus == ROOM_STATUS_GUEST_INTERACT) {
                    showToast(getString(R.string.disconnected));
                }
                if (!isLeaveByKickOut && rtsClient != null) {
                    rtsClient.requestFinishLive(roomId, mFinishLiveCallback);
                }
            }
        }

        LiveRTCManager.ins().leaveRTSRoom();
        LiveRTCManager.ins().leaveRoom();
        LiveRTCManager.ins().turnOnCamera(false);
        LiveRTCManager.ins().turnOnMic(false);
        LiveRTCManager.ins().removeAllUserRenderView();
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

    public void initByJoinResponse(LiveRoomInfo liveRoomInfo, LiveUserInfo liveUserInfo,
                                   int audienceCount, Map<String, String> pullStreamMap) {
        mLiveRoomInfo = liveRoomInfo;
        mSelfInfo = liveUserInfo;
        mAudienceCount = audienceCount;
        mPullStreamMap.clear();
        if (pullStreamMap != null) {
            mPullStreamMap.putAll(pullStreamMap);
        }

        LiveRTCManager.ins().joinRTSRoom(mLiveRoomInfo.roomId, mSelfInfo.userId, mRTSToken);
        if (mSelfInfo.role != USER_ROLE_HOST) {
            LiveRTCManager.ins().startCaptureAudio(false);
            LiveRTCManager.ins().startCaptureVideo(false);
        }

        //UI
        mViewBinding.hostAvatar.setUserName(liveRoomInfo == null ? null : liveRoomInfo.anchorUserName);
        mViewBinding.mainControls.setRole(liveUserInfo.role, liveUserInfo.linkMicStatus);
        setAudienceCount(audienceCount);

        if (liveUserInfo.role == USER_ROLE_HOST) {
            LiveRTCManager.ins().setSingleLiveInfo(mRTCRoomId, mSelfInfo.userId, mPushUrl);
            LiveRTCManager.ins().joinRoom(mRTCRoomId, mSelfInfo.userId, mRTCToken);
            if (mRoomStatus == ROOM_STATUS_CO_HOST) {
                updateOnlineGuestList(null);
                mViewBinding.mainContainer.setLiveUserInfo(mSelfInfo, mCoHostInfo, mSelfInfo);
            } else if (mRoomStatus == ROOM_STATUS_GUEST_INTERACT) {
                mViewBinding.mainContainer.setLiveUserInfo(mSelfInfo, null, mSelfInfo);
                updateOnlineGuestList();
            } else {
                mViewBinding.mainContainer.setLiveUserInfo(liveUserInfo, null, mSelfInfo);
                updateOnlineGuestList(null);
            }

            LiveRTCManager.ins().getRTSClient().updateResolution(
                    mLiveRoomInfo.roomId,
                    LiveRTCManager.ins().getWidth(LiveDataManager.USER_ROLE_HOST),
                    LiveRTCManager.ins().getHeight(LiveDataManager.USER_ROLE_HOST),
                    null);
        } else {
            mViewBinding.mainContainer.setLiveUserInfo(mHostInfo, null, mSelfInfo);
            updatePlayerStatus();
            updateOnlineGuestList(null);
            mViewBinding.mainControls.setAddGuestBtnStatus(STATUS_NORMAL);
        }

        LiveRTCManager.ins().setResolution(liveUserInfo.role,
                LiveRTCManager.ins().getWidth(liveUserInfo.role),
                LiveRTCManager.ins().getHeight(liveUserInfo.role));
    }

    private void setAudienceCount(int count) {
        mAudienceCount = count;
        mViewBinding.audienceNumTv.setText(String.format(Locale.US, "%d", mAudienceCount));
    }

    private void openFinishCoHostDialog() {
        final SolutionCommonDialog dialog = new SolutionCommonDialog(this);
        dialog.setCancelable(true);
        dialog.setPositiveBtnText(R.string.disconnect);
        dialog.setMessage(getString(R.string.quit_title));
        dialog.setNegativeListener((v) -> dialog.dismiss());
        dialog.setPositiveListener((v) -> {
            dialog.dismiss();
            LiveRTCManager.ins().getRTSClient().finishHostLink(mLinkId, mLiveRoomInfo.roomId, mInteractResponse);
        });
        dialog.show();
    }

    private void openInviteCoHostDialog() {
        if (mRoomStatus == ROOM_STATUS_GUEST_INTERACT) {
            showToast(getString(R.string.audience_connection_error));
            return;
        }
        LiveCoHostDialog liveCoHostDialog = new LiveCoHostDialog(this, new LiveCoHostDialog.CoHostCallback() {
            @Override
            public void onClick(LiveUserInfo info) {
                final IRequestCallback<LiveInviteResponse> callback = new IRequestCallback<LiveInviteResponse>() {
                    @Override
                    public void onSuccess(LiveInviteResponse data) {
                        showToast(getString(R.string.xxx_waiting_response, info.userName));
                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        if (errorCode == 622) {
                            showToast(getString(R.string.xxx_waiting_response, info.userName));
                        } else {
                            showToast(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
                        }
                    }
                };
                LiveRTCManager.ins().getRTSClient().inviteHostByHost(mLiveRoomInfo.roomId, mLiveRoomInfo.anchorUserId,
                        info.roomId, info.userId, "", callback);
            }
        });
        liveCoHostDialog.show();
    }
    /**
     * Audience mic operation button click
     */
    private void onGuestClick() {
        if (mSelfInfo == null) {
            return;
        }
        if (mSelfInfo.role == USER_ROLE_AUDIENCE) {
            if (mSelfInfo.linkMicStatus == LiveDataManager.LINK_MIC_STATUS_OTHER) {
                boolean isHostInCoHost = mHostInfo != null
                        && mHostInfo.linkMicStatus == LINK_MIC_STATUS_HOST_INTERACTING;
                if (mRoomStatus == ROOM_STATUS_CO_HOST || isHostInCoHost) {
                    SolutionToast.show(R.string.host_liveing);
                    return;
                }
                RequestInteractDialog dialog = new RequestInteractDialog(
                        LiveRoomMainActivity.this, mLastApplyTs,
                        () -> {
                            LiveRTCManager.ins().getRTSClient().requestLinkByAudience(mLiveRoomInfo.roomId, mGuestApplyResponse);
                            mViewBinding.mainControls.setAddGuestBtnStatus(LiveRoomControlsView.STATUS_NORMAL);
                        });
                dialog.show();
            } else if (mSelfInfo.linkMicStatus == LINK_MIC_STATUS_AUDIENCE_INTERACTING) {
                final SolutionCommonDialog dialog = new SolutionCommonDialog(this);
                dialog.setPositiveBtnText(R.string.disconnect_live);
                dialog.setCancelable(true);
                dialog.setMessage(getString(R.string.disconnect_host_live));
                dialog.setNegativeListener((v) -> dialog.dismiss());
                dialog.setPositiveListener((v) -> {
                    dialog.dismiss();
                    LiveRTCManager.ins().getRTSClient().finishAudienceLinkByAudience("",
                            mLiveRoomInfo.roomId, mInteractResponse);
                    mSelfInfo.linkMicStatus = LiveDataManager.LINK_MIC_STATUS_OTHER;
                    mViewBinding.mainContainer.setLiveUserInfo(mHostInfo, null, mSelfInfo);
                });
                dialog.show();
            }
        } else if (mSelfInfo.role == USER_ROLE_HOST) {
            if (mRoomStatus == ROOM_STATUS_CO_HOST) {
                showToast(getString(R.string.host_connection_error));
                return;
            }
            final InviteGuestsDialog dialog = new InviteGuestsDialog(this, mLiveRoomInfo.roomId,
                    this::inviteAudienceByHost);
            dialog.show();
        }
    }
    /**
     * As an anchor, invite audiences to link
     * @param info audience user information
     */
    private void inviteAudienceByHost(LiveUserInfo info) {
        if (info == null) {
            return;
        }
        final IRequestCallback<LiveInviteResponse> callback = new IRequestCallback<LiveInviteResponse>() {
            @Override
            public void onSuccess(LiveInviteResponse data) {
                showToast(getString(R.string.xxx_waiting_response, info.userName));
                SolutionDemoEventManager.post(new InviteAudienceEvent(info.userId,
                        LiveDataManager.INVITE_REPLY_WAITING));
            }

            @Override
            public void onError(int errorCode, String message) {
                if (errorCode == 622) {
                    showToast(getString(R.string.xxx_waiting_response, info.userName));
                } else {
                    showToast(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
                    SolutionDemoEventManager.post(new InviteAudienceEvent(info.userId,
                            LiveDataManager.INVITE_REPLY_TIMEOUT));
                }
            }
        };
        LiveRTCManager.ins().getRTSClient().inviteAudienceByHost(mLiveRoomInfo.roomId,
                mLiveRoomInfo.anchorUserId, mLiveRoomInfo.roomId, info.userId, "", callback);
    }

    private void openVideoEffectDialog() {
        LiveRTCManager.ins().openEffectDialog(this);
    }

    private void openSettingDialog() {
        if (mLiveRoomInfo == null || mSelfInfo == null) {
            return;
        }
        if (mSelfInfo.role == USER_ROLE_HOST) {
            LiveSettingDialog settingDialog = new LiveSettingDialog(this, true, mLiveRoomInfo.roomId);
            settingDialog.show();
        } else {
            GuestSettingDialog settingDialog = new GuestSettingDialog(
                    this, mSelfInfo.status, mLiveRoomInfo.roomId);
            settingDialog.show();
        }
    }

    private void openLeaveDialog() {
        if (mLiveRoomInfo == null || mSelfInfo == null) {
            finish();
            return;
        }
        if (mSelfInfo.role == USER_ROLE_HOST) {
            final SolutionCommonDialog dialog = new SolutionCommonDialog(this);
            dialog.setCancelable(true);
            dialog.setMessage(getString(R.string.end_live_alert));
            dialog.setPositiveBtnText(R.string.end_live_title);
            dialog.setNegativeListener((v) -> dialog.dismiss());
            dialog.setPositiveListener((v) -> {
                dialog.cancel();
                finish();
            });
            dialog.show();
        } else {
            finish();
        }
    }

    private void showToast(String message) {
        SolutionToast.show(message);
    }

    private void addChatMessage(CharSequence message) {
        mLiveChatAdapter.addChatMsg(message);
        mViewBinding.messageRv.post(() -> mViewBinding.messageRv.smoothScrollToPosition(mLiveChatAdapter.getItemCount()));
    }
    /**
     * Update player state
     * If you are the host, or you are a guest who is connecting with the host,
     * the control of connecting with the host will be displayed, and the streaming will stop
     * Otherwise, if a single anchor is live streaming and the camera is turned off,
     * the mic-connected control will be displayed and streaming will start
     */
    private void updatePlayerStatus() {
        if (mSelfInfo.role == USER_ROLE_HOST ||
                mSelfInfo.linkMicStatus == LINK_MIC_STATUS_AUDIENCE_INTERACTING) {
            mViewBinding.mainContainer.setVisibility(VISIBLE);
            stopPlayLiveStream();
        } else {
            boolean isSingleCameraOff = !mHostInfo.isCameraOn()
                    && (mHostInfo.linkMicStatus != LINK_MIC_STATUS_HOST_INTERACTING);
            mViewBinding.mainContainer.setVisibility(
                    isSingleCameraOff
                            ? VISIBLE : GONE);
            playLiveStream();
            mViewBinding.liveStreamContainer.setVisibility(isSingleCameraOff ? INVISIBLE : VISIBLE);
        }
    }
    /**
     * Play live rtmp live stream
     */
    private void playLiveStream() {
        if (mPullStreamMap.isEmpty()) {
            Log.d(TAG, "playLiveStream: pullStream map is empty");
            return;
        }

        String url = mPullStreamMap.get(LiveRTCManager.ins().getPlayLiveStreamResolution());
        if (mVideoPlayer != null) {
            mVideoPlayer.setPlayerUrl(url, mViewBinding.liveStreamContainer);
            mVideoPlayer.play();
            mVideoPlayer.updatePlayScaleModel(IVideoPlayer.MODE_ASPECT_FILL);
        }
    }
    /**
     * Set layout parameters in hidden mode, while ensuring the canvas is 9:16
     * @param params          layout parameter object
     * @param containerWidth  the width of the container
     * @param containerHeight the height of the container
     */
    private void calculatePlayerSize(FrameLayout.LayoutParams params,
                                     int containerWidth, int containerHeight) {
        float expectHeight = ((float) containerWidth) * 16 / 9;
        if (expectHeight > containerHeight) {
            params.width = containerWidth;
            params.height = (int) expectHeight;

            float yDelta = (containerHeight - expectHeight) / 2;
            params.topMargin = (int) yDelta;
        } else {
            float expectWidth = ((float) containerHeight) * 9 / 16;
            params.height = containerHeight;
            params.width = (int) expectWidth;

            params.leftMargin = (containerWidth - (int) expectWidth) / 2;
        }
    }

    private void stopPlayLiveStream() {
        mViewBinding.liveStreamContainer.removeAllViews();

        if (mVideoPlayer != null) {
            mVideoPlayer.stop();
        }
    }

    private void updateOnlineGuestList() {
        mViewBinding.audienceListRv.setUserList(mLiveRoomInfo.roomId, mLiveRoomInfo.anchorUserId,
                mSelfInfo.role == USER_ROLE_HOST, mGuestList);
    }
    /**
     * This method will use the list in Parameters to refresh the status information of the host,
     * Lianmai anchor, and yourself
     *
     * @param userList is used to refresh the data of users in the room
     */
    private void updateOnlineGuestList(List<LiveUserInfo> userList) {
        mGuestList.clear();
        // If you are not an anchor, reset your own state first,
        // because the subsequent traversal may not be executed
        if (mSelfInfo.role != USER_ROLE_HOST) {
            mSelfInfo.status = USER_STATUS_OTHER;
        }

        if (userList != null) {
            for (LiveUserInfo userInfo : userList) {
                if (!TextUtils.equals(userInfo.userId, mLiveRoomInfo.anchorUserId)) {
                    if (mRoomStatus == ROOM_STATUS_CO_HOST) {
                        mCoHostInfo = userInfo;
                    } else {
                        mGuestList.add(userInfo);
                    }
                } else {
                    mHostInfo = userInfo;
                }
                if (TextUtils.equals(userInfo.userId, mSelfInfo.userId)) {
                    mSelfInfo = userInfo.getDeepCopy();
                    if (mSelfInfo.role == USER_ROLE_HOST && mRoomStatus == ROOM_STATUS_CO_HOST) {
                        mSelfInfo.status = USER_STATUS_CO_HOSTING;
                        mSelfInfo.linkMicStatus = LINK_MIC_STATUS_HOST_INTERACTING;
                    } else {
                        mSelfInfo.status = USER_STATUS_AUDIENCE_INTERACTING;
                        mSelfInfo.linkMicStatus = LINK_MIC_STATUS_AUDIENCE_INTERACTING;
                    }
                }
            }
        }
        mViewBinding.audienceListRv.setUserList(mLiveRoomInfo.roomId, mLiveRoomInfo.anchorUserId,
                mSelfInfo.role == USER_ROLE_HOST, mGuestList);
    }

    private void showTopTip() {
        mViewBinding.mainDisconnectTip.setVisibility(View.VISIBLE);
    }

    private void hideTopTip() {
        mViewBinding.mainDisconnectTip.setVisibility(View.GONE);
    }

    private void togglePkLabel4Audience(boolean show) {
        if (show) {
            mViewBinding.livePkLabelTv.setVisibility(VISIBLE);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mViewBinding.livePkLabelTv.getLayoutParams();
            /***标签距离顶部距离为屏幕高度除以4，是因为在{@link LiveRTCManager#updateLiveTranscodingWithHost} 中自己主播和对端主播布局参数中
             视频流对应区域左上角的横坐标相对整体画面的归一化比例都为0.25(xxRegion.position(0, 0.25))**/
            layoutParams.topMargin = WindowUtils.getScreenHeight(getApplication()) / 4;
            mViewBinding.livePkLabelTv.setLayoutParams(layoutParams);
        } else {
            mViewBinding.livePkLabelTv.setVisibility(GONE);
        }
    }

    private List<String> sortUserList(List<LiveUserInfo> userInfos) {
        List<String> userIdList = new ArrayList<>();
        if (userInfos != null) {
            for (LiveUserInfo info : userInfos) {
                if (info.role == LiveDataManager.USER_ROLE_HOST &&
                        TextUtils.equals(SolutionDataManager.ins().getUserId(), info.userId)) {
                    userIdList.add(0, info.userId);
                } else {
                    userIdList.add(info.userId);
                }
            }
        }
        return userIdList;
    }

    public static void startFromList(Activity activity, String roomId, String hostId, int requestCode) {
        Intent intent = new Intent(activity, LiveRoomMainActivity.class);
        intent.putExtra(EXTRA_REFER, REFER_LIST);
        intent.putExtra(EXTRA_ROOM_ID, roomId);
        intent.putExtra(EXTRA_HOST_ID, hostId);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startFromCreate(Activity activity, LiveRoomInfo roomInfo, LiveUserInfo userInfo,
                                       String pushUrl, String rtmToken, String rtcToken, String rtcRoomId) {
        Intent intent = new Intent(activity, LiveRoomMainActivity.class);
        intent.putExtra(EXTRA_REFER, REFER_CREATE);
        intent.putExtra(EXTRA_ROOM_INFO, GsonUtils.gson().toJson(roomInfo));
        intent.putExtra(EXTRA_USER_INFO, GsonUtils.gson().toJson(userInfo));
        intent.putExtra(EXTRA_PUSH_URL, pushUrl);
        intent.putExtra(EXTRA_RTM_TOKEN, rtmToken);
        intent.putExtra(EXTRA_RTC_TOKEN, rtcToken);
        intent.putExtra(EXTRA_RTC_ROOM_ID, rtcRoomId);
        activity.startActivity(intent);
    }

    public static void startFromReconnect(Activity activity, ReconnectInfo reconnectInfo,
                                          LiveUserInfo selfInfo, int interactStatus,
                                          List<LiveUserInfo> interactUsers) {
        Intent intent = new Intent(activity, LiveRoomMainActivity.class);
        intent.putExtra(EXTRA_REFER, REFER_RECONNECT);
        intent.putExtra(EXTRA_USER_INFO, GsonUtils.gson().toJson(selfInfo));
        intent.putExtra(EXTRA_RECONNECT_INFO, GsonUtils.gson().toJson(reconnectInfo));
        intent.putExtra(EXTRA_INTERACT_STATUS, interactStatus);
        intent.putExtra(EXTRA_INTERACT_USERS, GsonUtils.gson().toJson(interactUsers));
        activity.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveRoomUserEvent(LiveRoomUserEvent event) {
        addChatMessage(event.audienceUserName + " " + getString(event.isJoin ? R.string.joined : R.string.left));

        setAudienceCount(event.audienceCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveFinishEvent(LiveFinishEvent event) {
        if (TextUtils.equals(event.roomId, mLiveRoomInfo.roomId)) {
            if (event.type == LiveDataManager.LIVE_FINISH_TYPE_TIMEOUT) {
                if (mSelfInfo.role == USER_ROLE_HOST) {
                    SolutionToast.show(R.string.minutes_error_message);
                } else {
                    SolutionToast.show(R.string.live_ended);
                }
            } else if (event.type == LiveDataManager.LIVE_FINISH_TYPE_IRREGULARITY) {
                SolutionToast.show(R.string.closed_terms_service);
            } else if (event.type == LiveDataManager.LIVE_FINISH_TYPE_NORMAL && mSelfInfo.role != USER_ROLE_HOST) {
                SolutionToast.show(R.string.live_ended);
            }
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceMediaUpdateEvent(AudienceMediaUpdateEvent event) {
        if (event.camera == LiveDataManager.MEDIA_STATUS_OFF) {
            showToast(getString(R.string.off_camera_title));
            LiveRTCManager.ins().startCaptureVideo(false);
        }
        if (event.mic == LiveDataManager.MEDIA_STATUS_OFF) {
            showToast(getString(R.string.off_mic_title));
            LiveRTCManager.ins().startCaptureAudio(false);
        }
        int mic = LiveRTCManager.ins().isMicOn() ? MEDIA_STATUS_ON : MEDIA_STATUS_OFF;
        int camera = LiveRTCManager.ins().isCameraOn() ? MEDIA_STATUS_ON : MEDIA_STATUS_OFF;
        LiveRTCManager.ins().getRTSClient().updateMediaStatus(event.guestRoomId, mic, camera,
                new IRequestCallback<LiveResponse>() {
                    @Override
                    public void onSuccess(LiveResponse data) {

                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        showToast(ErrorTool.getErrorMessageByErrorCode(errorCode, message));
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMediaChangedEvent(MediaChangedEvent event) {
        // ui, mic host interface changes, audience list status changes
        if (mSelfInfo.role == USER_ROLE_HOST) {
            if (mRoomStatus == ROOM_STATUS_CO_HOST) {
                if (mCoHostInfo != null && TextUtils.equals(event.userId, mCoHostInfo.userId)) {
                    mCoHostInfo.micStatus = event.mic;
                    mCoHostInfo.cameraStatus = event.camera;
                    mViewBinding.mainContainer.setLiveUserInfo(mSelfInfo, mCoHostInfo, mSelfInfo);
                } else if (TextUtils.equals(event.userId, mSelfInfo.userId)) {
                    mSelfInfo.micStatus = event.mic;
                    mSelfInfo.cameraStatus = event.camera;
                    LiveRTCManager.ins().startCaptureAudio(event.mic == MEDIA_STATUS_ON);
                    LiveRTCManager.ins().startCaptureVideo(event.camera == MEDIA_STATUS_ON);
                    mViewBinding.mainContainer.setLiveUserInfo(mSelfInfo, mCoHostInfo, mSelfInfo);
                }
            } else {
                if (TextUtils.equals(event.userId, mSelfInfo.userId)) {
                    mSelfInfo.micStatus = event.mic;
                    mSelfInfo.cameraStatus = event.camera;
                    LiveRTCManager.ins().startCaptureAudio(event.mic == MEDIA_STATUS_ON);
                    LiveRTCManager.ins().startCaptureVideo(event.camera == MEDIA_STATUS_ON);
                    mViewBinding.mainContainer.setLiveUserInfo(mSelfInfo, null, mSelfInfo);
                }
                if (mRoomStatus == ROOM_STATUS_GUEST_INTERACT) {
                    for (LiveUserInfo userInfo : mGuestList) {
                        if (TextUtils.equals(event.userId, userInfo.userId)) {
                            userInfo.micStatus = event.mic;
                            userInfo.cameraStatus = event.camera;
                            break;
                        }
                    }
                    updateOnlineGuestList();
                }
            }
        } else {
            // Non-anchor Lianmai situation
            for (LiveUserInfo userInfo : mGuestList) {
                if (TextUtils.equals(userInfo.userId, mSelfInfo.userId)) {
                    break;
                }
            }
            if (mHostInfo != null && TextUtils.equals(event.userId, mHostInfo.userId)) {
                mHostInfo.micStatus = event.mic;
                mHostInfo.cameraStatus = event.camera;
                mViewBinding.mainContainer.setLiveUserInfo(mHostInfo, null, mSelfInfo);
                updatePlayerStatus();
            }
            if (mRoomStatus == ROOM_STATUS_GUEST_INTERACT) {
                for (LiveUserInfo userInfo : mGuestList) {
                    if (TextUtils.equals(event.userId, userInfo.userId)) {
                        userInfo.micStatus = event.mic;
                        userInfo.cameraStatus = event.camera;
                        break;
                    }
                }
                updateOnlineGuestList();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserTemporaryLeaveEvent(UserTemporaryLeaveEvent event) {
        if (TextUtils.equals(event.userId, mLiveRoomInfo.anchorUserId) || mCoHostInfo != null) {
            showToast(getString(R.string.host_back_soon));
        } else {
            showToast(getString(R.string.guest_back_soon));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveReconnectEvent(SDKReconnectToRoomEvent event) {
        LiveRTCManager.ins().getRTSClient().requestLiveReconnect(
                mLiveRoomInfo.roomId,
                mLiveReconnectCallback);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenExpiredEvent(AppTokenExpiredEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePullStreamEvent(LocalUpdatePullStreamEvent event) {
        updatePlayerStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveKickUserEvent(LiveKickUserEvent event) {
        showToast(getString(R.string.same_logged_in));
        isLeaveByKickOut = true;
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkQualityEvent(SDKNetworkQualityEvent event) {
        boolean isGood = event.quality == NETWORK_QUALITY_EXCELLENT
                || event.quality == NETWORK_QUALITY_GOOD;
        mViewBinding.mainContainer.updateNetStatus(event.userId, isGood);
        mViewBinding.audienceListRv.updateNetStatus(event.userId, isGood);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkMicStatusEvent(LinkMicStatusEvent event) {
        mRoomStatus = event.linkMicStatus;
        mHostInfo.linkMicStatus = event.linkMicStatus;
        updatePlayerStatus();
    }
    // Audience Lianmai Audience end Invite audience to mic notification
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkInviteEvent(AudienceLinkInviteEvent event) {
        final InviteResultDialog dialog = new InviteResultDialog(this);
        dialog.setCancelable(true);
        dialog.setMessage(getString(R.string.live_together_invited));
        dialog.setNegativeListener((v) -> {
            LiveRTCManager.ins().getRTSClient().replyHostInviterByAudience(event.linkerId, mLiveRoomInfo.roomId,
                    LiveDataManager.LIVE_PERMIT_TYPE_REJECT, new IRequestCallback<LiveReplyResponse>() {
                        @Override
                        public void onSuccess(LiveReplyResponse data) {

                        }

                        @Override
                        public void onError(int errorCode, String message) {

                        }
                    });
            dialog.dismiss();
        });
        dialog.setPositiveListener((v) -> {
            LiveRTCManager.ins().getRTSClient().replyHostInviterByAudience(event.linkerId, mLiveRoomInfo.roomId,
                    LiveDataManager.LIVE_PERMIT_TYPE_ACCEPT, mReplyInviteCallbackByAudience);
            dialog.dismiss();
        });
        dialog.show();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkApplyEvent(AudienceLinkApplyEvent event) {
        final InviteResultDialog dialog = new InviteResultDialog(this);
        dialog.setCancelable(true);
        dialog.setMessage(getString(R.string.xxx_join_live, event.applicant.userName));
        dialog.setNegativeListener((v) -> {
            LiveRTCManager.ins().getRTSClient().replyAudienceRequestByHost(event.linkerId, mLiveRoomInfo.roomId,
                    mLiveRoomInfo.anchorUserId, mLiveRoomInfo.roomId, event.applicant.userId,
                    LiveDataManager.LIVE_PERMIT_TYPE_REJECT, new IRequestCallback<LiveAnchorPermitAudienceResponse>() {
                        @Override
                        public void onSuccess(LiveAnchorPermitAudienceResponse data) {

                        }

                        @Override
                        public void onError(int errorCode, String message) {

                        }
                    });
            dialog.dismiss();
        });
        dialog.setPositiveListener((v) -> {
            LiveRTCManager.ins().getRTSClient().replyAudienceRequestByHost(event.linkerId, mLiveRoomInfo.roomId,
                    mLiveRoomInfo.anchorUserId, mLiveRoomInfo.roomId, event.applicant.userId,
                    LiveDataManager.LIVE_PERMIT_TYPE_ACCEPT, mReplyInviteCallbackByHost);
            dialog.dismiss();
        });
        dialog.show();
    }
    // Audience Lianmai anchor side viewer reply anchor invitation result
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkReplyEvent(AudienceLinkReplyEvent event) {
        if (event.replyType == INVITE_REPLY_REJECT) {
            showToast(getString(R.string.xxx_declines_invite, event.userInfo.userName));
        } else if (event.replyType == INVITE_REPLY_ACCEPT) {
            updateOnlineGuestList(event.rtcUserList);
            if (mGuestList.size() == 1) {
                showToast(getString(R.string.click_seat_title));
                LiveRTCManager.ins().joinRoom(event.rtcRoomId, mSelfInfo.userId, event.rtcToken);
                LiveRTCManager.ins().updateLiveTranscodingWithAudience(event.rtcRoomId,
                        mSelfInfo.userId, mPushUrl, sortUserList(event.rtcUserList));
            } else {
                List<LiveUserInfo> audienceUserList = new ArrayList<>(mGuestList);
                if (mHostInfo != null) {
                    audienceUserList.add(mHostInfo.getDeepCopy());
                }
                LiveRTCManager.ins().updateLiveTranscodingWithAudience(event.rtcRoomId,
                        mSelfInfo.userId, mPushUrl, sortUserList(audienceUserList));
            }
        }
    }
    // Audience Lianmai, the audience terminal, the anchor confirms the application result of the audience
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkPermitEvent(AudienceLinkPermitEvent event) {
        mLastApplyTs = 0;
        if (event.permitType == LIVE_PERMIT_TYPE_REJECT) {
            showToast(getString(R.string.request_declined));
            mViewBinding.mainControls.setAddGuestBtnStatus(STATUS_NORMAL);
            SolutionDemoEventManager.post(new InviteAudienceEvent(
                    SolutionDataManager.ins().getUserId(),
                    LiveDataManager.INVITE_REPLY_REJECT));
        } else if (event.permitType == LIVE_PERMIT_TYPE_ACCEPT) {
            showToast(getString(R.string.will_start_live));

            SolutionDemoEventManager.post(new InviteAudienceEvent(
                    SolutionDataManager.ins().getUserId(),
                    LiveDataManager.INVITE_REPLY_ACCEPT));

            updateOnlineGuestList(event.rtcUserList);
            LiveRTCManager.ins().joinRoom(event.rtcRoomId, mSelfInfo.userId, event.rtcToken);
            LiveRTCManager.ins().startCaptureVideo(mSelfInfo.isCameraOn());
            LiveRTCManager.ins().startCaptureAudio(mSelfInfo.isMicOn());

            mRoomStatus = ROOM_STATUS_GUEST_INTERACT;
            mSelfInfo.status = USER_STATUS_AUDIENCE_INTERACTING;
            mSelfInfo.linkMicStatus = LINK_MIC_STATUS_AUDIENCE_INTERACTING;
            updatePlayerStatus();
            mViewBinding.mainControls.setAddGuestBtnStatus(STATUS_DISABLE);
            mViewBinding.mainControls.setRole(mSelfInfo.role, LINK_MIC_STATUS_AUDIENCE_INTERACTING);
            mViewBinding.mainContainer.setLiveUserInfo(mHostInfo, null, mSelfInfo);
        }
    }
    // Anchor Lianmai, the anchor side, received the host Lianmai invitation
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnchorLinkInviteEvent(AnchorLinkInviteEvent event) {
        final InviteResultDialog dialog = new InviteResultDialog(this);
        dialog.setCancelable(true);
        dialog.setPositiveBtnText(R.string.accept);
        dialog.setMessage(getString(R.string.xxxinvites_live, event.userInfo.userName));
        dialog.setNegativeListener((v) -> {
            LiveRTCManager.ins().getRTSClient().replyHostInviteeByHost(event.linkerId, event.userInfo.roomId,
                    event.userInfo.userId, mLiveRoomInfo.roomId, mLiveRoomInfo.anchorUserId,
                    LiveDataManager.LIVE_PERMIT_TYPE_REJECT, new IRequestCallback<LiveInviteResponse>() {
                        @Override
                        public void onSuccess(LiveInviteResponse data) {

                        }

                        @Override
                        public void onError(int errorCode, String message) {

                        }
                    });
            dialog.dismiss();
        });
        dialog.setPositiveListener((v) -> {
            mLinkId = event.linkerId;
            LiveRTCManager.ins().getRTSClient().replyHostInviteeByHost(event.linkerId, event.userInfo.roomId,
                    event.userInfo.userId, mLiveRoomInfo.roomId, mLiveRoomInfo.anchorUserId,
                    LiveDataManager.LIVE_PERMIT_TYPE_ACCEPT, mAnchorReplyInviteCallback);
            dialog.dismiss();
        });
        dialog.show();
    }
    // Anchor Lianmai, the anchor side, received the result of the host Lianmai invitation
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnchorLinkReplyEvent(AnchorLinkReplyEvent event) {
        if (!TextUtils.equals(event.userInfo.userId, mSelfInfo.userId) && event.replyType == INVITE_REPLY_REJECT) {
            mRoomStatus = ROOM_STATUS_LIVE;
            showToast(getString(R.string.not_available_live));
            mViewBinding.mainControls.setAddGuestBtnStatus(STATUS_NORMAL);
        } else if (event.replyType == INVITE_REPLY_ACCEPT) {
            mRoomStatus = ROOM_STATUS_CO_HOST;
            mLinkId = event.linkerId;
            for (LiveUserInfo info : event.rtcUserList) {
                if (!TextUtils.equals(info.userId, SolutionDataManager.ins().getUserId())) {
                    mCoHostInfo = info;
                    break;
                }
            }
            mViewBinding.mainControls.setCoHostBtnStatus(STATUS_DISABLE);
            mViewBinding.mainContainer.setLiveUserInfo(mSelfInfo, mCoHostInfo, mSelfInfo);
            setCoHostVideoConfig(mCoHostInfo);
            LiveRTCManager.ins().startForwardStreamToRooms(event.rtcRoomId, mCoHostInfo.userId,
                    event.rtcToken, mRTCRoomId, mSelfInfo.userId, mPushUrl);
        }
    }
    /**
     * Set the resolution information of the Lianmai anchor according to the extra
     * field of the user information of the business server
     * @param userInfo anchor information
     */
    private void setCoHostVideoConfig(LiveUserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        int width = 0;
        int height = 0;
        try {
            JSONObject ext = new JSONObject(mCoHostInfo.extra);
            width = ext.getInt("width");
            height = ext.getInt("height");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LiveRTCManager.ins().setCoHostVideoConfig(width, height);
    }
    // Audience mic, audience join or leave
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkStatusEvent(AudienceLinkStatusEvent event) {
        mLinkId = event.linkerId;
        if (event.isJoin) {
            mRoomStatus = ROOM_STATUS_GUEST_INTERACT;
            updateOnlineGuestList(event.userList);
            if (mSelfInfo.role == USER_ROLE_HOST) {
                if (mGuestList.size() == 1) {
                    showToast(getString(R.string.click_seat_title));
                    LiveRTCManager.ins().updateLiveTranscodingWithAudience(event.rtcRoomId, mSelfInfo.userId,
                            mPushUrl, sortUserList(event.userList));

                } else {
                    List<LiveUserInfo> audienceUserList = new ArrayList<>(mGuestList);
                    if (mHostInfo != null) {
                        audienceUserList.add(mHostInfo.getDeepCopy());
                    }
                    LiveRTCManager.ins().updateLiveTranscodingWithAudience(event.rtcRoomId, mSelfInfo.userId,
                            mPushUrl, sortUserList(audienceUserList));

                }
                for (LiveUserInfo info : mGuestList) {
                    SolutionDemoEventManager.post(new InviteAudienceEvent(info.userId, LiveDataManager.INVITE_REPLY_ACCEPT));
                }
            }
        } else {
            if (mSelfInfo.role == USER_ROLE_HOST) {
                String userName = "";
                for (LiveUserInfo userInfo : mGuestList) {
                    if (TextUtils.equals(userInfo.userId, event.userId)) {
                        userName = userInfo.userName;
                        break;
                    }
                }
                showToast(getString(R.string.xxxdisconnected_live, userName));
                updateOnlineGuestList(event.userList);
                List<LiveUserInfo> audienceUserList = new ArrayList<>(mGuestList);
                if (mHostInfo != null) {
                    audienceUserList.add(mHostInfo.getDeepCopy());
                }
                LiveRTCManager.ins().updateLiveTranscodingWithAudience(event.rtcRoomId,
                        mSelfInfo.userId, mPushUrl, sortUserList(audienceUserList));

            } else {
                if (!TextUtils.equals(event.userId, mSelfInfo.userId)) {
                    updateOnlineGuestList(event.userList);
                } else {
                    updateOnlineGuestList(null);
                    mViewBinding.mainControls.setRole(mSelfInfo.role, LiveDataManager.LINK_MIC_STATUS_OTHER);
                    mViewBinding.mainControls.setAddGuestBtnStatus(STATUS_NORMAL);
                    mSelfInfo.linkMicStatus = LiveDataManager.LINK_MIC_STATUS_OTHER;
                    updatePlayerStatus();
                    updateOnlineGuestList(null);
                    LiveRTCManager.ins().leaveRoom();
                }
            }
        }
    }
    // After the local interface request is successful,
    // it needs to send a local broadcast to update the user view
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocalKickUserEvent(LocalKickUserEvent event) {
        for (int i = mGuestList.size() - 1; i >= 0; i--) {
            LiveUserInfo userInfo = mGuestList.get(i);
            if (TextUtils.equals(event.userId, userInfo.userId)) {
                mGuestList.remove(i);
            }
        }
        updateOnlineGuestList();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudienceLinkFinishEvent(AudienceLinkFinishEvent event) {
        mRoomStatus = ROOM_STATUS_LIVE;
        mSelfInfo.linkMicStatus = LiveDataManager.LINK_MIC_STATUS_OTHER;
        mViewBinding.mainControls.setCoHostBtnStatus(STATUS_NORMAL);
        mViewBinding.mainControls.setAddGuestBtnStatus(STATUS_NORMAL);
        mViewBinding.mainControls.setRole(mSelfInfo.role, LiveDataManager.LINK_MIC_STATUS_OTHER);
        updateOnlineGuestList(null);
        if (mSelfInfo.role == USER_ROLE_HOST) {
            LiveRTCManager.ins().updateLiveTranscodingWithAudience(mRTCRoomId,
                    mLiveRoomInfo.anchorUserId, mPushUrl, null);
        } else {
            LiveRTCManager.ins().leaveRoom();
            showToast(getString(R.string.host_disconnected_live));
            mViewBinding.mainControls.setRole(mSelfInfo.role, LiveDataManager.LINK_MIC_STATUS_OTHER);
            mViewBinding.mainControls.setCoHostBtnStatus(STATUS_NORMAL);
            updateOnlineGuestList(null);

            mViewBinding.mainContainer.setLiveUserInfo(mHostInfo, null, mSelfInfo);
            updatePlayerStatus();

            mViewBinding.mainControls.setAddGuestBtnStatus(STATUS_NORMAL);
            mViewBinding.mainControls.setCoHostBtnStatus(STATUS_NORMAL);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnchorLinkFinishEvent(AnchorLinkFinishEvent event) {
        mRoomStatus = ROOM_STATUS_LIVE;
        showToast(getString(R.string.disconnected));
        LiveRTCManager.ins().setCoHostVideoConfig(0, 0);
        LiveRTCManager.ins().updateLiveTranscodingWithHost(false, mPushUrl,
                mRTCRoomId, mSelfInfo.userId, null, null);
        LiveRTCManager.ins().stopLiveTranscodingWithHost();
        updateOnlineGuestList(null);
        mViewBinding.mainContainer.setLiveUserInfo(mSelfInfo, null, mSelfInfo);
        mViewBinding.mainControls.setRole(USER_ROLE_HOST, LiveDataManager.LINK_MIC_STATUS_OTHER);
        mViewBinding.mainControls.setAddGuestBtnStatus(STATUS_NORMAL);
        mViewBinding.mainControls.setCoHostBtnStatus(STATUS_NORMAL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGiftEvent(GiftEvent event) {
        String giftName;
        ImageSpan imageSpan;
        int size = (int) Utils.dp2Px(18);
        if (TextUtils.equals(event.giftType, "flower")) {
            giftName = getString(R.string.flower);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.flower_icon);
            assert drawable != null;
            drawable.setBounds(0, 0, size, size);
            imageSpan = new ImageSpan(drawable);
        } else if (TextUtils.equals(event.giftType, "rocket")) {
            giftName = getString(R.string.rocket);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.rocket_icon);
            assert drawable != null;
            drawable.setBounds(0, 0, size, size);
            imageSpan = new ImageSpan(drawable);
        } else {
            return;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(event.userName);
        ssb.append(" ");
        ssb.append(getString(R.string.sent));
        ssb.append(" ");
        ssb.append(giftName);
        ssb.append(" ");
        ssb.setSpan(imageSpan, ssb.length() - 1, ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        addChatMessage(ssb);
    }
}
