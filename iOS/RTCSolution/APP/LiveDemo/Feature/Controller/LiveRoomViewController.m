//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveRoomViewController.h"
#import "LiveAddGuestsComponent.h"
#import "LiveCoHostComponent.h"
#import "LiveHostAvatarView.h"
#import "LivePeopleNumView.h"
#import "LivePullStreamComponent.h"
#import "LivePushStreamComponent.h"
#import "LiveRTCManager.h"
#import "LiveRoomBottomView.h"
#import "LiveRoomGuestSettingView.h"
#import "LiveRoomHostSettingView.h"
#import "LiveRoomViewController+SocketControl.h"
#import "NetworkingTool.h"

@interface LiveRoomViewController () <LiveRoomBottomViewDelegate, LiveRTCManagerDelegate>

@property (nonatomic, strong) LivePeopleNumView *peopleNumView;
@property (nonatomic, strong) LiveHostAvatarView *hostAvatarView;
@property (nonatomic, strong) LiveRoomBottomView *bottomView;
@property (nonatomic, strong) BaseIMComponent *imComponent;
@property (nonatomic, strong) LiveCoHostComponent *coHostComponent;
@property (nonatomic, strong) LiveAddGuestsComponent *addGuestsComponent;
@property (nonatomic, strong) LivePullStreamComponent *livePullStreamComponent;
@property (nonatomic, strong) LivePushStreamComponent *livePushStreamComponent;
@property (nonatomic, strong) BytedEffectProtocol *beautyComponent;
@property (nonatomic, strong) UIView *liveView;
@property (nonatomic, strong) UIView *giftView;

@property (nonatomic, strong) LiveRoomInfoModel *liveRoomModel;
@property (nonatomic, strong) LiveUserModel *currentUserModel;
@property (nonatomic, strong) NSString *streamPushUrl;
@property (nonatomic, strong) NSString *linkerID;

@end

@implementation LiveRoomViewController

- (instancetype)initWithRoomModel:(LiveRoomInfoModel *)liveRoomModel
                    streamPushUrl:(NSString *)streamPushUrl {
    self = [super init];
    if (self) {
        [UIApplication sharedApplication].idleTimerDisabled = YES;
        _liveRoomModel = liveRoomModel;
        _streamPushUrl = streamPushUrl;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];
    [self addSocketListener];

    [self joinRoom];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];

    self.navigationController.interactivePopGestureRecognizer.enabled = NO;
}

#pragma mark - LiveRoomBottomViewDelegate

- (void)liveRoomBottomView:(LiveRoomBottomView *_Nonnull)liveRoomBottomView
                itemButton:(LiveRoomItemButton *_Nullable)itemButton
                roleStatus:(BottomRoleStatus)roleStatus {
    if (itemButton.currentState == LiveRoomItemButtonStatePK) {
        if (self.addGuestsComponent.isConnect) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"audience_connection_error")];
        } else {
            [self clickBottomCoHost:liveRoomBottomView
                         itemButton:itemButton
                         roleStatus:roleStatus];
        }
    } else if (itemButton.currentState == LiveRoomItemButtonStateChat) {
        if (self.coHostComponent.isConnect) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"host_connection_error")];
        } else {
            [self clickBottomAddGuests:liveRoomBottomView
                            itemButton:itemButton
                            roleStatus:roleStatus];
        }
    } else if (itemButton.currentState == LiveRoomItemButtonStateBeauty) {
        if (!self.beautyComponent) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"not_support_beauty_error")];
            return;
        }
        if (roleStatus == BottomRoleStatusHost) {
            [self.beautyComponent showWithView:self.view dismissBlock:^(BOOL result){

            }];
        } else if (roleStatus == BottomRoleStatusGuests) {
            [self.beautyComponent showWithView:self.view dismissBlock:^(BOOL result){

            }];
        }

    } else if (itemButton.currentState == LiveRoomItemButtonStateSet) {
        [self clickBottomSettingWithRoleStatus:roleStatus];
    } else if (itemButton.currentState == LiveRoomItemButtonStateEnd) {
        [self clickBottomEndLive];
    } else if (itemButton.currentState == LiveRoomItemButtonStateGift) {
        [self.view addSubview:self.giftView];
        [self.giftView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.view);
        }];
    }
}

#pragma mark - Reconnect

- (void)reconnectLiveRoom {
    // Reconnection after network disconnection
    __weak __typeof(self) wself = self;
    [LiveRTSManager reconnect:self.liveRoomModel.roomID
                        block:^(LiveReconnectModel *reconnectModel, RTSACKModel *model) {
                            if (model.result) {
                                // Reconnect successfully, restore the status in the room
                                [wself joinRoom];
                            } else if (model.code == RTSStatusCodeUserIsInactive ||
                                       model.code == RTSStatusCodeRoomDisbanded ||
                                       model.code == RTSStatusCodeUserNotFound) {
                                // The user has left the room/live has ended, exit the room.
                                [[ToastComponent shareToastComponent] showWithMessage:model.message delay:0.8];
                                [wself hangUp];
                            } else {
                            }
                        }];
}

#pragma mark - Network request

- (void)loadDataWithJoinLiveRoom {
    __weak __typeof(self) wself = self;
    [[ToastComponent shareToastComponent] showLoading];
    [LiveRTSManager liveJoinLiveRoom:self.liveRoomModel.roomID
                               block:^(LiveRoomInfoModel *roomModel,
                                       LiveUserModel *userModel,
                                       RTSACKModel *model) {
                                   [[ToastComponent shareToastComponent] dismiss];
                                   if (model.result) {
                                       [wself restoreRoomWithRoomInfoModel:roomModel
                                                                 userModel:userModel
                                                               rtcUserList:@[]];
                                   } else {
                                       AlertActionModel *alertModel = [[AlertActionModel alloc] init];
                                       alertModel.title = LocalizedString(@"ok");
                                       alertModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
                                           if ([action.title isEqualToString:LocalizedString(@"ok")]) {
                                               [wself hangUp];
                                           }
                                       };
                                       [[AlertActionManager shareAlertActionManager] showWithMessage:LocalizedString(@"joining_room_failed") actions:@[alertModel]];
                                   }
                               }];
}

- (void)loadDataWithAnchorLinkmicReply:(LiveUserModel *)inviter
                              linkerID:(NSString *)linkerID
                             replyType:(LiveInviteReply)replyType {
    __weak __typeof(self) wself = self;
    [LiveRTSManager liveAnchorLinkmicReply:self.liveRoomModel.roomID
                             inviterRoomID:inviter.roomID
                             inviterUserID:inviter.uid
                                  linkerID:linkerID
                                 replyType:replyType
                                     block:^(NSString *_Nullable rtcRoomID, NSString *_Nullable rtcToken, NSArray<LiveUserModel *> *_Nullable userList, RTSACKModel *_Nonnull model) {
                                         if (model.result) {
                                             if (replyType == LiveInviteReplyPermitted) {
                                                 wself.linkerID = linkerID;
                                                 [wself receivedCoHostJoin:userList
                                                         otherAnchorRoomId:rtcRoomID
                                                          otherAnchorToken:rtcToken];
                                             }
                                         } else {
                                             [[ToastComponent shareToastComponent] showWithMessage:model.message];
                                         }
                                     }];
}

- (void)loadDataWithAnchorLinkmicFinish {
    [LiveRTSManager liveAnchorLinkmicFinish:self.liveRoomModel.roomID
                                   linkerID:self.linkerID
                                      block:^(RTSACKModel *_Nonnull model) {
                                          if (!model.result) {
                                              [[ToastComponent shareToastComponent] showWithMessage:model.message];
                                          }
                                      }];
}

- (void)loadDataWithPermitAudienceLinkmic:(NSString *)audienceRoomID
                           audienceUserID:(NSString *)audienceUserID
                                 linkerID:(NSString *)linkerID
                                    reply:(LiveInviteReply)reply {
    __weak __typeof(self) wself = self;
    [LiveRTSManager liveAudienceLinkmicPermit:self.liveRoomModel.roomID
                               audienceRoomID:audienceRoomID
                               audienceUserID:audienceUserID
                                     linkerID:linkerID
                                   permitType:reply
                                        block:^(NSString *rtcRoomID,
                                                NSString *rtcToken,
                                                NSArray<LiveUserModel *> *userList,
                                                RTSACKModel *model) {
                                            if (model.result) {
                                                if (reply == LiveInviteReplyPermitted) {
                                                    wself.linkerID = linkerID;
                                                    [wself receivedAddGuestsJoin:userList];
                                                }
                                            } else {
                                                [[ToastComponent shareToastComponent] showWithMessage:model.message];
                                            }
                                        }];
}

- (void)loadDataWithAudienceLinkmicFinish {
    [LiveRTSManager liveAudienceLinkmicFinish:self.liveRoomModel.roomID
                                        block:^(RTSACKModel *_Nonnull model) {
                                            if (!model.result) {
                                                [[ToastComponent shareToastComponent] showWithMessage:model.message];
                                            }
                                        }];
}

- (void)loadDataWithReplyAudienceLinkmic:(NSString *)linkerID
                                   reply:(LiveInviteReply)reply {
    __weak __typeof(self) wself = self;
    [LiveRTSManager liveAudienceLinkmicReply:self.liveRoomModel.roomID
                                    linkerID:linkerID
                                   replyType:reply
                                       block:^(NSString *rtcRoomID,
                                               NSString *rtcToken,
                                               NSArray<LiveUserModel *> *userList,
                                               RTSACKModel *model) {
                                           if (model.result) {
                                               if (reply == LiveInviteReplyPermitted) {
                                                   wself.linkerID = linkerID;
                                                   // The anchor invites the audience, and the audience agrees to join the mic(audience application)
                                                   [wself.addGuestsComponent joinRTCRoomByToken:rtcToken
                                                                                      rtcRoomID:rtcRoomID
                                                                                         userID:[LocalUserComponent userModel].uid];
                                                   [wself receivedAddGuestsJoin:userList];
                                                   // 成为嘉宾更新底部UI
                                                   [wself.bottomView updateButtonRoleStatus:BottomRoleStatusGuests];
                                               }
                                           } else {
                                               [[ToastComponent shareToastComponent] showWithMessage:model.message];
                                           }
                                       }];
}

- (void)loadDataWithAudienceLinkmicLeave {
    __weak __typeof(self) wself = self;
    [LiveRTSManager liveAudienceLinkmicLeave:self.liveRoomModel.roomID
                                    linkerID:self.linkerID
                                       block:^(RTSACKModel *_Nonnull model) {
                                           if (model.result) {
                                               // 成为观众更新底部UI
                                               [wself.bottomView updateButtonRoleStatus:BottomRoleStatusAudience];
                                           } else {
                                               [[ToastComponent shareToastComponent] showWithMessage:model.message];
                                           }
                                       }];
}

- (void)loadDataWithupdateRes:(LiveUserModel *)loginUserModel {
    BOOL isHost = (loginUserModel.role == 2);
    CGSize videoSize = isHost ? [LiveSettingVideoConfig defultVideoConfig].videoSize : [LiveSettingVideoConfig defultVideoConfig].guestVideoSize;
    [LiveRTSManager liveUpdateResWithSize:videoSize
                                   roomID:self.liveRoomModel.roomID
                                    block:^(RTSACKModel *_Nonnull model) {
                                        if (model.result) {
                                            if (isHost) {
                                                // Streamers update merge resolution and RTC encoding resolution
                                                [[LiveRTCManager shareRtc] updateLiveTranscodingResolution:videoSize];
                                                [[LiveRTCManager shareRtc] updateVideoEncoderResolution:videoSize];
                                            } else {
                                                // Guests update RTC encoding resolution
                                                [[LiveRTCManager shareRtc] updateVideoEncoderResolution:videoSize];
                                            }
                                        }
                                    }];
}

#pragma mark - SocketControl

- (void)addUser:(LiveUserModel *)userModel audienceCount:(NSInteger)audienceCount {
    NSString *message = [NSString stringWithFormat:@"%@ %@",
                                                   userModel.name,
                                                   LocalizedString(@"joined")];
    BaseIMModel *imModel = [[BaseIMModel alloc] init];
    imModel.message = message;
    [self.imComponent addIM:imModel];

    [self.addGuestsComponent updateList];
    [self.peopleNumView updateTitleLabel:audienceCount];
}

- (void)removeUser:(LiveUserModel *)userModel audienceCount:(NSInteger)audienceCount {
    NSString *message = [NSString stringWithFormat:@"%@ %@",
                                                   userModel.name,
                                                   LocalizedString(@"left")];
    BaseIMModel *imModel = [[BaseIMModel alloc] init];
    imModel.message = message;
    [self.imComponent addIM:imModel];

    [self.addGuestsComponent updateList];
    [self.peopleNumView updateTitleLabel:audienceCount];
}

- (void)receivedIMMessage:(NSString *)message sendUserModel:(LiveUserModel *)sendUserModel {
    if (![sendUserModel.uid isEqualToString:[LocalUserComponent userModel].uid]) {
        BOOL isFlower = [message containsString:@"鲜花"];
        BOOL isRocket = [message containsString:@"火箭"];
        NSString *imageName = @"";
        if (isFlower) {
            imageName = @"flower";
        }
        if (isRocket) {
            imageName = @"rocket";
        }

        NSRange userNameRang = [message rangeOfString:@"送出"];

        NSString *title = isFlower ? LocalizedString(@"flower") : LocalizedString(@"rocket");
        NSString *userName = [message substringToIndex:userNameRang.location];
        NSString *message = [NSString stringWithFormat:@"%@%@ %@",
                                                       userName,
                                                       LocalizedString(@"sent"),
                                                       title];
        // IM local display
        BaseIMModel *imModel = [[BaseIMModel alloc] init];
        imModel.iconImage = [UIImage imageNamed:imageName bundleName:HomeBundleName];
        imModel.message = message;
        [self.imComponent addIM:imModel];
    }
}

- (void)receivedCoHostInviteWithUser:(LiveUserModel *)inviter
                            linkerID:(NSString *)linkerID
                               extra:(NSString *)extra {
    __weak __typeof(self) wself = self;
    AlertActionModel *alertModel = [[AlertActionModel alloc] init];
    alertModel.title = LocalizedString(@"accept");
    alertModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
        if ([action.title isEqualToString:LocalizedString(@"accept")]) {
            // Accept make cohost
            [wself loadDataWithAnchorLinkmicReply:inviter
                                         linkerID:linkerID
                                        replyType:LiveInviteReplyPermitted];
        }
    };

    AlertActionModel *alertCancelModel = [[AlertActionModel alloc] init];
    alertCancelModel.title = LocalizedString(@"decline");
    alertCancelModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
        if ([action.title isEqualToString:LocalizedString(@"decline")]) {
            // Decline make cohost
            [wself loadDataWithAnchorLinkmicReply:inviter
                                         linkerID:linkerID
                                        replyType:LiveInviteReplyForbade];
        }
    };

    NSString *message = [NSString stringWithFormat:LocalizedString(@"%@invites_live"), inviter.name];
    [[AlertActionManager shareAlertActionManager] showWithMessage:message
                                                          actions:@[alertCancelModel, alertModel]
                                                        hideDelay:LiveApplyOvertimeInterval];
    [self.coHostComponent dismissInviteList];
    [self.addGuestsComponent dismissList];
}

- (void)receivedCoHostRefuseWithUser:(LiveUserModel *)invitee {
    NSString *message = LocalizedString(@"not_available_live");
    [[ToastComponent shareToastComponent] showWithMessage:message];
    [self.bottomView updateButtonStatus:LiveRoomItemButtonStatePK touchStatus:LiveRoomItemTouchStatusNone];
}

- (void)receivedCoHostSucceedWithUser:(LiveUserModel *)invitee
                             linkerID:(NSString *)linkerID {
    self.linkerID = linkerID;
}

- (void)receivedCoHostJoin:(NSArray<LiveUserModel *> *)userlList
         otherAnchorRoomId:(NSString *)otherRoomId
          otherAnchorToken:(NSString *)otherToken {
    __weak __typeof(self) wself = self;
    // Show make cohost interface
    [self.coHostComponent showCoHost:self.liveView
                       streamPushUrl:self.streamPushUrl
                       userModelList:userlList
                      loginUserModel:self.currentUserModel
                   otherAnchorRoomId:otherRoomId
                    otherAnchorToken:otherToken
                       completeBlock:^{
                           [wself.livePushStreamComponent close];
                       }];
    [self.bottomView updateButtonStatus:LiveRoomItemButtonStatePK
                            touchStatus:LiveRoomItemTouchStatusClose];
}

- (void)receivedCoHostEnd {
    if ([self isHost]) {
        NSString *message = LocalizedString(@"disconnected");
        [[ToastComponent shareToastComponent] showWithMessage:message];
        [self.coHostComponent closeCoHost];
        [self.livePushStreamComponent openWithUserModel:self.currentUserModel];
        [self.bottomView updateButtonStatus:LiveRoomItemButtonStatePK touchStatus:LiveRoomItemTouchStatusNone];
    }
}

- (void)receivedAddGuestsApplyWithUser:(LiveUserModel *)applicant
                              linkerID:(NSString *)linkerID
                                 extra:(NSString *)extra {
    if ([self isHost]) {
        __weak __typeof(self) wself = self;
        AlertActionModel *alertModel = [[AlertActionModel alloc] init];
        alertModel.title = LocalizedString(@"accept");
        alertModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
            if ([action.title isEqualToString:LocalizedString(@"accept")]) {
                // Accept the apply make guest
                [wself loadDataWithPermitAudienceLinkmic:applicant.roomID
                                          audienceUserID:applicant.uid
                                                linkerID:linkerID
                                                   reply:LiveInviteReplyPermitted];
            }
        };

        AlertActionModel *alertCancelModel = [[AlertActionModel alloc] init];
        alertCancelModel.title = LocalizedString(@"decline");
        alertCancelModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
            if ([action.title isEqualToString:LocalizedString(@"decline")]) {
                // Decline the apply make guest
                [wself loadDataWithPermitAudienceLinkmic:applicant.roomID
                                          audienceUserID:applicant.uid
                                                linkerID:linkerID
                                                   reply:LiveInviteReplyForbade];
            }
        };

        NSString *message = [NSString stringWithFormat:LocalizedString(@"%@_join_live"), applicant.name];
        [[AlertActionManager shareAlertActionManager] showWithMessage:message
                                                              actions:@[alertCancelModel, alertModel]
                                                            hideDelay:LiveApplyOvertimeInterval];

        [self.coHostComponent dismissInviteList];
        [self.addGuestsComponent dismissList];
    }
}

- (void)receivedAddGuestsInviteWithUser:(LiveUserModel *)inviter
                               linkerID:(NSString *)linkerID
                                  extra:(NSString *)extra {
    __weak __typeof(self) wself = self;
    AlertActionModel *alertModel = [[AlertActionModel alloc] init];
    alertModel.title = LocalizedString(@"accept");
    alertModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
        if ([action.title isEqualToString:LocalizedString(@"accept")]) {
            // Accept the invite make guest
            [wself loadDataWithReplyAudienceLinkmic:linkerID
                                              reply:LiveInviteReplyPermitted];
        }
    };

    AlertActionModel *alertCancelModel = [[AlertActionModel alloc] init];
    alertCancelModel.title = LocalizedString(@"decline");
    alertCancelModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
        if ([action.title isEqualToString:LocalizedString(@"decline")]) {
            // Decline the invite make guest
            [wself loadDataWithReplyAudienceLinkmic:linkerID
                                              reply:LiveInviteReplyForbade];
        }
    };

    [[AlertActionManager shareAlertActionManager] showWithMessage:LocalizedString(@"live_together_invited")
                                                          actions:@[alertCancelModel, alertModel]
                                                        hideDelay:LiveApplyOvertimeInterval];

    [self.addGuestsComponent closeApply];
}

- (void)receivedAddGuestsManageGuestMedia:(NSString *)uid
                                   camera:(NSInteger)camera
                                      mic:(NSInteger)mic {
    if (![uid isEqualToString:[LocalUserComponent userModel].uid]) {
        return;
    }
    BOOL cameraBool = self.currentUserModel.camera;
    BOOL micBool = self.currentUserModel.mic;
    if (camera != -1) {
        cameraBool = camera == 1 ? YES : NO;
    }
    if (mic != -1) {
        micBool = mic == 1 ? YES : NO;
    }
    if (self.addGuestsComponent.isConnect) {
        [self.addGuestsComponent updateGuestsCamera:cameraBool uid:uid];
        [self.addGuestsComponent updateGuestsMic:micBool uid:uid];
        if (camera != -1 && !cameraBool) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"off_camera_title")];
        }
        if (mic != -1 && !micBool) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"off_mic_title")];
        }
    }
    self.currentUserModel.camera = cameraBool;
    self.currentUserModel.mic = micBool;
    [self.settingComponent refreshGuestSettingView];
    [LiveRTSManager liveUpdateMediaStatus:self.liveRoomModel.roomID
                                      mic:micBool
                                   camera:cameraBool
                                    block:nil];
}

- (void)receivedAddGuestsSucceedWithUser:(LiveUserModel *)invitee
                                linkerID:(NSString *)linkerID
                               rtcRoomID:(NSString *)rtcRoomID
                                rtcToken:(NSString *)rtcToken {
    self.linkerID = linkerID;
    if (![self isHost]) {
        // Become a guest to update the bottom UI
        [self.bottomView updateButtonRoleStatus:BottomRoleStatusGuests];
        // Become a guest and join the RTC room(Host invites audience)
        [self.addGuestsComponent joinRTCRoomByToken:rtcToken
                                          rtcRoomID:rtcRoomID
                                             userID:[LocalUserComponent userModel].uid];
        // Guests restore beauty special effects
        [self.beautyComponent resume];

        [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"start_live_soon")];
    }
}

- (void)receivedAddGuestsRefuseWithUser:(LiveUserModel *)invitee {
    NSString *message = @"";
    if ([self isHost]) {
        message = [NSString stringWithFormat:LocalizedString(@"%@refuses_connect"), invitee.name];
        [self.addGuestsComponent updateList];
    } else {
        message = LocalizedString(@"request_declined");
        self.currentUserModel.status = LiveInteractStatusOther;
        [self.addGuestsComponent closeApply];
    }
    [[ToastComponent shareToastComponent] showWithMessage:message];
}

- (void)receivedAddGuestsJoin:(NSArray<LiveUserModel *> *)userList {
    if ([self isHost]) {
        [self.livePushStreamComponent close];
        [self.addGuestsComponent showAddGuests:self.liveView
                                 streamPushUrl:_streamPushUrl
                                       hostUid:self.liveRoomModel.anchorUserID
                                      userList:userList];
        [self.addGuestsComponent updateGuests:userList];
        [self.addGuestsComponent updateList];

        if (userList.count == 2) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"click_seat_title")];
        }
    } else {
        [self.livePullStreamComponent close];
        [self.addGuestsComponent showAddGuests:self.liveView
                                 streamPushUrl:_streamPushUrl
                                       hostUid:self.liveRoomModel.anchorUserID
                                      userList:userList];
        [self.addGuestsComponent updateGuests:userList];
        [self.bottomView updateButtonRoleStatus:BottomRoleStatusGuests];
        self.currentUserModel.status = LiveInteractStatusAudienceLink;
        [self.addGuestsComponent closeApply];
    }
}

- (void)receivedAddGuestsRemoveWithUser:(NSString *)uid userList:(NSArray<LiveUserModel *> *)userList {
    if ([self isHost]) {
        NSString *userName = [self.addGuestsComponent removeAddGuestsUid:uid userList:userList];
        if (userName != nil) {
            NSString *message = [NSString stringWithFormat:LocalizedString(@"%@disconnected_live"), userName];
            [[ToastComponent shareToastComponent] showWithMessage:message];
        }
    } else {
        if ([uid isEqualToString:[LocalUserComponent userModel].uid]) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"host_disconnected_live")];
            [self.addGuestsComponent closeAddGuests];
            [self.addGuestsComponent closeApply];
            [self.addGuestsComponent leaveRTCRoom];
            [self.livePullStreamComponent open:self.liveRoomModel];
            [self.bottomView updateButtonRoleStatus:BottomRoleStatusAudience];
            self.currentUserModel.status = LiveInteractStatusOther;
        } else {
            NSString *userName = [self.addGuestsComponent removeAddGuestsUid:uid userList:userList];
            if (userName != nil) {
                NSString *message = [NSString stringWithFormat:LocalizedString(@"%@disconnected_live"), userName];
                [[ToastComponent shareToastComponent] showWithMessage:message];
            }
        }
    }
}

- (void)receivedAddGuestsEnd {
    if ([self isHost]) {
        [self.addGuestsComponent closeAddGuests];
        [self.addGuestsComponent updateList];
        [self.livePushStreamComponent openWithUserModel:self.currentUserModel];
    } else {
        [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"host_disconnected_live")];
        [self.addGuestsComponent closeAddGuests];
        [self.addGuestsComponent closeApply];
        [self.addGuestsComponent leaveRTCRoom];
        [self.livePullStreamComponent open:self.liveRoomModel];
        [self.bottomView updateButtonRoleStatus:BottomRoleStatusAudience];
        self.currentUserModel.status = LiveInteractStatusOther;
    }
}

- (void)receivedLiveEnd:(NSString *)type {
    if ([type integerValue] == 2) {
        // timeout close
        if ([self isHost]) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"minutes_error_message") delay:0.8];
        } else {
            // Audience & Guests
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"live_ended") delay:0.8];
        }
    } else if ([type integerValue] == 3) {
        // Illegal close
        [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"closed_terms_service") delay:0.8];
    } else {
        if (![self isHost]) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"live_ended") delay:0.8];
        }
    }
    [self navigationControllerPop];
}

- (void)receivedRoomStatus:(LiveInteractStatus)status {
    self.liveRoomModel.hostUserModel.status = status;
    if (![self isHost]) {
        // If the connected player does not support SEI
        if (![[LivePlayerManager sharePlayer] isSupportSEI]) {
            // There will be time to check between the video stream and the service message. It is recommended to use the video SEI solution.
            // If the connected player does not support parsing SEI, the rendering layout can also be updated through the business status
            SEL selector = @selector(delayUpdatePullStatus:);
            [NSObject cancelPreviousPerformRequestsWithTarget:self selector:selector object:nil];
            [self performSelector:selector withObject:@(status) afterDelay:3];
        }
    }
}

- (void)delayUpdatePullStatus:(NSNumber *)statusNum {
    [self updatePullStatus:statusNum.integerValue];
}

- (void)receivedAddGuestsMediaChangeWithUser:(NSString *)uid
                                 operatorUid:(NSString *)operatorUid
                                      camera:(BOOL)camera
                                         mic:(BOOL)mic {
    [self changeMediaWithUser:uid camera:camera mic:mic];
}

- (void)receivedLeaveTemporary:(NSString *)uid
                      userName:(NSString *)userName
                      userRole:(NSString *)userRole {
    NSString *message = @"";
    if ([userRole integerValue] == 2) {
        message = LocalizedString(@"host_back_soon");
    } else {
        message = LocalizedString(@"guest_back_soon");
    }
    [[ToastComponent shareToastComponent] showWithMessage:message];
}

#pragma mark - LiveRTCManagerDelegate

- (void)liveRTCManager:(LiveRTCManager *)manager
    onRoomStateChanged:(RTCJoinModel *)joinModel {
    if (joinModel.joinType == 0) {
        // Entering the room for the first time
        if ([self isHost]) {
            // turn on the merge and retweet
            [[LiveRTCManager shareRtc]
                startMixStreamRetweetWithPushUrl:self.streamPushUrl
                                        hostUser:self.liveRoomModel.hostUserModel
                                       rtcRoomId:self.liveRoomModel.rtcRoomId];
        }
    } else {
        // Entering the room after reconnection
        [self reconnectLiveRoom];
    }
}

#pragma mark - Private Action

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self.settingComponent close];
}

- (void)addSubviewAndConstraints {
    [self.view addSubview:self.liveView];
    [self.liveView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];

    [self.view addSubview:self.hostAvatarView];
    [self.hostAvatarView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(36);
        make.left.equalTo(self.view).offset(16);
        make.top.equalTo(self.view).offset([DeviceInforTool getStatusBarHight] + 2);
    }];

    [self.view addSubview:self.peopleNumView];
    [self.peopleNumView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(32);
        make.right.equalTo(self.view).offset(-16);
        make.top.equalTo(self.view).offset([DeviceInforTool getStatusBarHight] + 5);
    }];

    [self.view addSubview:self.bottomView];
    [self.bottomView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-16);
        make.bottom.mas_equalTo(-32 - [DeviceInforTool getVirtualHomeHeight]);
        make.height.mas_equalTo(36);
        make.width.mas_equalTo(0);
    }];

    [self imComponent];
}

- (void)showEndCoHostAlert {
    __weak __typeof(self) wself = self;
    AlertActionModel *alertModel = [[AlertActionModel alloc] init];
    alertModel.title = LocalizedString(@"disconnect");
    alertModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
        if ([action.title isEqualToString:LocalizedString(@"disconnect")]) {
            [wself loadDataWithAnchorLinkmicFinish];
        }
    };
    AlertActionModel *alertCancelModel = [[AlertActionModel alloc] init];
    alertCancelModel.title = LocalizedString(@"cancel");
    NSString *message = LocalizedString(@"quit_title");
    [[AlertActionManager shareAlertActionManager] showWithMessage:message actions:@[alertCancelModel, alertModel]];
}

- (void)updateLayoutToRole:(LiveUserModel *)userModel
               rtcUserList:(NSArray<LiveUserModel *> *)rtcUserList {
    if (userModel.role == LiveUserRoleHost) {
        // Host
        if (userModel.status == LiveInteractStatusHostLink) {
            // Make coHsot
            [self.bottomView updateButtonRoleStatus:BottomRoleStatusHost];
            [self.bottomView updateButtonStatus:LiveRoomItemButtonStatePK touchStatus:LiveRoomItemTouchStatusClose];
        } else if (userModel.status == LiveInteractStatusAudienceLink) {
            // Make Guests
            [self.bottomView updateButtonRoleStatus:BottomRoleStatusHost];
            [self.addGuestsComponent showAddGuests:self.liveView
                                     streamPushUrl:_streamPushUrl
                                           hostUid:self.liveRoomModel.anchorUserID
                                          userList:rtcUserList];
        } else {
            // Unknown state
            [self.bottomView updateButtonRoleStatus:BottomRoleStatusHost];
            [self.livePushStreamComponent openWithUserModel:userModel];
            [self.livePushStreamComponent updateHostMic:userModel.mic
                                                 camera:userModel.camera];
            if (self.addGuestsComponent.isConnect) {
                [self.addGuestsComponent closeAddGuests];
            }
            if (self.coHostComponent.isConnect) {
                [self.coHostComponent closeCoHost];
            }
        }
    } else {
        // Audience
        if (userModel.status == LiveInteractStatusAudienceLink) {
            // Make Guests
            [self.bottomView updateButtonRoleStatus:BottomRoleStatusGuests];
            [self.addGuestsComponent showAddGuests:self.liveView
                                     streamPushUrl:_streamPushUrl
                                           hostUid:self.liveRoomModel.anchorUserID
                                          userList:rtcUserList];
        } else {
            // Make audience, watch live
            [self.bottomView updateButtonRoleStatus:BottomRoleStatusAudience];
            [self.livePullStreamComponent open:self.liveRoomModel];
            if (self.addGuestsComponent.isConnect) {
                [self.addGuestsComponent closeAddGuests];
                [self.addGuestsComponent leaveRTCRoom];
            }
        }
    }
    [self addBeautyEffect:userModel];
}

- (void)addBeautyEffect:(LiveUserModel *)userModel {
    BOOL beautyEnable = NO;
    if (userModel.role == LiveUserRoleAudience) {
        if (userModel.status == LiveInteractStatusAudienceLink) {
            beautyEnable = YES;
        }
    } else if (userModel.role == LiveUserRoleHost) {
        beautyEnable = YES;
    } else {
        beautyEnable = NO;
    }
    if (beautyEnable) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            // CV Simultaneous initialization and setting will cause deadlock, requiring delayed setting of special effects
            [self.beautyComponent resume];
        });
    }
}

- (void)clickBottomAddGuests:(LiveRoomBottomView *)bottomView
                  itemButton:(LiveRoomItemButton *_Nullable)itemButton
                  roleStatus:(BottomRoleStatus)roleStatus {
    __weak __typeof(self) wself = self;
    if (roleStatus == BottomRoleStatusAudience) {
        if (self.liveRoomModel.hostUserModel.status == LiveInteractStatusHostLink) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"host_liveing")];
        } else {
            [self.addGuestsComponent showApply:self.currentUserModel
                                        hostID:self.liveRoomModel.anchorUserID];
        }
    } else if (roleStatus == BottomRoleStatusHost) {
        if ([bottomView getButtonTouchStatus:LiveRoomItemButtonStatePK]) {
            [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"request_sent_waiting")];
        } else {
            [self.addGuestsComponent showList:^(LiveAddGuestsDismissState state) {
                if (state == LiveAddGuestsDismissStateCloseConnect) {
                    [wself showCloseAddGuests];
                }
            }];
        }
    } else if (roleStatus == BottomRoleStatusGuests) {
        [self showCloseAddGuests];
    } else {
    }
}

- (void)clickBottomCoHost:(LiveRoomBottomView *)bottomView
               itemButton:(LiveRoomItemButton *_Nullable)itemButton
               roleStatus:(BottomRoleStatus)roleStatus {
    if (itemButton.touchStatus == LiveRoomItemTouchStatusClose) {
        [self showEndCoHostAlert];
    } else if (itemButton.touchStatus == LiveRoomItemTouchStatusIng) {
        // Waiting for the response from the invited host
    } else {
        // show list view
        [self.coHostComponent showInviteList:nil];
    }
}

- (void)clickBottomSettingWithRoleStatus:(BottomRoleStatus)roleStatus {
    if (roleStatus == BottomRoleStatusHost) {
        if (self.addGuestsComponent.isConnect || self.coHostComponent.isConnect) {
            [self.settingComponent showWithType:LiveRoomSettingTypeHostChat
                                  fromSuperView:self.view
                                         roomID:self.liveRoomModel
                                      userModel:self.currentUserModel];
        } else {
            [self.settingComponent showWithType:LiveRoomSettingTypeHostLiving
                                  fromSuperView:self.view
                                         roomID:self.liveRoomModel
                                      userModel:self.currentUserModel];
        }
    } else if (roleStatus == BottomRoleStatusGuests) {
        [self.settingComponent showWithType:LiveRoomSettingTypeGuest
                              fromSuperView:self.view
                                     roomID:self.liveRoomModel
                                  userModel:self.currentUserModel];
    } else if (roleStatus == BottomRoleStatusAudience) {
        [self.settingComponent showWithType:LiveRoomSettingTypeAudience
                              fromSuperView:self.view
                                     roomID:self.liveRoomModel
                                  userModel:nil];
    }
}

- (void)clickBottomEndLive {
    if ([self isHost]) {
        __weak __typeof(self) wself = self;
        AlertActionModel *alertModel = [[AlertActionModel alloc] init];
        alertModel.title = LocalizedString(@"end_live_title");
        alertModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
            if ([action.title isEqualToString:LocalizedString(@"end_live_title")]) {
                [wself hangUp];
            }
        };
        AlertActionModel *alertCancelModel = [[AlertActionModel alloc] init];
        alertCancelModel.title = LocalizedString(@"cancel");
        NSString *message = LocalizedString(@"end_live_alert");
        [[AlertActionManager shareAlertActionManager] showWithMessage:message actions:@[alertCancelModel, alertModel]];
    } else {
        [self hangUp];
    }
}

- (void)showCloseAddGuests {
    if ([self isHost]) {
        __weak __typeof(self) wself = self;
        AlertActionModel *alertModel = [[AlertActionModel alloc] init];
        alertModel.title = LocalizedString(@"exit");
        alertModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
            if ([action.title isEqualToString:LocalizedString(@"exit")]) {
                [wself loadDataWithAudienceLinkmicFinish];
            }
        };

        AlertActionModel *alertCancelModel = [[AlertActionModel alloc] init];
        alertCancelModel.title = LocalizedString(@"cancel");

        NSString *message = [NSString stringWithFormat:LocalizedString(@"%@sure_stop_live"), @(self.addGuestsComponent.guestList.count).stringValue];
        [[AlertActionManager shareAlertActionManager] showWithMessage:message actions:@[alertCancelModel, alertModel]];
    } else {
        __weak __typeof(self) wself = self;
        AlertActionModel *alertModel = [[AlertActionModel alloc] init];
        alertModel.title = LocalizedString(@"disconnect");
        alertModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
            if ([action.title isEqualToString:LocalizedString(@"disconnect")]) {
                [wself loadDataWithAudienceLinkmicLeave];
            }
        };

        AlertActionModel *alertCancelModel = [[AlertActionModel alloc] init];
        alertCancelModel.title = LocalizedString(@"cancel");

        NSString *message = LocalizedString(@"disconnect_host_live");
        [[AlertActionManager shareAlertActionManager] showWithMessage:message actions:@[alertCancelModel, alertModel]];
    }
}

- (void)joinRoom {
    if ([self isHost]) {
        // The host creates a room, no need to join the room
        [self restoreRoomWithRoomInfoModel:self.liveRoomModel
                                 userModel:self.liveRoomModel.hostUserModel
                               rtcUserList:@[]];
    } else {
        // Audience need to join the room first
        [self loadDataWithJoinLiveRoom];
        [self.livePullStreamComponent open:self.liveRoomModel];
    }
}

- (void)restoreRoomWithRoomInfoModel:(LiveRoomInfoModel *)roomModel
                           userModel:(LiveUserModel *)userModel
                         rtcUserList:(NSArray<LiveUserModel *> *)rtcUserList {
    self.liveRoomModel = roomModel;
    self.currentUserModel = userModel;
    // Join RTS room
    [LiveRTCManager shareRtc].delegate = self;
    [[LiveRTCManager shareRtc] joinLiveRoomByToken:roomModel.rtmToken
                                            roomID:roomModel.roomID
                                            userID:[LocalUserComponent userModel].uid];

    if ([self isHost]) {
        // Host join RTC room
        [[LiveRTCManager shareRtc] joinRTCRoomByToken:roomModel.rtcToken
                                            rtcRoomID:roomModel.rtcRoomId
                                               userID:[LocalUserComponent userModel].uid];
    }
    // Update UI layout
    [self addSubviewAndConstraints];
    self.hostAvatarView.hostName = roomModel.anchorUserName;
    [self.peopleNumView updateTitleLabel:roomModel.audienceCount];
    [self updateLayoutToRole:userModel rtcUserList:rtcUserList];
    [self updatePullStatus:roomModel.hostUserModel.status];
    [self changeMediaWithUser:roomModel.hostUserModel.uid
                       camera:roomModel.hostUserModel.camera
                          mic:roomModel.hostUserModel.mic];

    // Update Resolution
    [self loadDataWithupdateRes:self.currentUserModel];
}

- (BOOL)isHost {
    return [self.liveRoomModel.anchorUserID isEqualToString:[LocalUserComponent userModel].uid];
}

- (void)hangUp {
    __weak __typeof(self) wself = self;
    if ([self isHost]) {
        // Host
        [LiveRTSManager liveFinishLive:self.liveRoomModel.roomID
                                 block:^(RTSACKModel *_Nonnull model) {
                                     __strong __typeof(wself) strongSelf = wself;
                                     if (strongSelf.hangUpBlock) {
                                         strongSelf.hangUpBlock(model.result);
                                     }
                                 }];
    } else {
        // Audience & Guests
        [LiveRTSManager liveLeaveLiveRoom:self.liveRoomModel.roomID block:^(RTSACKModel *_Nonnull model) {
            __strong __typeof(wself) strongSelf = wself;
            if (strongSelf.hangUpBlock) {
                strongSelf.hangUpBlock(model.result);
            }
        }];
    }

    [self navigationControllerPop];
}

- (void)navigationControllerPop {
    UIViewController *jumpVC = nil;
    for (UIViewController *vc in self.navigationController.viewControllers) {
        NSString *vcName = @"LiveRoomListsViewController";
        if ([NSStringFromClass([vc class]) isEqualToString:vcName]) {
            jumpVC = vc;
            break;
        }
    }
    if (jumpVC) {
        [self.navigationController popToViewController:jumpVC animated:YES];
    } else {
        [self.navigationController popViewControllerAnimated:YES];
    }
}

- (void)updatePullStatus:(LiveInteractStatus)status {
    if (![self isHost]) {
        PullRenderStatus pullStatus = PullRenderStatusNone;
        if (status == LiveInteractStatusHostLink) {
            pullStatus = PullRenderStatusCoHst;
        }
        [self.livePullStreamComponent updateWithStatus:pullStatus];
    }
}

- (void)changeMediaWithUser:(NSString *)uid
                     camera:(BOOL)camera
                        mic:(BOOL)mic {
    if ([uid isEqualToString:self.liveRoomModel.anchorUserID]) {
        self.liveRoomModel.hostUserModel.camera = camera;
        self.liveRoomModel.hostUserModel.mic = mic;
        if (self.livePushStreamComponent.isConnect) {
            [self.livePushStreamComponent updateHostMic:mic camera:camera];
        }
        if (self.livePullStreamComponent.isConnect) {
            [self.livePullStreamComponent updateHostMic:mic camera:camera];
        }
    }
    if (self.addGuestsComponent.isConnect) {
        [self.addGuestsComponent updateGuestsCamera:camera uid:uid];
        [self.addGuestsComponent updateGuestsMic:mic uid:uid];
        if ([self isHost]) {
            [self.addGuestsComponent closeSheet:uid];
        }
    }
    if (self.coHostComponent.isConnect) {
        [self.coHostComponent updateGuestsMic:mic uid:uid];
        [self.coHostComponent updateGuestsCamera:camera uid:uid];
    }
    if ([uid isEqualToString:[LocalUserComponent userModel].uid]) {
        self.currentUserModel.mic = mic;
        self.currentUserModel.camera = camera;
        [self.settingComponent refreshGuestSettingView];
    }
}

#pragma mark - Getter

- (LiveHostAvatarView *)hostAvatarView {
    if (!_hostAvatarView) {
        _hostAvatarView = [[LiveHostAvatarView alloc] init];
        _hostAvatarView.backgroundColor = [UIColor colorFromRGBHexString:@"#000000" andAlpha:0.2 * 255];
        _hostAvatarView.layer.cornerRadius = 18;
        _hostAvatarView.layer.masksToBounds = YES;
    }
    return _hostAvatarView;
}

- (LivePeopleNumView *)peopleNumView {
    if (!_peopleNumView) {
        _peopleNumView = [[LivePeopleNumView alloc] init];
        _peopleNumView.backgroundColor = [UIColor colorFromRGBHexString:@"#000000" andAlpha:0.2 * 255];
        _peopleNumView.layer.cornerRadius = 16;
        _peopleNumView.layer.masksToBounds = YES;
    }
    return _peopleNumView;
}

- (LiveRoomBottomView *)bottomView {
    if (!_bottomView) {
        _bottomView = [[LiveRoomBottomView alloc] init];
        _bottomView.delegate = self;
    }
    return _bottomView;
}

- (BaseIMComponent *)imComponent {
    if (!_imComponent) {
        _imComponent = [[BaseIMComponent alloc] initWithSuperView:self.view];
    }
    return _imComponent;
}

- (UIView *)liveView {
    if (!_liveView) {
        _liveView = [[UIView alloc] init];
    }
    return _liveView;
}

- (LiveCoHostComponent *)coHostComponent {
    if (!_coHostComponent) {
        _coHostComponent = [[LiveCoHostComponent alloc] initWithRoomID:self.liveRoomModel];
    }
    return _coHostComponent;
}

- (LiveAddGuestsComponent *)addGuestsComponent {
    if (!_addGuestsComponent) {
        _addGuestsComponent = [[LiveAddGuestsComponent alloc]
            initWithRoomID:self.liveRoomModel];
    }
    return _addGuestsComponent;
}

- (LivePushStreamComponent *)livePushStreamComponent {
    if (!_livePushStreamComponent) {
        _livePushStreamComponent = [[LivePushStreamComponent alloc]
            initWithSuperView:self.liveView
                    roomModel:self.liveRoomModel
                streamPushUrl:self.streamPushUrl];
    }
    return _livePushStreamComponent;
}

- (LivePullStreamComponent *)livePullStreamComponent {
    if (!_livePullStreamComponent) {
        _livePullStreamComponent = [[LivePullStreamComponent alloc] initWithSuperView:self.liveView];
    }
    return _livePullStreamComponent;
}

- (LiveRoomSettingComponent *)settingComponent {
    if (!_settingComponent) {
        _settingComponent = [[LiveRoomSettingComponent alloc] init];
    }
    return _settingComponent;
}

- (BytedEffectProtocol *)beautyComponent {
    if (!_beautyComponent) {
        _beautyComponent = [[BytedEffectProtocol alloc] initWithRTCEngineKit:[LiveRTCManager shareRtc].rtcEngineKit];
    }
    return _beautyComponent;
}

#pragma mark - Gift

- (void)giftViewMaskAction {
    [self.giftView removeFromSuperview];
    self.giftView = nil;
}

- (void)senderFlowerGift {
    [self senderGift:@"flower"];
}

- (void)senderRocketGift {
    [self senderGift:@"rocket"];
}

- (void)senderGift:(NSString *)imageName {
    BOOL isSelectFlower = [imageName isEqualToString:@"flower"];
    NSString *title = isSelectFlower ? @"鲜花" : @"火箭";
    NSString *message = [NSString stringWithFormat:@"%@ 送出 %@",
                                                   [LocalUserComponent userModel].name,
                                                   title];

    // Local display
    BaseIMModel *imModel = [[BaseIMModel alloc] init];
    imModel.iconImage = [UIImage imageNamed:imageName bundleName:HomeBundleName];
    imModel.message = [NSString stringWithFormat:@"%@ %@ %@", [LocalUserComponent userModel].name, LocalizedString(@"send"), isSelectFlower ? LocalizedString(@"flower") : LocalizedString(@"rocket")];
    [self.imComponent addIM:imModel];

    // Send to the room
    [LiveRTSManager sendIMMessage:message block:^(RTSACKModel *_Nonnull model) {
        if (!model.result) {
            [[ToastComponent shareToastComponent] showWithMessage:model.message];
        }
    }];

    // Close the panel
    [self giftViewMaskAction];
}

- (UIView *)giftView {
    if (!_giftView) {
        _giftView = [[UIView alloc] init];
        _giftView.backgroundColor = [UIColor clearColor];

        _giftView.userInteractionEnabled = YES;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(giftViewMaskAction)];
        [_giftView addGestureRecognizer:tap];

        UIButton *contentView = [[UIButton alloc] init];
        contentView.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];
        [_giftView addSubview:contentView];
        [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.width.bottom.equalTo(_giftView);
            make.height.mas_equalTo(148 + [DeviceInforTool getVirtualHomeHeight]);
        }];

        UILabel *titleLabel = [[UILabel alloc] init];
        titleLabel.text = LocalizedString(@"gifts");
        titleLabel.font = [UIFont systemFontOfSize:16];
        titleLabel.textColor = [UIColor whiteColor];
        [contentView addSubview:titleLabel];
        [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(contentView);
            make.top.mas_equalTo(16);
        }];

        UIButton *flowerGiftButton = [[UIButton alloc] init];
        [flowerGiftButton addTarget:self action:@selector(senderFlowerGift) forControlEvents:UIControlEventTouchUpInside];
        flowerGiftButton.backgroundColor = [UIColor clearColor];
        [contentView addSubview:flowerGiftButton];
        [flowerGiftButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(40, 69));
            make.top.mas_equalTo(55);
            make.right.equalTo(titleLabel.mas_left).offset(-25);
        }];

        UIButton *rocketGiftButton = [[UIButton alloc] init];
        [rocketGiftButton addTarget:self action:@selector(senderRocketGift) forControlEvents:UIControlEventTouchUpInside];
        rocketGiftButton.backgroundColor = [UIColor clearColor];
        [contentView addSubview:rocketGiftButton];
        [rocketGiftButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(40, 69));
            make.top.mas_equalTo(55);
            make.left.equalTo(titleLabel.mas_right).offset(25);
        }];

        [self addImageAndTitleL:flowerGiftButton imageName:@"flower" message:LocalizedString(@"flower")];
        [self addImageAndTitleL:rocketGiftButton imageName:@"rocket" message:LocalizedString(@"rocket")];
    }
    return _giftView;
}

- (void)addImageAndTitleL:(UIButton *)button
                imageName:(NSString *)imageName
                  message:(NSString *)message {
    UIImageView *imageView = [[UIImageView alloc] init];
    imageView.image = [UIImage imageNamed:imageName bundleName:HomeBundleName];
    [button addSubview:imageView];
    [imageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.height.equalTo(button.mas_width);
        make.centerX.top.equalTo(button);
    }];

    UILabel *messageLabel = [[UILabel alloc] init];
    messageLabel.text = message;
    messageLabel.font = [UIFont systemFontOfSize:12];
    messageLabel.textColor = [UIColor whiteColor];
    [button addSubview:messageLabel];
    [messageLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.centerX.equalTo(button);
    }];
}

- (void)dealloc {
    [UIApplication sharedApplication].idleTimerDisabled = NO;
    [[LiveRTCManager shareRtc] leaveLiveRoom];
    [[LivePlayerManager sharePlayer] stopPull];
    [self.livePullStreamComponent close];
}

@end
