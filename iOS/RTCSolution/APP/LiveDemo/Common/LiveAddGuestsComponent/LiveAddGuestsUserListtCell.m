//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveAddGuestsUserListtCell.h"
#import "LiveAvatarView.h"

@interface LiveAddGuestsUserListtCell ()

@property (nonatomic, strong) UILabel *nameLabel;
@property (nonatomic, strong) BaseButton *rightButton;
@property (nonatomic, strong) LiveAvatarView *avatarView;

@end

@implementation LiveAddGuestsUserListtCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.contentView.backgroundColor = [UIColor clearColor];
        [self createUIComponent];
    }
    return self;
}

- (void)setModel:(LiveUserModel *)model {
    _model = model;
    self.nameLabel.text = model.name;
    self.avatarView.text = model.name;
    CGFloat buttonWidth = 74;

    if (model.status == LiveInteractStatusInviting) {
        [self.rightButton setTitle:LocalizedString(@"Initiate_send") forState:UIControlStateNormal];
        self.rightButton.backgroundColor = [UIColor colorFromRGBHexString:@"#4086FF"];
        self.rightButton.alpha = 0.5;
        self.rightButton.userInteractionEnabled = NO;
    } else if (model.status == LiveInteractStatusAudienceLink) {
        [self.rightButton setTitle:LocalizedString(@"connecting") forState:UIControlStateNormal];
        self.rightButton.backgroundColor = [UIColor colorFromRGBHexString:@"#4086FF"];
        self.rightButton.alpha = 0.5;
        self.rightButton.userInteractionEnabled = NO;
    } else {
        [self.rightButton setTitle:LocalizedString(@"invite") forState:UIControlStateNormal];
        self.rightButton.backgroundColor = [UIColor colorFromRGBHexString:@"#4086FF"];
        self.rightButton.alpha = 1.0;
        self.rightButton.userInteractionEnabled = YES;
    }
    [self.rightButton mas_updateConstraints:^(MASConstraintMaker *make) {
        make.width.mas_equalTo(buttonWidth);
    }];

    [self.rightButton.titleLabel mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.width.mas_equalTo(buttonWidth * 0.9);
        make.center.height.equalTo(self.rightButton);
    }];
}

- (void)createUIComponent {
    [self.contentView addSubview:self.avatarView];
    [self.avatarView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(40, 40));
        make.left.mas_equalTo(8);
        make.bottom.equalTo(self.contentView);
    }];

    [self.contentView addSubview:self.rightButton];
    [self.rightButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.mas_equalTo(74);
        make.height.mas_equalTo(28);
        make.right.mas_equalTo(-24);
        make.centerY.equalTo(self.avatarView);
    }];

    [self.contentView addSubview:self.nameLabel];
    [self.nameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.avatarView.mas_right).mas_offset(9);
        make.centerY.equalTo(self.avatarView);
        make.right.mas_lessThanOrEqualTo(self.rightButton.mas_left).offset(-9);
    }];
}

- (void)rightButtonAction:(BaseButton *)sender {
    if ([self.delegate respondsToSelector:@selector(liveAddGuestsUserListtCell:clickButton:)]) {
        [self.delegate liveAddGuestsUserListtCell:self clickButton:self.model];
    }
}

#pragma mark - Getter

- (BaseButton *)rightButton {
    if (!_rightButton) {
        _rightButton = [[BaseButton alloc] init];
        _rightButton.layer.cornerRadius = 2;
        _rightButton.layer.masksToBounds = YES;
        _rightButton.titleLabel.font = [UIFont systemFontOfSize:14];
        _rightButton.titleLabel.adjustsFontSizeToFitWidth = YES;
        _rightButton.titleLabel.textAlignment = NSTextAlignmentCenter;
        [_rightButton addTarget:self action:@selector(rightButtonAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _rightButton;
}

- (LiveAvatarView *)avatarView {
    if (!_avatarView) {
        _avatarView = [[LiveAvatarView alloc] init];
        _avatarView.layer.cornerRadius = 20;
        _avatarView.layer.masksToBounds = YES;
        _avatarView.fontSize = 20;
    }
    return _avatarView;
}

- (UILabel *)nameLabel {
    if (!_nameLabel) {
        _nameLabel = [[UILabel alloc] init];
        _nameLabel.textColor = [UIColor colorFromHexString:@"#E5E6EB"];
        _nameLabel.font = [UIFont systemFontOfSize:16];
    }
    return _nameLabel;
}

@end
