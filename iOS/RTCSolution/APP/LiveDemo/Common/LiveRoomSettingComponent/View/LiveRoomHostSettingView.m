// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveRoomHostSettingView.h"
#import "LiveSettingBitrateView.h"
#import "LiveSettingItemButton.h"
#import "LiveSettingSingleSelectView.h"

@interface LiveRoomHostSettingView ()
@property (nonatomic, strong) UILabel *titleLabel;

@property (nonatomic, strong) NSMutableArray *buttons;

@property (nonatomic, strong) UIView *lineView;

@property (nonatomic, strong) LiveSettingSingleSelectView *fpsSelectView;
@property (nonatomic, strong) LiveSettingSingleSelectView *resolutoinSelectView;
@property (nonatomic, strong) LiveSettingBitrateView *bitrateSelectView;

@end

@implementation LiveRoomHostSettingView

- (void)dealloc {
    NSLog(@"%@,%s", [NSThread currentThread], __func__);
}

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.allowChangeConfig = YES;

        self.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];

        [self addSubview:self.titleLabel];
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
          make.centerX.equalTo(self);
          make.top.mas_equalTo(12);
        }];

        NSArray *items = @[ @[ LocalizedString(@"camera_flip"), @"InteractiveLive_setting_switch_camera", @"" ],
                            @[ LocalizedString(@"microphone"), @"InteractiveLive_setting_audio_enable", @"InteractiveLive_setting_audio_disable" ],
                            @[ LocalizedString(@"camera"), @"InteractiveLive_setting_video_enable", @"InteractiveLive_setting_video_disable" ] ];

        for (int i = 0; i < items.count; i++) {
            NSString *title = items[i][0];
            NSString *imageNameNormal = items[i][1];
            NSString *imageNameSelected = items[i][2];

            LiveSettingItemButton *button = [[LiveSettingItemButton alloc] init];
            button.title = title;
            button.imageName = imageNameNormal;
            button.imageNameSelected = imageNameSelected;
            button.tag = i;

            [button addTarget:self action:@selector(itemButtonClicked:) forControlEvents:UIControlEventTouchUpInside];

            [self addSubview:button];

            [button mas_makeConstraints:^(MASConstraintMaker *make) {
              make.centerX.equalTo(self).multipliedBy(0.75 * i + 0.25);
              make.top.equalTo(self.titleLabel.mas_bottom).offset(16);
              make.width.mas_equalTo(44);
              make.height.mas_equalTo(68);
            }];

            [self.buttons addObject:button];
        }

        [self addSubview:self.lineView];
        [self.lineView mas_makeConstraints:^(MASConstraintMaker *make) {
          make.left.mas_equalTo(16);
          make.top.mas_equalTo(132);
          make.height.mas_equalTo(1);
          make.right.mas_equalTo(-16);
        }];

        [self addSubview:self.fpsSelectView];
        [self.fpsSelectView mas_makeConstraints:^(MASConstraintMaker *make) {
          make.left.right.equalTo(self);
          make.top.equalTo(self.lineView.mas_bottom).offset(0);
          make.height.mas_equalTo(60);
        }];

        __weak __typeof(self) wself = self;
        [self.fpsSelectView setItemChangeBlock:^(NSInteger index) {
          [wself fpsDidChanged:index];
        }];

        [self addSubview:self.resolutoinSelectView];
        [self.resolutoinSelectView mas_makeConstraints:^(MASConstraintMaker *make) {
          make.left.right.height.equalTo(self.fpsSelectView);
          make.top.equalTo(self.fpsSelectView.mas_bottom);
        }];

        [self.resolutoinSelectView setItemChangeBlock:^(NSInteger index) {
          [wself resolutionDidChanged:index];
        }];

        [self addSubview:self.bitrateSelectView];
        [self.bitrateSelectView mas_makeConstraints:^(MASConstraintMaker *make) {
          make.left.right.equalTo(self.fpsSelectView);
          make.top.equalTo(self.resolutoinSelectView.mas_bottom).offset(16);
          make.height.mas_equalTo(60);
        }];

        [self.bitrateSelectView setBitrateDidChangedBlock:^(NSInteger bitrate) {
          [wself bitrateDidChanged:bitrate];
        }];
    }
    return self;
}

- (void)setVideoConfig:(LiveSettingVideoConfig *)videoConfig {
    _videoConfig = videoConfig;
    [self.fpsSelectView setSelectedIndex:videoConfig.fpsType];
    [self.resolutoinSelectView setSelectedIndex:videoConfig.resolutionType];

    self.bitrateSelectView.maxBitrate = videoConfig.maxBitrate;
    self.bitrateSelectView.minBitrate = videoConfig.minBitrate;
    self.bitrateSelectView.bitrate = videoConfig.bitrate;
}

- (void)setIsSwitchCamera:(BOOL)isSwitchCamera {
    _isSwitchCamera = isSwitchCamera;
}

#pragma mark - Private Action
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
}

- (void)itemButtonClicked:(LiveSettingItemButton *)sender {
    sender.selected = !sender.selected;

    switch (sender.tag) {
        case 0: {
            if ([self.delegate respondsToSelector:@selector(liveRoomHostSettingView:didSwitchCamera:)]) {
                [self.delegate liveRoomHostSettingView:self didSwitchCamera:!sender.selected];
            }
        } break;
        case 1: {
            if ([self.delegate respondsToSelector:@selector(liveRoomHostSettingView:didChangeMicState:)]) {
                [self.delegate liveRoomHostSettingView:self didChangeMicState:sender.selected];
            }
        } break;
        case 2: {
            if ([self.delegate respondsToSelector:@selector(liveRoomHostSettingView:didChangeCameraState:)]) {
                [self.delegate liveRoomHostSettingView:self didChangeCameraState:sender.selected];
            }
        } break;
        default:
            break;
    }
}

- (void)fpsDidChanged:(NSInteger)index {
    self.videoConfig.fpsType = index;

    if ([self.delegate respondsToSelector:@selector(liveRoomHostSettingView:didChangefpsType:)]) {
        [self.delegate liveRoomHostSettingView:self didChangefpsType:index];
    }
}

- (void)resolutionDidChanged:(NSInteger)index {
    self.videoConfig.resolutionType = index;
    self.bitrateSelectView.maxBitrate = self.videoConfig.maxBitrate;
    self.bitrateSelectView.minBitrate = self.videoConfig.minBitrate;
    self.bitrateSelectView.bitrate = self.videoConfig.bitrate;

    if ([self.delegate respondsToSelector:@selector(liveRoomHostSettingView:didChangeResolution:)]) {
        [self.delegate liveRoomHostSettingView:self didChangeResolution:index];
    }
}

- (void)bitrateDidChanged:(NSInteger)bitrate {
    self.videoConfig.bitrate = bitrate;

    if ([self.delegate respondsToSelector:@selector(liveRoomHostSettingView:didChangeBitrate:)]) {
        [self.delegate liveRoomHostSettingView:self didChangeBitrate:bitrate];
    }
}

- (void)setAllowChangeConfig:(BOOL)allowChangeConfig {
    _allowChangeConfig = allowChangeConfig;

    if (allowChangeConfig) {
        CGFloat alpha = 1.0;
        self.fpsSelectView.alpha = alpha;
        self.fpsSelectView.userInteractionEnabled = YES;

        self.resolutoinSelectView.alpha = alpha;
        self.resolutoinSelectView.userInteractionEnabled = YES;

        self.bitrateSelectView.alpha = alpha;
        self.bitrateSelectView.userInteractionEnabled = YES;
    } else {
        CGFloat alpha = 0.34;
        self.fpsSelectView.alpha = alpha;
        self.fpsSelectView.userInteractionEnabled = NO;

        self.resolutoinSelectView.alpha = alpha;
        self.resolutoinSelectView.userInteractionEnabled = NO;

        self.bitrateSelectView.alpha = alpha;
        self.bitrateSelectView.userInteractionEnabled = NO;
    }
}

- (void)setIsMicOn:(BOOL)isMicOn {
    _isMicOn = isMicOn;
    for (LiveSettingItemButton *button in self.buttons) {
        if (button.tag == 1) {
            button.selected = !isMicOn;
        }
    }
}

- (void)setIsCameraOn:(BOOL)isCameraOn {
    _isCameraOn = isCameraOn;
    for (LiveSettingItemButton *button in self.buttons) {
        if (button.tag == 2) {
            button.selected = !isCameraOn;
        }
    }
}

#pragma mark - Getter

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.text = LocalizedString(@"settings");
        _titleLabel.font = [UIFont systemFontOfSize:16];
        _titleLabel.textColor = [UIColor colorFromHexString:@"#E5E6EB"];
    }
    return _titleLabel;
}

- (LiveSettingSingleSelectView *)fpsSelectView {
    if (!_fpsSelectView) {
        _fpsSelectView = [[LiveSettingSingleSelectView alloc] initWithTitle:LocalizedString(@"frame_rate") optionArray:@[ @"15", @"20" ]];
    }
    return _fpsSelectView;
}

- (LiveSettingSingleSelectView *)resolutoinSelectView {
    if (!_resolutoinSelectView) {
        _resolutoinSelectView = [[LiveSettingSingleSelectView alloc] initWithTitle:LocalizedString(@"resolution") optionArray:@[@"540p", @"720p", @"1080p" ]];
    }
    return _resolutoinSelectView;
}

- (LiveSettingBitrateView *)bitrateSelectView {
    if (!_bitrateSelectView) {
        _bitrateSelectView = [[LiveSettingBitrateView alloc] init];
    }
    return _bitrateSelectView;
}

- (UIView *)lineView {
    if (!_lineView) {
        _lineView = [[UIView alloc] init];
        _lineView.backgroundColor = [UIColor colorFromHexString:@"#5D626C"];
    }
    return _lineView;
}

- (NSMutableArray *)buttons {
    if (!_buttons) {
        _buttons = [[NSMutableArray alloc] init];
    }
    return _buttons;
}
@end
