//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveAvatarView.h"

@interface LiveAvatarView ()

@property (nonatomic, strong) UILabel *avatarLabel;
@property (nonatomic, strong) UIView *avatarBgView;

@end

@implementation LiveAvatarView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self addSubview:self.avatarBgView];
        [self.avatarBgView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];

        [self addSubview:self.avatarLabel];
        [self.avatarLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.center.equalTo(self.avatarBgView);
        }];
    }
    return self;
}

- (void)setFontSize:(NSInteger)fontSize {
    _fontSize = fontSize;

    self.avatarLabel.font = [UIFont systemFontOfSize:fontSize weight:UIFontWeightMedium];
}

- (void)setText:(NSString *)text {
    _text = text;

    if (NOEmptyStr(text)) {
        if (text.length > 0) {
            self.avatarLabel.text = [text substringToIndex:1];
        }
    }
}

#pragma mark - Getter

- (UIView *)avatarBgView {
    if (!_avatarBgView) {
        _avatarBgView = [[UIView alloc] init];
        _avatarBgView.backgroundColor = [UIColor colorFromHexString:@"#4E5969"];
    }
    return _avatarBgView;
}

- (UILabel *)avatarLabel {
    if (!_avatarLabel) {
        _avatarLabel = [[UILabel alloc] init];
        _avatarLabel.textColor = [UIColor colorFromHexString:@"#FFFFFF"];
    }
    return _avatarLabel;
}

@end
