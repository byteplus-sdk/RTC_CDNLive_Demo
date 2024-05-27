//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveSettingVideoConfig.h"
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class LiveCreateRoomSettingView;

@protocol LiveCreateRoomSettingViewDelegate <NSObject>

- (void)liveCreateRoomSettingView:(LiveCreateRoomSettingView *)settingView didChangefpsType:(LiveSettingVideoFpsType)fpsType;

- (void)liveCreateRoomSettingView:(LiveCreateRoomSettingView *)settingView didChangeResolution:(NSInteger)index;

- (void)liveCreateRoomSettingView:(LiveCreateRoomSettingView *)settingView didChangeBitrate:(NSInteger)bitrate;

@end

@interface LiveCreateRoomSettingView : UIView
@property (nonatomic, weak) id<LiveCreateRoomSettingViewDelegate> delegate;

@property (nonatomic, strong) LiveSettingVideoConfig *videoConfig;
@end

NS_ASSUME_NONNULL_END
