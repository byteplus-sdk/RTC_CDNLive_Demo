//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveAddGuestsApplyView.h"

@interface LiveAddGuestsApplyView ()

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) BaseButton *applyButton;

@end

@implementation LiveAddGuestsApplyView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self addSubview:self.titleLabel];
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(16);
            make.centerX.equalTo(self);
        }];

        [self addSubview:self.applyButton];
        [self.applyButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(132, 44));
            make.centerX.equalTo(self);
            make.top.equalTo(self.titleLabel.mas_bottom).offset(24);
        }];
    }
    return self;
}

- (void)setUserModel:(LiveUserModel *)userModel {
    _userModel = userModel;

    if (userModel.status == LiveInteractStatusApplying) {
        // Inviting
        self.applyButton.alpha = 0.34;
        self.applyButton.userInteractionEnabled = NO;
        [self.applyButton setTitle:LocalizedString(@"requesting") forState:UIControlStateNormal];
    } else {
        // None
        self.applyButton.alpha = 1;
        self.applyButton.userInteractionEnabled = YES;
        [self.applyButton setTitle:LocalizedString(@"request_live") forState:UIControlStateNormal];
    }
}

- (void)applyButtonAction {
    if (self.clickApplyBlock) {
        self.clickApplyBlock();
    }
}

#pragma mark - Publish Action

- (void)updateApplying {
    self.userModel.status = LiveInteractStatusApplying;
    [self setUserModel:self.userModel];
}

- (void)resetStatus {
    self.userModel.status = LiveInteractStatusOther;
    [self setUserModel:self.userModel];
}

#pragma mark - Getter

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.text = LocalizedString(@"live_interact_together");
        _titleLabel.font = [UIFont systemFontOfSize:16];
        _titleLabel.textColor = [UIColor whiteColor];
    }
    return _titleLabel;
}

- (BaseButton *)applyButton {
    if (!_applyButton) {
        _applyButton = [[BaseButton alloc] init];
        _applyButton.backgroundColor = [UIColor colorFromHexString:@"#4080FF"];
        [_applyButton setTitle:LocalizedString(@"request_live") forState:UIControlStateNormal];
        [_applyButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _applyButton.titleLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightRegular];
        [_applyButton addTarget:self action:@selector(applyButtonAction) forControlEvents:UIControlEventTouchUpInside];
        _applyButton.layer.cornerRadius = 22;
        _applyButton.layer.masksToBounds = YES;
    }
    return _applyButton;
}

@end
