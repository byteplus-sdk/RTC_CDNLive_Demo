// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveRTCManager.h"
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface LivePushRenderView : UIView

@property (nonatomic, strong) UIView *streamView;

- (void)updateHostMic:(BOOL)mic camera:(BOOL)camera;

- (void)updateNetworkQuality:(LiveNetworkQualityStatus)status;

- (void)setUserName:(NSString *)userName;

@end

NS_ASSUME_NONNULL_END
