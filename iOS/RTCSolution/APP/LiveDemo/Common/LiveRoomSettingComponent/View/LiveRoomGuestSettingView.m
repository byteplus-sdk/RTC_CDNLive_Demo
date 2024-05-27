//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveRoomGuestSettingView.h"
#import "LiveRoomSettingResolutionView.h"
#import "LiveSettingItemButton.h"

@interface LiveRoomGuestSettingView ()
@property (nonatomic, strong) UILabel *titleLabel;

@property (nonatomic, strong) UIButton *backButton;

@property (nonatomic, strong) LiveRoomSettingResolutionView *resolutionView;

@property (nonatomic, strong) NSMutableArray *buttons;

@end

@implementation LiveRoomGuestSettingView

- (void)dealloc {
    NSLog(@"%@,%s", [NSThread currentThread], __func__);
}

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];

        [self addSubview:self.titleLabel];
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(self);
            make.top.mas_equalTo(16);
        }];

        NSArray *items = @[@[LocalizedString(@"resolution"), @"InteractiveLive_setting_resoluton", @""],
                           @[LocalizedString(@"camera_flip"), @"InteractiveLive_setting_switch_camera", @""],
                           @[LocalizedString(@"microphone"), @"InteractiveLive_setting_audio_enable", @"InteractiveLive_setting_audio_disable"],
                           @[LocalizedString(@"camera"), @"InteractiveLive_setting_video_enable", @"InteractiveLive_setting_video_disable"]];

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
            [self.buttons addObject:button];

            [button mas_makeConstraints:^(MASConstraintMaker *make) {
                make.centerX.equalTo(self).multipliedBy(0.5 * i + 0.25);
                make.top.equalTo(self.titleLabel.mas_bottom).offset(16);
                make.width.mas_equalTo(44);
                make.height.mas_equalTo(68);
            }];

            if (i == 0) {
                button.active = NO;
                button.alpha = 0.34;
                button.userInteractionEnabled = NO;
            }
        }

        [self addSubview:self.resolutionView];
        [self.resolutionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
    }
    return self;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
}

- (void)itemButtonClicked:(LiveSettingItemButton *)sender {
    sender.selected = !sender.selected;

    switch (sender.tag) {
        case 0: {
            // none
        } break;
        case 1: {
            if ([self.delegate respondsToSelector:@selector(liveRoomGuestSettingView:didSwitchCamera:)]) {
                [self.delegate liveRoomGuestSettingView:self didSwitchCamera:!sender.selected];
            }
        } break;
        case 2: {
            if ([self.delegate respondsToSelector:@selector(liveRoomGuestSettingView:didChangeMicState:)]) {
                [self.delegate liveRoomGuestSettingView:self didChangeMicState:sender.selected];
            }
        } break;
        case 3: {
            if ([self.delegate respondsToSelector:@selector(liveRoomGuestSettingView:didChangeCameraState:)]) {
                [self.delegate liveRoomGuestSettingView:self didChangeCameraState:sender.selected];
            }
        } break;
        default:
            break;
    }
}

- (void)setIsMicOn:(BOOL)isMicOn {
    _isMicOn = isMicOn;
    for (LiveSettingItemButton *button in self.buttons) {
        if (button.tag == 2) {
            button.selected = !isMicOn;
        }
    }
}

- (void)setIsCameraOn:(BOOL)isCameraOn {
    _isCameraOn = isCameraOn;
    for (LiveSettingItemButton *button in self.buttons) {
        if (button.tag == 3) {
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

- (UIButton *)backButton {
    if (!_backButton) {
        _backButton = [[UIButton alloc] init];
    }
    return _backButton;
}

- (LiveRoomSettingResolutionView *)resolutionView {
    if (!_resolutionView) {
        _resolutionView = [[LiveRoomSettingResolutionView alloc] init];
        _resolutionView.hidden = YES;
    }
    return _resolutionView;
}

- (NSMutableArray *)buttons {
    if (!_buttons) {
        _buttons = [[NSMutableArray alloc] init];
    }
    return _buttons;
}
@end
