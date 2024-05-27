//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveAddGuestsRoomView.h"
#import "LiveAddGuestsItemView.h"
#import "LiveNoStreamingView.h"
#import "LiveSettingVideoConfig.h"
#import "LiveStateIconView.h"

@interface LiveAddGuestsRoomView ()

@property (nonatomic, strong) LiveNoStreamingView *noStreamingView;
@property (nonatomic, strong) UIView *streamingView;
@property (nonatomic, strong) UIView *guestsView;
@property (nonatomic, strong) NSMutableArray<LiveAddGuestsItemView *> *itemList;
@property (nonatomic, copy) NSArray<LiveUserModel *> *userList;
@property (nonatomic, strong) LiveRoomInfoModel *roomInfoModel;
@property (nonatomic, strong) LiveStateIconView *netQualityView;
@property (nonatomic, strong) LiveStateIconView *micView;
@property (nonatomic, strong) LiveStateIconView *cameraView;

@property (nonatomic, copy) NSString *hostID;

@end

@implementation LiveAddGuestsRoomView

- (instancetype)initWithHostID:(NSString *)hostID
                 roomInfoModel:(LiveRoomInfoModel *)roomInfoModel {
    self = [super init];
    if (self) {
        _roomInfoModel = roomInfoModel;
        _hostID = hostID;
        NSInteger maxItemNumber = 6;
        CGFloat itemHeight = SCREEN_HEIGHT * 80.0 / 667.0;
        CGFloat viewHeight = (itemHeight + 2) * maxItemNumber;
        [self addSubview:self.noStreamingView];
        [self.noStreamingView setUserName:roomInfoModel.anchorUserName];
        [self.noStreamingView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];

        [self addSubview:self.streamingView];
        [self.streamingView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];

        [self addSubview:self.netQualityView];
        [self.netQualityView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.height.mas_equalTo(17);
            make.left.mas_equalTo(16);
            make.top.mas_equalTo(50 + [DeviceInforTool getStatusBarHight]);
        }];

        [self addSubview:self.micView];
        [self.micView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.height.left.equalTo(self.netQualityView);
            make.top.mas_equalTo(75 + [DeviceInforTool getStatusBarHight]);
        }];

        [self addSubview:self.cameraView];
        [self.cameraView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.height.left.equalTo(self.netQualityView);
            make.top.mas_equalTo(100 + [DeviceInforTool getStatusBarHight]);
        }];

        [self addSubview:self.guestsView];
        [self.guestsView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.mas_equalTo(-78 - [DeviceInforTool getVirtualHomeHeight]);
            make.right.mas_equalTo(-16);
            make.width.mas_equalTo(itemHeight);
            make.height.mas_equalTo(viewHeight);
        }];

        __weak __typeof(self) wself = self;
        for (int i = 0; i < maxItemNumber; i++) {
            LiveAddGuestsItemView *itemView = [[LiveAddGuestsItemView alloc] init];
            itemView.clickBlock = ^(LiveUserModel *_Nonnull userModel) {
                if (wself.clickGuestsBlock) {
                    wself.clickGuestsBlock(userModel);
                }
            };
            itemView.hidden = YES;
            [self.itemList addObject:itemView];
            [self.guestsView addSubview:itemView];
        }

        [self.itemList mas_distributeViewsAlongAxis:MASAxisTypeVertical
                                withFixedItemLength:itemHeight
                                        leadSpacing:0
                                        tailSpacing:0];
        [self.itemList mas_updateConstraints:^(MASConstraintMaker *make) {
            make.left.right.equalTo(self.guestsView);
        }];
    }
    return self;
}

#pragma mark - Publish Action

- (void)updateGuests:(NSArray<LiveUserModel *> *)userList {
    _userList = userList;

    // Host
    LiveUserModel *hostUserModel = [self getHostUserModel:userList];
    [self updateGuestsMic:hostUserModel.mic uid:self.hostID];
    [self updateGuestsCamera:hostUserModel.camera uid:self.hostID];
    [[LiveRTCManager shareRtc] bindCanvasViewToUid:self.hostID];
    UIView *rtcStreamView = [[LiveRTCManager shareRtc] getStreamViewWithUid:self.hostID];
    rtcStreamView.backgroundColor = [UIColor clearColor];
    rtcStreamView.hidden = NO;
    [self.streamingView addSubview:rtcStreamView];
    [rtcStreamView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.streamingView);
    }];

    // Guests
    _guestList = [self removeHostUserModel:userList];
    [self updateItemView];

    if ([self.hostID isEqualToString:[LocalUserComponent userModel].uid]) {
        // Only the streamer needs to update the confluence layout
        [[LiveRTCManager shareRtc] updateTranscodingLayout:userList
                                                 mixStatus:RTCMixStatusAddGuests
                                                 rtcRoomId:self.roomInfoModel.rtcRoomId];
    }
}

- (void)removeGuests:(NSString *)uid {
    LiveUserModel *deleteUserModel = nil;
    NSMutableArray *list = [_guestList mutableCopy];
    for (LiveUserModel *userModel in list) {
        if ([userModel.uid isEqualToString:uid]) {
            deleteUserModel = userModel;
            break;
        }
    }
    if (deleteUserModel) {
        [list removeObject:deleteUserModel];
        [self updateItemView];
        _guestList = [list copy];
    }
}

- (void)updateGuestsMic:(BOOL)mic uid:(NSString *)uid {
    if ([uid isEqualToString:self.hostID]) {
        self.micView.hidden = mic;

        CGFloat top = self.micView.hidden ? 75 + [DeviceInforTool getStatusBarHight] : 100 + [DeviceInforTool getStatusBarHight];
        [self.cameraView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(top);
        }];

        if ([uid isEqualToString:[LocalUserComponent userModel].uid]) {
            [[LiveRTCManager shareRtc] switchAudioCapture:mic];
        }
    } else {
        LiveAddGuestsItemView *model = nil;
        for (LiveAddGuestsItemView *itemView in self.itemList) {
            if ([itemView.userModel.uid isEqualToString:uid]) {
                model = itemView;
                break;
            }
        }
        if (model) {
            LiveUserModel *userModel = model.userModel;
            userModel.mic = mic;
            model.userModel = userModel;
        }
    }
}

- (void)updateGuestsCamera:(BOOL)camera uid:(NSString *)uid {
    if ([uid isEqualToString:self.hostID]) {
        self.cameraView.hidden = camera;
        self.noStreamingView.hidden = camera;
        self.streamingView.hidden = !camera;

        if ([uid isEqualToString:[LocalUserComponent userModel].uid]) {
            [[LiveRTCManager shareRtc] switchVideoCapture:camera];
        }
    } else {
        LiveAddGuestsItemView *model = nil;
        for (LiveAddGuestsItemView *itemView in self.itemList) {
            if ([itemView.userModel.uid isEqualToString:uid]) {
                model = itemView;
                break;
            }
        }
        if (model) {
            LiveUserModel *userModel = model.userModel;
            userModel.camera = camera;
            model.userModel = userModel;
        }
    }

    for (LiveUserModel *model in _userList) {
        if ([model.uid isEqualToString:uid]) {
            model.camera = camera;
            break;
        }
    }

    if ([self.hostID isEqualToString:[LocalUserComponent userModel].uid]) {
        // Only the streamer needs to update the confluence layout
        [[LiveRTCManager shareRtc] updateTranscodingLayout:_userList
                                                 mixStatus:RTCMixStatusAddGuests
                                                 rtcRoomId:self.roomInfoModel.rtcRoomId];
    }
}

- (void)updateNetworkQuality:(LiveNetworkQualityStatus)status uid:(NSString *)uid {
    if ([uid isEqualToString:self.hostID]) {
        if (status == LiveNetworkQualityStatusGood) {
            [self.netQualityView updateState:LiveIconStateNetQuality];
        } else if (status == LiveNetworkQualityStatusNone) {
            [self.netQualityView updateState:LiveIconStateHidden];
        } else {
            [self.netQualityView updateState:LiveIconStateNetQualityBad];
        }
    } else {
        LiveAddGuestsItemView *updateItemView = nil;
        for (LiveAddGuestsItemView *itemView in self.itemList) {
            if ([itemView.userModel.uid isEqualToString:uid]) {
                updateItemView = itemView;
                break;
            }
        }
        if (updateItemView) {
            [updateItemView updateNetworkQuality:status];
        }
    }
}

#pragma mark - Private Action

- (void)updateItemView {
    for (int i = 5; i >= 0; i--) {
        LiveAddGuestsItemView *itemView = self.itemList[i];
        NSInteger dataRow = 5 - i;
        if (dataRow < _guestList.count) {
            itemView.hidden = NO;
            itemView.userModel = _guestList[dataRow];
        } else {
            itemView.hidden = YES;
        }
    }
}

- (NSArray<LiveUserModel *> *)removeHostUserModel:(NSArray<LiveUserModel *> *)userModelList {
    NSMutableArray *mutableList = [userModelList mutableCopy];
    LiveUserModel *hostUserModel = nil;
    for (LiveUserModel *userModel in userModelList) {
        if ([userModel.uid isEqualToString:self.hostID]) {
            hostUserModel = userModel;
        }
    }
    if (hostUserModel) {
        [mutableList removeObject:hostUserModel];
    }
    return [mutableList copy];
}

- (LiveUserModel *)getHostUserModel:(NSArray<LiveUserModel *> *)userModelList {
    LiveUserModel *hostUserModel = nil;
    for (LiveUserModel *userModel in userModelList) {
        if ([userModel.uid isEqualToString:self.hostID]) {
            hostUserModel = userModel;
        }
    }
    return hostUserModel;
}

#pragma mark - Getter

- (UIView *)streamingView {
    if (!_streamingView) {
        _streamingView = [[UIView alloc] init];
        _streamingView.backgroundColor = [UIColor clearColor];
    }
    return _streamingView;
}

- (LiveNoStreamingView *)noStreamingView {
    if (!_noStreamingView) {
        _noStreamingView = [[LiveNoStreamingView alloc] init];
    }
    return _noStreamingView;
}

- (UIView *)guestsView {
    if (!_guestsView) {
        _guestsView = [[UIView alloc] init];
    }
    return _guestsView;
}

- (NSMutableArray<LiveAddGuestsItemView *> *)itemList {
    if (!_itemList) {
        _itemList = [[NSMutableArray alloc] init];
    }
    return _itemList;
}

- (LiveStateIconView *)netQualityView {
    if (!_netQualityView) {
        _netQualityView = [[LiveStateIconView alloc] initWithState:LiveIconStateHidden];
    }
    return _netQualityView;
}

- (LiveStateIconView *)micView {
    if (!_micView) {
        _micView = [[LiveStateIconView alloc] initWithState:LiveIconStateMic];
        _micView.hidden = YES;
    }
    return _micView;
}

- (LiveStateIconView *)cameraView {
    if (!_cameraView) {
        _cameraView = [[LiveStateIconView alloc] initWithState:LiveIconStateCamera];
        _cameraView.hidden = YES;
    }
    return _cameraView;
}

@end
