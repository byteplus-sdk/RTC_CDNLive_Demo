//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveRoomCell.h"
#import "LiveAvatarView.h"

@interface LiveRoomCell ()

@property (nonatomic, strong) UIView *bgView;
@property (nonatomic, strong) LiveAvatarView *avatarView;
@property (nonatomic, strong) UILabel *nameLabel;
@property (nonatomic, strong) UILabel *roomIDLabel;
@property (nonatomic, strong) UIImageView *livingImageView;

@end

@implementation LiveRoomCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.contentView.backgroundColor = [UIColor clearColor];
        [self createUIComponent];
    }
    return self;
}

- (void)setModel:(LiveRoomInfoModel *)model {
    _model = model;
    self.nameLabel.text = model.anchorUserName;
    self.avatarView.text = model.anchorUserName;
    // room_id
    self.roomIDLabel.text = [NSString stringWithFormat:@"%@:%@", LocalizedString(@"room_id"), model.roomID];
}

#pragma mark - Private Action

- (void)createUIComponent {
    [self.contentView addSubview:self.bgView];
    [self.bgView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(20);
        make.left.equalTo(self.contentView).offset(16);
        make.right.equalTo(self.contentView).offset(-16);
    }];

    [self.bgView addSubview:self.avatarView];
    [self.avatarView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.height.mas_equalTo(40);
        make.top.left.mas_equalTo(16);
    }];

    [self.bgView addSubview:self.nameLabel];
    [self.nameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.avatarView);
        make.left.equalTo(self.avatarView.mas_right).offset(9);
        make.right.mas_lessThanOrEqualTo(self.bgView.mas_right).offset(-15);
    }];

    [self.bgView addSubview:self.roomIDLabel];
    [self.roomIDLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(16);
        make.top.equalTo(self.avatarView.mas_bottom).offset(24);
        make.bottom.equalTo(self.bgView.mas_bottom).offset(-16);
    }];

    [self addSubview:self.livingImageView];
    [self.livingImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.roomIDLabel.mas_right).offset(8);
        make.centerY.equalTo(self.roomIDLabel);
    }];

    [self.bgView mas_updateConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.contentView);
    }];
}

#pragma mark - Getter

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
        _nameLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightRegular];
    }
    return _nameLabel;
}

- (UILabel *)roomIDLabel {
    if (!_roomIDLabel) {
        _roomIDLabel = [[UILabel alloc] init];
        _roomIDLabel.textColor = [UIColor colorFromHexString:@"#86909C"];
        _roomIDLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightRegular];
    }
    return _roomIDLabel;
}

- (UIView *)bgView {
    if (!_bgView) {
        _bgView = [[UIView alloc] init];
        _bgView.backgroundColor = [UIColor colorFromHexString:@"#394254"];
        _bgView.layer.cornerRadius = 20;
        _bgView.layer.masksToBounds = YES;
    }
    return _bgView;
}

- (UIImageView *)livingImageView {
    if (!_livingImageView) {
        _livingImageView = [[UIImageView alloc] init];
        _livingImageView.image = [UIImage imageNamed:@"InteractiveLive_living" bundleName:HomeBundleName];
    }
    return _livingImageView;
}
@end
