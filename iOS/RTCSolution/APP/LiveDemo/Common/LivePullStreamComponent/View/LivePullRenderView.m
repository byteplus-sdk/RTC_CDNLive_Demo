// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LivePullRenderView.h"
#import "LiveHostAvatarView.h"
#import "LiveNoStreamingView.h"
#import "LiveRTCManager.h"
#import "LiveStateIconView.h"

@interface LivePullRenderView ()

@property (nonatomic, strong) LiveNoStreamingView *noStreamingView;
@property (nonatomic, strong) UIView *liveView;
@property (nonatomic, strong) UIView *streamView;
@property (nonatomic, strong) UIImageView *iconImageView;
@property (nonatomic, strong) UIImageView *topMaskImageView;
@property (nonatomic, strong) UIView *coHostMaskView;
@property (nonatomic, assign) BOOL hasAddItemLayer;
@property (nonatomic, strong) UILabel *connectingLabel;
@property (nonatomic, assign) BOOL curHostCamera;
@property (nonatomic, assign) BOOL curHostMic;

@end

@implementation LivePullRenderView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self addSubview:self.noStreamingView];
        [self.noStreamingView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
        
        [self addSubview:self.streamView];
        [self.streamView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
        
        [self.streamView addSubview:self.coHostMaskView];
        [self.coHostMaskView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.streamView);
        }];
        
        [self.streamView addSubview:self.liveView];
        [self.liveView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.streamView);
        }];
        
        [self.streamView addSubview:self.topMaskImageView];
        [self.topMaskImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.right.top.equalTo(self.streamView);
            make.height.mas_equalTo(42);
        }];
        
        [self.streamView addSubview:self.iconImageView];
        [self.iconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(90, 20));
            make.top.centerX.equalTo(self.streamView);
        }];
        
        [self.iconImageView addSubview:self.connectingLabel];
        [self.connectingLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.mas_equalTo(66);
            make.height.centerX.equalTo(self.iconImageView);
        }];
    }
    return self;
}

- (void)setStatus:(PullRenderStatus)status {
    if (_status != status) {
        _status = status;
        
        [self addSubview:self.streamView];
        if (status == PullRenderStatusCoHst) {
            [self.streamView mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.width.equalTo(self);
                make.height.mas_equalTo(ceilf((SCREEN_WIDTH / 2) * 16 / 9));
                make.center.equalTo(self);
            }];
            self.iconImageView.hidden = NO;
            self.topMaskImageView.hidden = NO;
            self.coHostMaskView.hidden = NO;
            self.noStreamingView.hidden = YES;
            self.streamView.hidden = NO;
        } else {
            [self.streamView mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.edges.equalTo(self);
            }];
            self.iconImageView.hidden = YES;
            self.topMaskImageView.hidden = YES;
            self.coHostMaskView.hidden = YES;
            [self updateHostMic:self.curHostMic
                         camera:self.curHostCamera];
        }
        self.streamView.alpha = 0;
        [UIView animateWithDuration:0.3
                              delay:0.2
                            options:UIViewAnimationOptionCurveEaseInOut
                         animations:^{
            self.streamView.alpha = 1;
        } completion:^(BOOL finished) {
            self.streamView.alpha = 1;
        }];
    }
}

#pragma mark - Publish Action

- (void)updateHostMic:(BOOL)mic camera:(BOOL)camera {
    self.curHostCamera = camera;
    self.curHostMic = mic;
    if (self.status == PullRenderStatusNone) {
        // Single anchor & audience mic mode
        self.noStreamingView.hidden = camera;
        self.streamView.hidden = !camera;
    } else {
        // PK mode
        self.noStreamingView.hidden = YES;
        self.streamView.hidden = NO;
    }
}

- (void)setUserName:(NSString *)userName {
    [self.noStreamingView setUserName:userName];
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

- (LiveNoStreamingView *)noStreamingView {
    if (!_noStreamingView) {
        _noStreamingView = [[LiveNoStreamingView alloc] init];
        _noStreamingView.hidden = YES;
    }
    return _noStreamingView;
}

- (UIView *)streamView {
    if (!_streamView) {
        _streamView = [[UIView alloc] init];
        _streamView.backgroundColor = [UIColor clearColor];
    }
    return _streamView;
}

- (UIView *)liveView {
    if (!_liveView) {
        _liveView = [[UIView alloc] init];
        _liveView.backgroundColor = [UIColor clearColor];
    }
    return _liveView;
}

- (UIImageView *)iconImageView {
    if (!_iconImageView) {
        _iconImageView = [[UIImageView alloc] init];
        _iconImageView.image = [UIImage imageNamed:@"InteractiveLive_cohost" bundleName:HomeBundleName];
        _iconImageView.hidden = YES;
    }
    return _iconImageView;
}

- (UIImageView *)topMaskImageView {
    if (!_topMaskImageView) {
        _topMaskImageView = [[UIImageView alloc] init];
        _topMaskImageView.image = [UIImage imageNamed:@"InteractiveLive_top_mask" bundleName:HomeBundleName];
        _topMaskImageView.hidden = YES;
    }
    return _topMaskImageView;
}

- (UIView *)coHostMaskView {
    if (!_coHostMaskView) {
        _coHostMaskView = [[UIView alloc] init];
        _coHostMaskView.backgroundColor = [UIColor colorFromHexString:@"#30394A"];
        _coHostMaskView.hidden = YES;
    }
    return _coHostMaskView;
}

@end
