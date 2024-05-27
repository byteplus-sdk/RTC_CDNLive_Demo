//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveAddGuestsItemView.h"
#import "LiveAvatarView.h"
#import "LiveStateIconView.h"

@interface LiveAddGuestsItemView ()

@property (nonatomic, strong) BaseButton *maskButton;
@property (nonatomic, strong) LiveStateIconView *netQualityView;
@property (nonatomic, strong) UIImageView *micImageView;
@property (nonatomic, strong) UILabel *userNameLabel;
@property (nonatomic, strong) LiveAvatarView *avatarComponent;
@property (nonatomic, strong) UIView *renderView;

@end

@implementation LiveAddGuestsItemView

- (instancetype)init {
    self = [super init];
    if (self) {
        self.layer.cornerRadius = 4;
        if (@available(iOS 11.0, *)) {
            self.layer.maskedCorners = YES;
        } else {
            self.layer.masksToBounds = YES;
        }
        self.backgroundColor = [UIColor colorFromHexString:@"#1D2129"];

        [self addSubview:self.renderView];
        [self.renderView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];

        [self addSubview:self.netQualityView];
        [self.netQualityView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.height.mas_equalTo(12);
            make.left.mas_equalTo(2);
            make.top.mas_equalTo(3);
            make.right.equalTo(self);
        }];

        [self addSubview:self.micImageView];
        [self.micImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(12, 12));
            make.left.mas_equalTo(2);
            make.bottom.mas_equalTo(-2);
        }];

        [self addSubview:self.userNameLabel];
        [self.userNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(16);
            make.bottom.mas_equalTo(-2);
            make.right.mas_lessThanOrEqualTo(self.mas_right).offset(-2);
        }];

        [self addSubview:self.avatarComponent];
        [self.avatarComponent mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(32, 32));
            make.center.equalTo(self);
        }];

        [self addSubview:self.maskButton];
        [self.maskButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
    }
    return self;
}

- (void)setUserModel:(LiveUserModel *)userModel {
    _userModel = userModel;

    [[LiveRTCManager shareRtc] bindCanvasViewToUid:userModel.uid];
    UIView *rtcStreamView = [[LiveRTCManager shareRtc] getStreamViewWithUid:userModel.uid];
    if (userModel.camera) {
        rtcStreamView.backgroundColor = [UIColor clearColor];
        [self.renderView addSubview:rtcStreamView];
        [rtcStreamView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.renderView);
        }];
        rtcStreamView.hidden = NO;
        self.avatarComponent.hidden = YES;
    } else {
        rtcStreamView.hidden = YES;
        self.avatarComponent.hidden = NO;
    }
    self.avatarComponent.text = userModel.name;
    self.userNameLabel.text = userModel.name;
    self.micImageView.hidden = userModel.mic;
    [self.userNameLabel mas_updateConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.micImageView.hidden ? 2 : 16);
    }];

    if ([userModel.uid isEqualToString:[LocalUserComponent userModel].uid]) {
        [[LiveRTCManager shareRtc] switchVideoCapture:userModel.camera];
        [[LiveRTCManager shareRtc] switchAudioCapture:userModel.mic];
    }
}

- (void)maskButtonAction {
    if (self.clickBlock) {
        self.clickBlock(self.userModel);
    }
}

- (void)updateNetworkQuality:(LiveNetworkQualityStatus)status {
    if (status == LiveNetworkQualityStatusGood) {
        [self.netQualityView updateState:LiveIconStateNetQuality];
    } else if (status == LiveNetworkQualityStatusNone) {
        [self.netQualityView updateState:LiveIconStateHidden];
    } else {
        [self.netQualityView updateState:LiveIconStateNetQualityBad];
    }
}

#pragma mark - Getter

- (BaseButton *)maskButton {
    if (!_maskButton) {
        _maskButton = [[BaseButton alloc] init];
        _maskButton.backgroundColor = [UIColor clearColor];
        [_maskButton addTarget:self action:@selector(maskButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _maskButton;
}

- (LiveStateIconView *)netQualityView {
    if (!_netQualityView) {
        _netQualityView = [[LiveStateIconView alloc] initWithState:LiveIconStateHidden];
    }
    return _netQualityView;
}

- (UIImageView *)micImageView {
    if (!_micImageView) {
        _micImageView = [[UIImageView alloc] init];
        _micImageView.image = [UIImage imageNamed:@"InteractiveLive_mic_icon" bundleName:HomeBundleName];
    }
    return _micImageView;
}

- (UILabel *)userNameLabel {
    if (!_userNameLabel) {
        _userNameLabel = [[UILabel alloc] init];
        _userNameLabel.textColor = [UIColor whiteColor];
        _userNameLabel.font = [UIFont systemFontOfSize:11];
    }
    return _userNameLabel;
}

- (LiveAvatarView *)avatarComponent {
    if (!_avatarComponent) {
        _avatarComponent = [[LiveAvatarView alloc] init];
        _avatarComponent.layer.cornerRadius = 16;
        _avatarComponent.layer.masksToBounds = YES;
        _avatarComponent.fontSize = 16;
        _avatarComponent.hidden = YES;
    }
    return _avatarComponent;
}

- (UIView *)renderView {
    if (!_renderView) {
        _renderView = [[UIView alloc] init];
    }
    return _renderView;
}

@end
