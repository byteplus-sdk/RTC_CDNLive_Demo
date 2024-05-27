//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveUserModel.h"
#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, PullRenderStatus) {
    // Single anchor & audience mic mode
    PullRenderStatusNone = 0,
    // PK mode
    PullRenderStatusCoHst,
};

NS_ASSUME_NONNULL_BEGIN

@interface LivePullRenderView : UIView

@property (nonatomic, strong, readonly) UIView *liveView;

@property (nonatomic, assign) PullRenderStatus status;

- (void)updateHostMic:(BOOL)mic camera:(BOOL)camera;

- (void)setUserName:(NSString *)userName;

@end

NS_ASSUME_NONNULL_END
