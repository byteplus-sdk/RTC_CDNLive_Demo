//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveCreateRoomTipView.h"

@interface LiveCreateRoomTipView ()

@property (nonatomic, strong) UIImageView *tipImageView;
@property (nonatomic, strong) UILabel *tipLabel;
@property (nonatomic, strong) UIView *contentView;

@end

@implementation LiveCreateRoomTipView

- (instancetype)init {
    self = [super init];
    if (self) {
        self.backgroundColor = [UIColor clearColor];

        [self addSubview:self.contentView];
        [self addSubview:self.tipLabel];
        [self.tipLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(34);
            make.top.mas_equalTo(8);
            make.right.mas_lessThanOrEqualTo(-10);
        }];

        [self addSubview:self.tipImageView];
        [self.tipImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.mas_equalTo(11);
            make.width.height.mas_equalTo(14);
            make.centerY.equalTo(self.tipLabel);
        }];

        [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.tipLabel).offset(-34);
            make.top.equalTo(self.tipLabel).offset(-8);
            make.right.equalTo(self.tipLabel).offset(8);
            make.bottom.equalTo(self.tipLabel).offset(8);
        }];
    }
    return self;
}

- (void)setMessage:(NSString *)message {
    _message = message;

    self.tipLabel.text = message;

    [self mas_updateConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.contentView);
    }];
}

#pragma mark - Getter

- (UILabel *)tipLabel {
    if (!_tipLabel) {
        _tipLabel = [[UILabel alloc] init];
        _tipLabel.font = [UIFont systemFontOfSize:12];
        _tipLabel.numberOfLines = 0;
        _tipLabel.textColor = [UIColor whiteColor];
    }
    return _tipLabel;
}

- (UIView *)contentView {
    if (!_contentView) {
        _contentView = [[UIView alloc] init];
        _contentView.backgroundColor = [UIColor colorFromRGBHexString:@"#1D2129" andAlpha:0.8 * 255];
        _contentView.layer.cornerRadius = 8;
        _contentView.layer.masksToBounds = YES;
    }
    return _contentView;
}

- (UIImageView *)tipImageView {
    if (!_tipImageView) {
        _tipImageView = [[UIImageView alloc] init];
        _tipImageView.image = [UIImage imageNamed:@"InteractiveLive_warning" bundleName:HomeBundleName];
    }
    return _tipImageView;
}

@end
