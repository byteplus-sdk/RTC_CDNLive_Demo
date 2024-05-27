//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LivePushStreamComponent.h"
#import "LivePushRenderView.h"
#import "LiveRTCManager.h"

@interface LivePushStreamComponent ()

@property (nonatomic, weak) UIView *superView;
@property (nonatomic, weak) LivePushRenderView *renderView;

@end

@implementation LivePushStreamComponent

- (instancetype)initWithSuperView:(UIView *)superView
                        roomModel:(LiveRoomInfoModel *)roomModel
                    streamPushUrl:(NSString *)streamPushUrl {
    self = [super init];
    if (self) {
        _superView = superView;
    }
    return self;
}

- (void)openWithUserModel:(LiveUserModel *)userModel {
    _isConnect = YES;

    [[LiveRTCManager shareRtc] bindCanvasViewToUid:userModel.uid];

    if (!_renderView) {
        LivePushRenderView *renderView = [[LivePushRenderView alloc] init];
        [renderView setUserName:userModel.name];
        [_superView addSubview:renderView];
        [renderView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(_superView);
        }];
        _renderView = renderView;
    }
    [self updateHostMic:userModel.mic camera:userModel.camera];
    __weak __typeof(self) wself = self;
    [[LiveRTCManager shareRtc] didChangeNetworkQuality:^(LiveNetworkQualityStatus status, NSString *_Nonnull uid) {
        dispatch_queue_async_safe(dispatch_get_main_queue(), (^{
                                      if ([uid isEqualToString:[LocalUserComponent userModel].uid]) {
                                          [wself.renderView updateNetworkQuality:status];
                                      }
                                  }));
    }];
}

- (void)close {
    _isConnect = NO;
    if (_renderView) {
        [_renderView removeFromSuperview];
        _renderView = nil;
    }
}

- (void)updateHostMic:(BOOL)mic camera:(BOOL)camera {
    if (_renderView) {
        [_renderView updateHostMic:mic camera:camera];

        UIView *rtcStreamView = [[LiveRTCManager shareRtc] getStreamViewWithUid:[LocalUserComponent userModel].uid];
        if (camera) {
            rtcStreamView.hidden = NO;
            [_renderView.streamView addSubview:rtcStreamView];
            [rtcStreamView mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.edges.equalTo(_renderView.streamView);
            }];
        } else {
            [[LiveRTCManager shareRtc] removeCanvasLocalUid];
        }
    }
}

@end
