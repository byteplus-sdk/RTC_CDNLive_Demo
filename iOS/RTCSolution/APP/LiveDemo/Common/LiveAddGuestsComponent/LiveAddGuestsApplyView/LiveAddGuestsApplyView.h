//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveUserModel.h"
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface LiveAddGuestsApplyView : UIView

@property (nonatomic, strong) LiveUserModel *userModel;

@property (nonatomic, copy) void (^clickApplyBlock)(void);

- (void)updateApplying;

- (void)resetStatus;

@end

NS_ASSUME_NONNULL_END
