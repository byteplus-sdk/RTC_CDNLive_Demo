//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class LiveRoomAudienceSettingView;
@protocol LiveRoomAudienceSettingViewDelegate <NSObject>

- (void)liveRoomAudienceSettingView:(LiveRoomAudienceSettingView *)settingView didChangeResolution:(NSInteger)index;

@end

@interface LiveRoomAudienceSettingView : UIView
@property (nonatomic, weak) id<LiveRoomAudienceSettingViewDelegate> delegate;
@end

NS_ASSUME_NONNULL_END
