// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveCoHostAudienceListsView.h"

@interface LiveCoHostAudienceListsView () <UITableViewDelegate, UITableViewDataSource, LiveCoHostUserListtCellDelegate>

@property (nonatomic, strong) UILabel *emptyLabel;
@property (nonatomic, strong) UITableView *roomTableView;

@end

@implementation LiveCoHostAudienceListsView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self addSubview:self.emptyLabel];
        [self.emptyLabel mas_makeConstraints:^(MASConstraintMaker *make) {
          make.center.equalTo(self);
        }];

        [self addSubview:self.roomTableView];
        [self.roomTableView mas_makeConstraints:^(MASConstraintMaker *make) {
          make.edges.equalTo(self);
        }];
    }
    return self;
}

#pragma mark - Publish Action

- (void)setDataLists:(NSArray *)dataLists {
    _dataLists = dataLists;

    self.emptyLabel.hidden = dataLists.count > 0 ? YES : NO;
    self.roomTableView.hidden = dataLists.count > 0 ? NO : YES;

    [self.roomTableView reloadData];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    LiveCoHostUserListtCell *cell = [tableView dequeueReusableCellWithIdentifier:@"LiveCoHostUserListtCellID" forIndexPath:indexPath];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.delegate = self;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 60;
}

#pragma mark - UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataLists.count;
}

#pragma mark - LiveCoHostUserListtCellDelegate

- (void)liveCoHostUserListtCell:(LiveCoHostUserListtCell *)liveCoHostUserListtCell clickButton:(id)model {
    if ([self.delegate respondsToSelector:@selector(liveCoHostAudienceListsView:clickButton:)]) {
        [self.delegate liveCoHostAudienceListsView:self clickButton:model];
    }
}

#pragma mark - Getter

- (UITableView *)roomTableView {
    if (!_roomTableView) {
        _roomTableView = [[UITableView alloc] init];
        _roomTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _roomTableView.delegate = self;
        _roomTableView.dataSource = self;
        _roomTableView.hidden = YES;
        [_roomTableView registerClass:LiveCoHostUserListtCell.class forCellReuseIdentifier:@"LiveCoHostUserListtCellID"];
        _roomTableView.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];
    }
    return _roomTableView;
}

- (UILabel *)emptyLabel {
    if (!_emptyLabel) {
        _emptyLabel = [[UILabel alloc] init];
        _emptyLabel.textColor = [UIColor colorFromHexString:@"#86909C"];
        _emptyLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightMedium];
        _emptyLabel.text = LocalizedString(@"host_arriving_soon");
        _emptyLabel.hidden = YES;
    }
    return _emptyLabel;
}

- (void)dealloc {
    NSLog(@"dealloc %@", NSStringFromClass([self class]));
}

@end
