// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveCreateRoomSettingView.h"
#import "LiveSettingBitrateView.h"
#import "LiveSettingSingleSelectView.h"

@interface LiveCreateRoomSettingView ()
@property (nonatomic, strong) UILabel *titleLabel;

@property (nonatomic, strong) LiveSettingSingleSelectView *fpsSelectView;
@property (nonatomic, strong) LiveSettingSingleSelectView *resolutoinSelectView;
@property (nonatomic, strong) LiveSettingBitrateView *bitrateSelectView;

@end

@implementation LiveCreateRoomSettingView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];

        [self addSubview:self.titleLabel];
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
          make.centerX.equalTo(self);
          make.top.mas_equalTo(16);
        }];

        [self addSubview:self.fpsSelectView];
        [self.fpsSelectView mas_makeConstraints:^(MASConstraintMaker *make) {
          make.left.right.equalTo(self);
          make.top.equalTo(self.titleLabel.mas_bottom).offset(24);
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
          make.top.equalTo(self.resolutoinSelectView.mas_bottom).offset(20);
          make.height.mas_equalTo(60);
        }];

        [self.bitrateSelectView setBitrateDidChangedBlock:^(NSInteger bitrate) {
          [wself bitrateDidChanged:bitrate];
        }];
    }
    return self;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
}

- (void)setVideoConfig:(LiveSettingVideoConfig *)videoConfig {
    _videoConfig = videoConfig;
    
    [self.fpsSelectView setSelectedIndex:videoConfig.fpsType];
    [self.resolutoinSelectView setSelectedIndex:videoConfig.resolutionType];

    self.bitrateSelectView.maxBitrate = videoConfig.maxBitrate;
    self.bitrateSelectView.minBitrate = videoConfig.minBitrate;
    self.bitrateSelectView.bitrate = videoConfig.bitrate;
}

- (void)fpsDidChanged:(NSInteger)index {
    self.videoConfig.fpsType = index;

    if ([self.delegate respondsToSelector:@selector(liveCreateRoomSettingView:didChangefpsType:)]) {
        [self.delegate liveCreateRoomSettingView:self didChangefpsType:index];
    }
}

- (void)resolutionDidChanged:(NSInteger)index {
    self.videoConfig.resolutionType = index;
    self.bitrateSelectView.maxBitrate = self.videoConfig.maxBitrate;
    self.bitrateSelectView.minBitrate = self.videoConfig.minBitrate;
    self.bitrateSelectView.bitrate = self.videoConfig.bitrate;

    if ([self.delegate respondsToSelector:@selector(liveCreateRoomSettingView:didChangeResolution:)]) {
        [self.delegate liveCreateRoomSettingView:self didChangeResolution:index];
    }
    if ([self.delegate respondsToSelector:@selector(liveCreateRoomSettingView:didChangeBitrate:)]) {
        [self.delegate liveCreateRoomSettingView:self didChangeBitrate:self.videoConfig.bitrate];
    }
}

- (void)bitrateDidChanged:(NSInteger)bitrate {
    self.videoConfig.bitrate = bitrate;
    if ([self.delegate respondsToSelector:@selector(liveCreateRoomSettingView:didChangeBitrate:)]) {
        [self.delegate liveCreateRoomSettingView:self didChangeBitrate:bitrate];
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

@end
