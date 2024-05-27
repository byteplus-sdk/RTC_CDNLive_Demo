//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveCoHostRoomView.h"
#import "LiveCoHostRoomItemView.h"

@interface LiveCoHostRoomView ()

@property (nonatomic, strong) LiveCoHostRoomItemView *ownItemView;
@property (nonatomic, strong) LiveCoHostRoomItemView *otherItemView;
@property (nonatomic, strong) UIImageView *iconImageView;
@property (nonatomic, strong) UILabel *connectingLabel;

@end

@implementation LiveCoHostRoomView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self addSubview:self.ownItemView];
        [self.ownItemView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(self).multipliedBy(0.5);
            make.left.bottom.height.equalTo(self);
        }];

        [self addSubview:self.otherItemView];
        [self.otherItemView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(self).multipliedBy(0.5);
            make.right.bottom.height.equalTo(self);
        }];

        [self addSubview:self.iconImageView];
        [self.iconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(90, 20));
            make.top.centerX.equalTo(self);
        }];

        [self.iconImageView addSubview:self.connectingLabel];
        [self.connectingLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.mas_equalTo(66);
            make.height.centerX.equalTo(self.iconImageView);
        }];
    }
    return self;
}

#pragma mark - Publish Action

- (void)setUserModelList:(NSArray<LiveUserModel *> *)userModelList {
    _userModelList = userModelList;

    LiveUserModel *userModel = nil;
    LiveUserModel *otherModel = nil;
    for (LiveUserModel *tempUserModel in userModelList) {
        if ([tempUserModel.uid isEqualToString:[LocalUserComponent userModel].uid]) {
            userModel = tempUserModel;
        } else {
            otherModel = tempUserModel;
        }
    }
    self.ownItemView.userModel = userModel;
    self.otherItemView.userModel = otherModel;
}

- (void)updateGuestsMic:(BOOL)mic uid:(NSString *)uid {
    if ([uid isEqualToString:[LocalUserComponent userModel].uid]) {
        LiveUserModel *userModel = self.ownItemView.userModel;
        userModel.mic = mic;
        self.ownItemView.userModel = userModel;
        [[LiveRTCManager shareRtc] switchAudioCapture:mic];
    } else {
        LiveUserModel *userModel = self.otherItemView.userModel;
        userModel.mic = mic;
        self.otherItemView.userModel = userModel;
    }
}

- (void)updateGuestsCamera:(BOOL)camera uid:(NSString *)uid {
    if ([uid isEqualToString:[LocalUserComponent userModel].uid]) {
        LiveUserModel *userModel = self.ownItemView.userModel;
        userModel.camera = camera;
        self.ownItemView.userModel = userModel;
        [[LiveRTCManager shareRtc] switchVideoCapture:camera];
    } else {
        LiveUserModel *userModel = self.otherItemView.userModel;
        userModel.camera = camera;
        self.otherItemView.userModel = userModel;
    }
}

- (void)updateNetworkQuality:(LiveNetworkQualityStatus)status uid:(NSString *)uid {
    if ([uid isEqualToString:[LocalUserComponent userModel].uid]) {
        [self.ownItemView updateNetworkQuality:status];
    } else {
        [self.otherItemView updateNetworkQuality:status];
    }
}

#pragma mark - Getter

- (UILabel *)connectingLabel {
    if (!_connectingLabel) {
        _connectingLabel = [[UILabel alloc] init];
        _connectingLabel.font = [UIFont systemFontOfSize:12];
        _connectingLabel.textColor = [UIColor whiteColor];
        _connectingLabel.text = LocalizedString(@"co-host_connecting_message");
        _connectingLabel.adjustsFontSizeToFitWidth = YES;
    }
    return _connectingLabel;
}

- (LiveCoHostRoomItemView *)ownItemView {
    if (!_ownItemView) {
        _ownItemView = [[LiveCoHostRoomItemView alloc] initWithIsOwn:YES];
    }
    return _ownItemView;
}

- (LiveCoHostRoomItemView *)otherItemView {
    if (!_otherItemView) {
        _otherItemView = [[LiveCoHostRoomItemView alloc] initWithIsOwn:NO];
    }
    return _otherItemView;
}

- (UIImageView *)iconImageView {
    if (!_iconImageView) {
        _iconImageView = [[UIImageView alloc] init];
        _iconImageView.image = [UIImage imageNamed:@"InteractiveLive_cohost" bundleName:HomeBundleName];
    }
    return _iconImageView;
}

@end
