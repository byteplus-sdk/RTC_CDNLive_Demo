// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveRoomInfoModel.h"
#import "LiveUserModel.h"
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface LivePushStreamComponent : NSObject

@property (nonatomic, assign, readonly) BOOL isConnect;

- (instancetype)initWithSuperView:(UIView *)superView
                        roomModel:(LiveRoomInfoModel *)roomModel
                    streamPushUrl:(NSString *)streamPushUrl;

- (void)openWithUserModel:(LiveUserModel *)userModel;

- (void)close;

- (void)updateHostMic:(BOOL)mic camera:(BOOL)camera;

@end

NS_ASSUME_NONNULL_END
