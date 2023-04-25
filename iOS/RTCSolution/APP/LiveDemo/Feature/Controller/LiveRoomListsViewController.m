// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveRoomListsViewController.h"
#import "LiveCreateRoomViewController.h"
#import "LiveRoomHostSettingView.h"
#import "LiveRoomTableView.h"
#import "LiveRoomViewController.h"

@interface LiveRoomListsViewController () <LiveRoomTableViewDelegate>

@property (nonatomic, strong) UIButton *createButton;
@property (nonatomic, strong) LiveRoomTableView *roomTableView;
@property (nonatomic, strong) UILabel *noDataLabel;
@property (nonatomic, copy) NSString *currentAppid;

@end

@implementation LiveRoomListsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navView.backgroundColor = [UIColor clearColor];
    
    [self.view addSubview:self.createButton];
    [self.createButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(170, 50));
        make.centerX.equalTo(self.view);
        make.bottom.equalTo(self.view).offset(-20 - [DeviceInforTool getVirtualHomeHeight]);
    }];
    
    [self.view addSubview:self.roomTableView];
    [self.roomTableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.top.equalTo(self.navView.mas_bottom);
        make.bottom.equalTo(self.createButton.mas_top).offset(-20);
    }];
    
    [self.view addSubview:self.noDataLabel];
    [self.noDataLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.equalTo(self.view);
    }];
    
    [[LivePlayerManager sharePlayer] startWithConfiguration];
    [self loadDataWithGetLists];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    self.navTitle = LocalizedString(@"interactive_live");
    self.navRightImage = [UIImage imageNamed:@"refresh" bundleName:HomeBundleName];
}

- (void)rightButtonAction:(BaseButton *)sender {
    [super rightButtonAction:sender];
    
    [self loadDataWithGetLists];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

#pragma mark - load data

- (void)loadDataWithGetLists {
    __weak __typeof(self) wself = self;
    [[ToastComponent shareToastComponent] showLoading];
    [LiveRTSManager liveClearUserWithBlock:^(RTSACKModel * _Nonnull model) {
        [LiveRTSManager liveGetActiveLiveRoomListWithBlock:^(NSArray<LiveRoomInfoModel *> *roomList, RTSACKModel *model) {
            [[ToastComponent shareToastComponent] dismiss];
            if (model.result) {
                wself.roomTableView.dataLists = roomList;
                if (roomList.count == 0) {
                    wself.noDataLabel.hidden = NO;
                } else {
                    wself.noDataLabel.hidden = YES;
                }
            } else {
                wself.noDataLabel.hidden = NO;
            }
        }];
    }];
}

#pragma mark - LiveRoomTableViewDelegate

- (void)LiveRoomTableView:(LiveRoomTableView *)LiveRoomTableView didSelectRowAtIndexPath:(LiveRoomInfoModel *)model {
    [PublicParameterComponent share].roomId = model.roomID;
    LiveRoomViewController *next = [[LiveRoomViewController alloc]
                                    initWithRoomModel:model
                                    streamPushUrl:@""];
    [self.navigationController pushViewController:next animated:YES];
    __weak __typeof(self) wself = self;
    next.hangUpBlock = ^(BOOL result) {
        [wself loadDataWithGetLists];
    };
}

#pragma mark - Touch Action

- (void)createButtonAction:(UIButton *)sender {
    sender.userInteractionEnabled = NO;
    LiveCreateRoomViewController *next = [[LiveCreateRoomViewController alloc] init];
    [self.navigationController pushViewController:next animated:YES];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.25 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        sender.userInteractionEnabled = YES;
    });
}

#pragma mark - Getter

- (UIButton *)createButton {
    if (!_createButton) {
        _createButton = [[UIButton alloc] init];
        _createButton.backgroundColor = [UIColor colorFromHexString:@"#4080FF"];
        [_createButton addTarget:self action:@selector(createButtonAction:) forControlEvents:UIControlEventTouchUpInside];
        _createButton.layer.cornerRadius = 25;
        _createButton.layer.masksToBounds = YES;
        
        UIView *contentView = [[UIView alloc] init];
        contentView.backgroundColor = [UIColor clearColor];
        [_createButton addSubview:contentView];
        [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.center.equalTo(_createButton);
        }];
        
        UIImageView *iconImageView = [[UIImageView alloc] init];
        iconImageView.image = [UIImage imageNamed:@"InteractiveLive_add" bundleName:HomeBundleName];
        [contentView addSubview:iconImageView];
        [iconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(16, 16));
            make.centerY.equalTo(_createButton);
            make.left.equalTo(contentView);
        }];
        
        UILabel *titleLabel = [[UILabel alloc] init];
        titleLabel.text = LocalizedString(@"create_live");
        titleLabel.textColor = [UIColor whiteColor];
        titleLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightRegular];
        [contentView addSubview:titleLabel];
        [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(_createButton);
            make.left.equalTo(iconImageView.mas_right).offset(10);
            make.right.equalTo(contentView);
        }];
    }
    return _createButton;
}

- (LiveRoomTableView *)roomTableView {
    if (!_roomTableView) {
        _roomTableView = [[LiveRoomTableView alloc] init];
        _roomTableView.delegate = self;
    }
    return _roomTableView;
}

- (UILabel *)noDataLabel {
    if (!_noDataLabel) {
        _noDataLabel = [[UILabel alloc] init];
        _noDataLabel.text = LocalizedString(@"no_live");
        _noDataLabel.textColor = [UIColor colorFromHexString:@"#86909C"];
        _noDataLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightRegular];
        _noDataLabel.hidden = YES;
    }
    return _noDataLabel;
}

- (void)dealloc {
    [[LiveRTCManager shareRtc] disconnect];
    [PublicParameterComponent clear];
}

@end
