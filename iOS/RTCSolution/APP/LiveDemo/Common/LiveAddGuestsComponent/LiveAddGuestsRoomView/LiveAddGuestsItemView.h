// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "BaseButton.h"
#import "LiveRTCManager.h"
#import "LiveUserModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface LiveAddGuestsItemView : BaseButton

@property (nonatomic, strong) LiveUserModel *userModel;

@property (nonatomic, copy) void (^clickBlock)(LiveUserModel *userModel);

- (void)updateNetworkQuality:(LiveNetworkQualityStatus)status;

@end

NS_ASSUME_NONNULL_END
