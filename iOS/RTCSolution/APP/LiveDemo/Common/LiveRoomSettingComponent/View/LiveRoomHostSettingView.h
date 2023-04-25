// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveSettingVideoConfig.h"
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class LiveRoomHostSettingView;

@protocol LiveRoomHostSettingViewDelegate <NSObject>

- (void)liveRoomHostSettingView:(LiveRoomHostSettingView *)settingView didSwitchCamera:(BOOL)isFront;

- (void)liveRoomHostSettingView:(LiveRoomHostSettingView *)settingView didChangeMicState:(BOOL)isOn;

- (void)liveRoomHostSettingView:(LiveRoomHostSettingView *)settingView didChangeCameraState:(BOOL)isOn;

- (void)liveRoomHostSettingView:(LiveRoomHostSettingView *)settingView didChangefpsType:(LiveSettingVideoFpsType)type;

- (void)liveRoomHostSettingView:(LiveRoomHostSettingView *)settingView didChangeResolution:(NSInteger)index;

- (void)liveRoomHostSettingView:(LiveRoomHostSettingView *)settingView didChangeBitrate:(NSInteger)bitrate;

@end

@interface LiveRoomHostSettingView : UIView

@property (nonatomic, weak) id<LiveRoomHostSettingViewDelegate> delegate;

@property (nonatomic, strong) LiveSettingVideoConfig *videoConfig;

@property (nonatomic, assign) BOOL allowChangeConfig;
@property (nonatomic, assign) BOOL isMicOn;
@property (nonatomic, assign) BOOL isCameraOn;
@property (nonatomic, assign) BOOL isSwitchCamera;
@end

NS_ASSUME_NONNULL_END
