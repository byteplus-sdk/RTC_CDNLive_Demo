//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveRoomAudienceSettingView.h"
#import "LiveRoomSettingResolutionView.h"
#import "LiveSettingItemButton.h"
#import "LiveSettingVideoConfig.h"

@interface LiveRoomAudienceSettingView ()
@property (nonatomic, strong) UILabel *titleLabel;

@property (nonatomic, strong) UIButton *backButton;

@property (nonatomic, strong) LiveRoomSettingResolutionView *resolutionView;
@end

@implementation LiveRoomAudienceSettingView

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
                           @[LocalizedString(@"camera_flip"), @"InteractiveLive_setting_camera_audience", @""],
                           @[LocalizedString(@"microphone"), @"InteractiveLive_setting_audio_audience", @""],
                           @[LocalizedString(@"camera"), @"InteractiveLive_setting_video_audience", @""]];

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
                make.centerX.equalTo(self).multipliedBy(0.5 * i + 0.25);
                make.top.equalTo(self.titleLabel.mas_bottom).offset(16);
                make.width.mas_equalTo(44);
                make.height.mas_equalTo(68);
            }];

            if (i != 0) {
                button.active = NO;
                button.alpha = 0.34;
                button.userInteractionEnabled = NO;
            }
        }

        [self addSubview:self.resolutionView];
        [self.resolutionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];

        __weak __typeof(self) wself = self;
        [self.resolutionView setResolutionChangeBlock:^(NSInteger index) {
            if ([wself.delegate respondsToSelector:@selector(liveRoomAudienceSettingView:didChangeResolution:)]) {
                [wself.delegate liveRoomAudienceSettingView:wself didChangeResolution:index];
            }
        }];

        [self.resolutionView setSelectedResKey:[LiveSettingVideoConfig defultResPullKey]];
    }
    return self;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
}

- (void)itemButtonClicked:(LiveSettingItemButton *)sender {
    if (sender.tag == 0) {
        self.resolutionView.hidden = NO;
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

@end
