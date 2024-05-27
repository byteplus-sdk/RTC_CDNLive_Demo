//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveCreateRoomViewController.h"
#import "BytedEffectProtocol.h"
#import "LiveCreateRoomControlView.h"
#import "LiveCreateRoomTipView.h"
#import "LiveRTCManager.h"
#import "LiveRoomSettingComponent.h"
#import "LiveRoomViewController.h"

@interface LiveCreateRoomViewController () <LiveCreateRoomControlViewDelegate>
@property (nonatomic, strong) LiveCreateRoomTipView *tipView;
@property (nonatomic, strong) UIView *renderView;

@property (nonatomic, strong) UIButton *startButton;

@property (nonatomic, strong) LiveCreateRoomControlView *controlView;
@property (nonatomic, strong) LiveRoomSettingComponent *settingComponent;
@property (nonatomic, strong) BytedEffectProtocol *beautyComponent;

@property (nonatomic, strong) LiveRoomInfoModel *roomInfoModel;
@property (nonatomic, copy) NSString *pushUrl;

@end

@implementation LiveCreateRoomViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];
    self.navView.backgroundColor = [UIColor clearColor];
    [self loadDataWithCreateLive];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)startButtonAction:(UIButton *)sender {
    [[ToastComponent shareToastComponent] showLoading];
    __weak __typeof(self) wself = self;
    [PublicParameterComponent share].roomId = self.roomInfoModel.roomID;
    [LiveRTSManager liveStartLive:self.roomInfoModel.roomID
                            block:^(LiveUserModel *hostUserModel,
                                    RTSACKModel *_Nonnull model) {
                                if (model.result) {
                                    self.roomInfoModel.hostUserModel = hostUserModel;
                                    LiveRoomViewController *next = [[LiveRoomViewController alloc]
                                        initWithRoomModel:self.roomInfoModel
                                            streamPushUrl:wself.pushUrl];
                                    next.settingComponent = wself.settingComponent;
                                    [wself.navigationController pushViewController:next animated:YES];
                                } else {
                                    [[ToastComponent shareToastComponent] showWithMessage:model.message];
                                }
                                [[ToastComponent shareToastComponent] dismiss];
                            }];
}

#pragma mark - Private Action

- (void)addSubviewAndConstraints {
    [self.view addSubview:self.renderView];
    [self.renderView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];

    [self.view bringSubviewToFront:self.navView];

    [self.view addSubview:self.tipView];
    [self.tipView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(48);
        make.centerY.equalTo(self.navView.mas_bottom).offset(10);
        make.right.equalTo(self.view);
    }];

    [self.view addSubview:self.startButton];
    [self.startButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(170, 50));
        make.centerX.equalTo(self.view);
        make.bottom.equalTo(self.view).offset(-20 - [DeviceInforTool getVirtualHomeHeight]);
    }];

    [self.view addSubview:self.controlView];
    [self.controlView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.startButton.mas_top).offset(-10);
        make.centerX.equalTo(self.view);
        make.width.mas_equalTo(200);
        make.height.mas_equalTo(85);
    }];
}

- (void)loadDataWithCreateLive {
    __weak __typeof(self) wself = self;
    [[ToastComponent shareToastComponent] showLoading];
    [LiveRTSManager liveCreateLive:[LocalUserComponent userModel].name
                             block:^(LiveRoomInfoModel *roomInfoModel, LiveUserModel *hostUserModel, NSString *pushUrl, RTSACKModel *model) {
                                 [[ToastComponent shareToastComponent] dismiss];
                                 if (model.result) {
                                     wself.roomInfoModel = roomInfoModel;
                                     wself.pushUrl = pushUrl;
                                     [wself addSubviewAndConstraints];
                                     [wself setupLocalRenderView];
                                 } else {
                                     AlertActionModel *alertModel = [[AlertActionModel alloc] init];
                                     alertModel.title = LocalizedString(@"ok");
                                     alertModel.alertModelClickBlock = ^(UIAlertAction *_Nonnull action) {
                                         if ([action.title isEqualToString:LocalizedString(@"ok")]) {
                                             [wself.navigationController popViewControllerAnimated:YES];
                                         }
                                     };
                                     [[AlertActionManager shareAlertActionManager] showWithMessage:model.message actions:@[alertModel]];
                                 }
                             }];
}

- (void)setupLocalRenderView {
    [[LiveRTCManager shareRtc] switchVideoCapture:YES];
    [[LiveRTCManager shareRtc] switchAudioCapture:YES];
    [[LiveRTCManager shareRtc] bindCanvasViewToUid:[LocalUserComponent userModel].uid];

    UIView *rtcStreamView = [[LiveRTCManager shareRtc] getStreamViewWithUid:[LocalUserComponent userModel].uid];
    rtcStreamView.hidden = NO;
    [self.renderView addSubview:rtcStreamView];
    [rtcStreamView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.renderView);
    }];

    // add effect
    [self.beautyComponent resume];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [_settingComponent close];
}

#pragma mark - LiveCreateRoomControlViewDelegate

- (void)liveCreateRoomControlView:(LiveCreateRoomControlView *)liveCreateRoomControlView didClickedSwitchCameraButton:(UIButton *)button {
    [[LiveRTCManager shareRtc] switchCamera];
}

- (void)liveCreateRoomControlView:(LiveCreateRoomControlView *)liveCreateRoomControlView didClickedBeautyButton:(UIButton *)button {
    if (self.beautyComponent) {
        __weak __typeof(self) wself = self;
        self.controlView.hidden = YES;
        [self.beautyComponent showWithView:self.view
                              dismissBlock:^(BOOL result) {
                                  wself.controlView.hidden = NO;
                              }];
    } else {
        [[ToastComponent shareToastComponent] showWithMessage:LocalizedString(@"not_support_beauty_error")];
    }
}

- (void)liveCreateRoomControlView:(LiveCreateRoomControlView *)liveCreateRoomControlView didClickedSettingButton:(UIButton *)button {
    [self.settingComponent showWithType:LiveRoomSettingTypeCreateRoom
                          fromSuperView:self.view
                                 roomID:self.roomInfoModel
                              userModel:nil];
}

#pragma mark - Getter

- (UIButton *)startButton {
    if (!_startButton) {
        _startButton = [[UIButton alloc] init];
        _startButton.backgroundColor = [UIColor colorFromHexString:@"#4080FF"];
        [_startButton setTitle:LocalizedString(@"go_live") forState:UIControlStateNormal];
        [_startButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _startButton.titleLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightRegular];
        [_startButton addTarget:self action:@selector(startButtonAction:) forControlEvents:UIControlEventTouchUpInside];
        _startButton.layer.cornerRadius = 25;
        _startButton.layer.masksToBounds = YES;
    }
    return _startButton;
}

- (LiveCreateRoomTipView *)tipView {
    if (!_tipView) {
        _tipView = [[LiveCreateRoomTipView alloc] init];
        _tipView.message = [NSString stringWithFormat:LocalizedString(@"application_experiencing_%@_title"), @"20"];
    }
    return _tipView;
}

- (UIView *)renderView {
    if (!_renderView) {
        _renderView = [[UIView alloc] init];
    }
    return _renderView;
}

- (LiveCreateRoomControlView *)controlView {
    if (!_controlView) {
        _controlView = [[LiveCreateRoomControlView alloc] init];
        _controlView.delegate = self;
    }
    return _controlView;
}

- (LiveRoomSettingComponent *)settingComponent {
    if (!_settingComponent) {
        _settingComponent = [[LiveRoomSettingComponent alloc] init];
    }
    return _settingComponent;
}

- (BytedEffectProtocol *)beautyComponent {
    if (!_beautyComponent) {
        _beautyComponent = [[BytedEffectProtocol alloc] initWithRTCEngineKit:[LiveRTCManager shareRtc].rtcEngineKit];
    }
    return _beautyComponent;
}

- (void)dealloc {
    [[LiveRTCManager shareRtc] leaveLiveRoom];
}

@end
