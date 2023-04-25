// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "BaseUserModel.h"
#import "LiveConstants.h"
#import "ToolKit.h"

NS_ASSUME_NONNULL_BEGIN

@interface LiveUserModel : BaseUserModel

@property (nonatomic, copy) NSString *roomID;
@property (nonatomic, assign) LiveUserRole role;
@property (nonatomic, assign) LiveInteractStatus status;
@property (nonatomic, assign) BOOL mic;
@property (nonatomic, assign) BOOL camera;
@property (nonatomic, strong) NSString *extra;
@property (nonatomic, assign, readonly) BOOL isLoginUser;
@property (nonatomic, assign) CGFloat videoWidth;
@property (nonatomic, assign) CGFloat videoHeight;
@property (nonatomic, assign) CGSize videoSize;

@end

NS_ASSUME_NONNULL_END
