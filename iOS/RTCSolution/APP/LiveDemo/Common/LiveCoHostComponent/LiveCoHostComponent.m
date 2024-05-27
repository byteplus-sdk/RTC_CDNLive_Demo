//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveCoHostComponent.h"
#import "LiveCoHostRoomView.h"
#import "LiveCoHostTopSelectView.h"
#import "LiveRTCManager.h"
#import "LiveSettingVideoConfig.h"
#import "NetworkingTool.h"

@interface LiveCoHostComponent () <LiveCoHostTopSelectViewDelegate, LiveCoHostRaiseHandListsViewDelegate, LiveCoHostAudienceListsViewDelegate>

@property (nonatomic, weak) LiveCoHostRaiseHandListsView *raiseHandListsView;
@property (nonatomic, weak) LiveCoHostAudienceListsView *audienceListsView;
@property (nonatomic, strong) UIButton *maskButton;
@property (nonatomic, copy) void (^dismissBlock)(LiveCoHostDismissState state);
@property (nonatomic, strong) LiveCoHostRoomView *liveCoHostRoomView;
@property (nonatomic, copy) LiveRoomInfoModel *roomInfoModel;
@property (nonatomic, copy) NSArray<LiveUserModel *> *userModelList;
@end

@implementation LiveCoHostComponent

- (instancetype)initWithRoomID:(LiveRoomInfoModel *)roomInfoModel {
    self = [super init];
    if (self) {
        _roomInfoModel = roomInfoModel;
    }
    return self;
}

#pragma mark - Publish List Action

- (void)showInviteList:(void (^)(LiveCoHostDismissState state))dismissBlock {
    self.dismissBlock = dismissBlock;
    UIViewController *rootVC = [DeviceInforTool topViewController];

    [rootVC.view addSubview:self.maskButton];
    [self.maskButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.left.height.equalTo(rootVC.view);
        make.top.equalTo(rootVC.view).offset(SCREEN_HEIGHT);
    }];

    LiveCoHostAudienceListsView *audienceListsView = [[LiveCoHostAudienceListsView alloc] init];
    audienceListsView.delegate = self;
    audienceListsView.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];
    [self.maskButton addSubview:audienceListsView];
    [audienceListsView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(0);
        make.right.mas_equalTo(0);
        make.height.mas_offset(204 + [DeviceInforTool getVirtualHomeHeight]);
        make.bottom.mas_offset(0);
    }];
    _audienceListsView = audienceListsView;

    LiveCoHostRaiseHandListsView *raiseHandListsView = [[LiveCoHostRaiseHandListsView alloc] init];
    raiseHandListsView.delegate = self;
    raiseHandListsView.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];
    [self.maskButton addSubview:raiseHandListsView];
    [raiseHandListsView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(audienceListsView);
    }];
    _raiseHandListsView = raiseHandListsView;

    LiveCoHostTopSelectView *topSelectView = [[LiveCoHostTopSelectView alloc] init];
    topSelectView.delegate = self;
    [self.maskButton addSubview:topSelectView];
    [topSelectView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(rootVC.view);
        make.bottom.equalTo(audienceListsView.mas_top);
        make.height.mas_equalTo(44);
    }];

    // Start animation
    [rootVC.view layoutIfNeeded];
    [self.maskButton.superview setNeedsUpdateConstraints];
    [UIView animateWithDuration:0.25
                     animations:^{
                         [self.maskButton mas_updateConstraints:^(MASConstraintMaker *make) {
                             make.top.equalTo(rootVC.view).offset(0);
                         }];
                         [self.maskButton.superview layoutIfNeeded];
                     }];

    [self loadDataWithRaiseHandLists];
}

- (void)dismissInviteList {
    [self maskButtonAction];
}

- (void)updateInviteList {
    if (self.raiseHandListsView.superview && !self.raiseHandListsView.hidden) {
        [self loadDataWithRaiseHandLists];
    } else if (self.audienceListsView.superview && !self.audienceListsView.hidden) {
        [self loadDataWithAudienceLists];
    } else {
    }
}

#pragma mark - Publish Room Action

- (void)showCoHost:(UIView *)superView
        streamPushUrl:(NSString *)streamPushUrl
        userModelList:(NSArray<LiveUserModel *> *)userModelList
       loginUserModel:(LiveUserModel *)loginUserModel
    otherAnchorRoomId:(NSString *)otherRoomId
     otherAnchorToken:(NSString *)otherToken
        completeBlock:(void (^)(void))completeBlock {
    _isConnect = YES;
    _userModelList = userModelList;

    [[LiveRTCManager shareRtc] pauseRemoteAudioSubscribedStream:NO];
    // Enable span the room retweet stream
    [[LiveRTCManager shareRtc] startForwardStreamToRooms:otherRoomId
                                                   token:otherToken];
    // Receive the audio and video stream retweeted across the room by the host of the other party
    __weak __typeof(self) wself = self;
    [LiveRTCManager shareRtc].onUserPublishStreamBlock = ^(NSString *_Nonnull uid) {
        LiveUserModel *otherUserModel = [wself getOtherUserModel:userModelList];
        if ([uid isEqualToString:otherUserModel.uid]) {
            // Update confluence retweet
            [[LiveRTCManager shareRtc] updateTranscodingLayout:userModelList
                                                     mixStatus:RTCMixStatusCoHost
                                                     rtcRoomId:wself.roomInfoModel.rtcRoomId];

            // UI
            [wself updateCoHostRoomView:superView
                          userModelList:userModelList
                         loginUserModel:loginUserModel];

            if (completeBlock) {
                completeBlock();
            }
        }
    };

    // Enable network monitoring
    [[LiveRTCManager shareRtc] didChangeNetworkQuality:^(LiveNetworkQualityStatus status, NSString *_Nonnull uid) {
        dispatch_queue_async_safe(dispatch_get_main_queue(), (^{
                                      [wself.liveCoHostRoomView updateNetworkQuality:status uid:uid];
                                  }));
    }];
}

- (void)updateCoHostRoomView:(UIView *)superView
               userModelList:(NSArray<LiveUserModel *> *)userModelList
              loginUserModel:(LiveUserModel *)loginUserModel {
    // UI
    [superView addSubview:self.liveCoHostRoomView];
    CGFloat coHostWidth = SCREEN_WIDTH;
    CGFloat coHostHeight = ceilf((SCREEN_WIDTH / 2) * 16 / 9);
    [self.liveCoHostRoomView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(coHostWidth, coHostHeight));
        make.center.equalTo(superView);
    }];
    [self.liveCoHostRoomView updateGuestsMic:loginUserModel.mic
                                         uid:loginUserModel.uid];
    [self.liveCoHostRoomView updateGuestsCamera:loginUserModel.camera
                                            uid:loginUserModel.uid];

    // Update local UI layout
    self.liveCoHostRoomView.userModelList = userModelList;
}

- (void)closeCoHost {
    _isConnect = NO;

    // Stop span the room retweet stream
    [[LiveRTCManager shareRtc] stopForwardStreamToRooms];
    // Update confluence retweet
    LiveUserModel *ownerUserModel = [[LiveUserModel alloc] init];
    ownerUserModel.uid = [LocalUserComponent userModel].uid;

    [[LiveRTCManager shareRtc] updateTranscodingLayout:@[ownerUserModel]
                                             mixStatus:RTCMixStatusSingleLive
                                             rtcRoomId:self.roomInfoModel.rtcRoomId];
    [LiveRTCManager shareRtc].onUserPublishStreamBlock = nil;

    if (self.liveCoHostRoomView) {
        [self.liveCoHostRoomView removeFromSuperview];
        self.liveCoHostRoomView = nil;
    }
}

- (void)updateGuestsMic:(BOOL)mic uid:(NSString *)uid {
    [self.liveCoHostRoomView updateGuestsMic:mic uid:uid];
}

- (void)updateGuestsCamera:(BOOL)camera uid:(NSString *)uid {
    [self.liveCoHostRoomView updateGuestsCamera:camera uid:uid];
}

#pragma mark - Load Data

- (void)loadDataWithRaiseHandLists {
    __weak __typeof(self) wself = self;
    [LiveRTSManager liveGetActiveAnchorList:self.roomInfoModel.roomID
                                      block:^(NSArray<LiveUserModel *> *_Nullable userList, RTSACKModel *_Nonnull model) {
                                          if (model.result) {
                                              wself.raiseHandListsView.dataLists = userList;
                                          }
                                      }];
}

- (void)loadDataWithAudienceLists {
    self.audienceListsView.dataLists = @[];
}

#pragma mark - LiveCoHostTopSelectViewDelegate

- (void)liveCoHostTopSelectView:(LiveCoHostTopSelectView *)liveCoHostTopSelectView clickSwitchItem:(BOOL)isAudience {
    if (isAudience) {
        self.raiseHandListsView.hidden = YES;
        self.audienceListsView.hidden = NO;
        [self loadDataWithAudienceLists];
    } else {
        self.raiseHandListsView.hidden = NO;
        self.audienceListsView.hidden = YES;
        [self loadDataWithRaiseHandLists];
    }
}

#pragma mark - LiveCoHostRaiseHandListsViewDelegate

- (void)liveCoHostRaiseHandListsView:(LiveCoHostRaiseHandListsView *)liveCoHostRaiseHandListsView clickButton:(LiveUserModel *)userModel {
    [self dismissUserListView:LiveCoHostDismissStateInviteIng];
    [[ToastComponent shareToastComponent] showLoading];
    [LiveRTSManager liveAnchorLinkmicInvite:self.roomInfoModel.roomID
                              inviteeRoomID:userModel.roomID
                              inviteeUserID:userModel.uid
                                      extra:@""
                                      block:^(NSString *linkerID,
                                              RTSACKModel *model) {
                                          [[ToastComponent shareToastComponent] dismiss];
                                          if (model.result ||
                                              model.code == RTSStatusCodeUserIsInviting ||
                                              model.code == RTSStatusCodeUserIsNewInviting) {
                                              // Initiate an invitation
                                              NSString *name = userModel.name;
                                              [[ToastComponent shareToastComponent] showWithMessage:[NSString stringWithFormat:LocalizedString(@"%@ waiting_response."), name]];
                                          } else {
                                              [[ToastComponent shareToastComponent] showWithMessage:model.message];
                                          }
                                      }];
}

#pragma mark - LiveCoHostAudienceListsViewDelegate

- (void)liveCoHostAudienceListsView:(LiveCoHostAudienceListsView *)liveCoHostAudienceListsView clickButton:(BaseUserModel *)model {
}

#pragma mark - Private Action

- (void)maskButtonAction {
    [self dismissUserListView:LiveCoHostDismissStateNone];
}

- (void)dismissUserListView:(LiveCoHostDismissState)state {
    [self.maskButton removeAllSubviews];
    [self.maskButton removeFromSuperview];
    self.maskButton = nil;

    if (self.dismissBlock) {
        self.dismissBlock(state);
    }
}

- (LiveUserModel *)getOtherUserModel:(NSArray<LiveUserModel *> *)userModelList {
    LiveUserModel *otherModel = nil;
    for (LiveUserModel *tempUserModel in userModelList) {
        if (![tempUserModel.uid isEqualToString:[LocalUserComponent userModel].uid]) {
            otherModel = tempUserModel;
            break;
        }
    }
    return otherModel;
}

- (LiveUserModel *)getOwnerUserModel:(NSArray<LiveUserModel *> *)userModelList {
    LiveUserModel *model = nil;
    for (LiveUserModel *tempUserModel in userModelList) {
        if ([tempUserModel.uid isEqualToString:[LocalUserComponent userModel].uid]) {
            model = tempUserModel;
            break;
        }
    }
    return model;
}

#pragma mark - Getter

- (LiveCoHostRoomView *)liveCoHostRoomView {
    if (!_liveCoHostRoomView) {
        _liveCoHostRoomView = [[LiveCoHostRoomView alloc] init];
    }
    return _liveCoHostRoomView;
}

- (UIButton *)maskButton {
    if (!_maskButton) {
        _maskButton = [[UIButton alloc] init];
        [_maskButton addTarget:self action:@selector(maskButtonAction) forControlEvents:UIControlEventTouchUpInside];
        [_maskButton setBackgroundColor:[UIColor clearColor]];
    }
    return _maskButton;
}

- (void)dealloc {
    NSLog(@"dealloc %@", NSStringFromClass([self class]));
}

@end
