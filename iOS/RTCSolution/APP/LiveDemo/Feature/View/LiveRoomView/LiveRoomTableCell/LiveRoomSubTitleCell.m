// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveRoomSubTitleCell.h"

@interface LiveRoomSubTitleCell ()

@property (nonatomic, strong) UILabel *titleLabel;

@end

@implementation LiveRoomSubTitleCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.contentView.backgroundColor = [UIColor clearColor];

        [self.contentView addSubview:self.titleLabel];
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
          make.top.equalTo(self.contentView).offset(30);
          make.bottom.equalTo(self.contentView).offset(-20);
          make.left.equalTo(self.contentView).offset(20);
          make.right.equalTo(self.contentView).offset(-20);
        }];
    }
    return self;
}

- (void)setTitleStr:(NSString *)titleStr {
    _titleStr = titleStr;

    self.titleLabel.text = titleStr;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.text = LocalizedString(@"other_listeners");
        _titleLabel.textColor = [UIColor colorFromHexString:@"#86909C"];
        _titleLabel.font = [UIFont systemFontOfSize:16];
    }
    return _titleLabel;
}

@end
