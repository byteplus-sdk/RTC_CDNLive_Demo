// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveNoStreamingView.h"
#import "LiveAvatarView.h"
#import "LiveStateIconView.h"

@interface LiveNoStreamingView ()

@property (nonatomic, strong) UIView *bgMaskView;
@property (nonatomic, strong) LiveAvatarView *avatarComponent;
@property (nonatomic, assign) BOOL hasAddItemLayer;

@end

@implementation LiveNoStreamingView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self addSubview:self.bgMaskView];
        [self.bgMaskView mas_makeConstraints:^(MASConstraintMaker *make) {
          make.edges.equalTo(self);
        }];

        [self addSubview:self.avatarComponent];
        [self.avatarComponent mas_makeConstraints:^(MASConstraintMaker *make) {
          make.width.height.mas_equalTo(160);
          make.center.equalTo(self);
        }];
        self.avatarComponent.text = @"B";
    }
    return self;
}

- (void)setUserName:(NSString *)userName {
    self.avatarComponent.text = userName;
}

- (void)layoutSubviews {
    [super layoutSubviews];

    if (_hasAddItemLayer == NO) {
        [self addBgGradientLayer];
        _hasAddItemLayer = YES;
    }
}

- (void)addBgGradientLayer {
    UIColor *startColor = [UIColor colorFromHexString:@"#30394A"];
    UIColor *endColor = [UIColor colorFromRGBHexString:@"#1D2129"];
    CAGradientLayer *gradientLayer = [CAGradientLayer layer];
    gradientLayer.frame = self.bgMaskView.bounds;
    gradientLayer.colors = @[ (__bridge id)[startColor colorWithAlphaComponent:1.0].CGColor,
                              (__bridge id)[endColor colorWithAlphaComponent:1.0].CGColor ];
    gradientLayer.startPoint = CGPointMake(.0, .0);
    gradientLayer.endPoint = CGPointMake(1.0, 1.0);
    [self.bgMaskView.layer addSublayer:gradientLayer];
}

- (LiveAvatarView *)avatarComponent {
    if (!_avatarComponent) {
        _avatarComponent = [[LiveAvatarView alloc] init];
        _avatarComponent.layer.cornerRadius = 80;
        _avatarComponent.layer.masksToBounds = YES;
        _avatarComponent.fontSize = 80;
    }
    return _avatarComponent;
}

- (UIView *)bgMaskView {
    if (!_bgMaskView) {
        _bgMaskView = [[UIView alloc] init];
    }
    return _bgMaskView;
}

@end
