//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveRoomAudienceCell.h"
#import "LiveRoomAudienceView.h"

@interface LiveRoomAudienceCell ()

@property (nonatomic, strong) LiveRoomAudienceView *audienceView;

@end

@implementation LiveRoomAudienceCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.contentView.backgroundColor = [UIColor clearColor];

        [self.contentView addSubview:self.audienceView];
        [self.audienceView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.contentView);
            make.left.equalTo(self.contentView).offset(18);
            make.right.equalTo(self.contentView).offset(-18);
            make.height.mas_equalTo(64 + 10);
            make.bottom.equalTo(self.contentView).offset(0);
        }];
    }
    return self;
}

- (void)setDataLists:(NSArray *)dataLists {
    _dataLists = dataLists;

    self.audienceView.dataLists = dataLists;
    NSInteger row = (dataLists.count / 4);
    NSInteger rowNumber = ((dataLists.count % 4) == 0) ? row : row + 1;
    [self.audienceView mas_updateConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo((64 + 10) * rowNumber);
    }];
}

- (LiveRoomAudienceView *)audienceView {
    if (!_audienceView) {
        _audienceView = [[LiveRoomAudienceView alloc] init];
        _audienceView.backgroundColor = [UIColor clearColor];
    }
    return _audienceView;
}

@end
