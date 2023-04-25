// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveCoHostAudienceListsView.h"
#import "LiveCoHostRaiseHandListsView.h"
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, LiveCoHostDismissState) {
    LiveCoHostDismissStateNone,
    LiveCoHostDismissStateInviteIng,
};

@interface LiveCoHostComponent : NSObject

@property (nonatomic, assign, readonly) BOOL isConnect;

- (instancetype)initWithRoomID:(LiveRoomInfoModel *)roomInfoModel;

// list

- (void)showInviteList:(void (^ __nullable)(LiveCoHostDismissState state))dismissBlock;

- (void)dismissInviteList;

- (void)updateInviteList;

// live room

- (void)showCoHost:(UIView *)superView
     streamPushUrl:(NSString *)streamPushUrl
     userModelList:(NSArray<LiveUserModel *> *)userModelList
    loginUserModel:(LiveUserModel *)loginUserModel
 otherAnchorRoomId:(NSString *)otherRoomId
  otherAnchorToken:(NSString *)otherToken
     completeBlock:(void (^)(void))completeBlock;

- (void)closeCoHost;

- (void)updateGuestsMic:(BOOL)mic uid:(NSString *)uid;

- (void)updateGuestsCamera:(BOOL)camera uid:(NSString *)uid;

@end

NS_ASSUME_NONNULL_END
