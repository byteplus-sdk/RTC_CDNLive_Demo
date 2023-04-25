// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "BaseButton.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, LiveRoomItemButtonState) {
    LiveRoomItemButtonStatePK = 0,
    LiveRoomItemButtonStateChat,
    LiveRoomItemButtonStateBeauty,
    LiveRoomItemButtonStateSet,
    LiveRoomItemButtonStateEnd,
    LiveRoomItemButtonStateGift,
};

typedef NS_ENUM(NSInteger, LiveRoomItemTouchStatus) {
    LiveRoomItemTouchStatusNone = 0,
    LiveRoomItemTouchStatusClose,
    LiveRoomItemTouchStatusIng,
};

@interface LiveRoomItemButton : BaseButton

@property (nonatomic, assign) LiveRoomItemTouchStatus touchStatus;

@property (nonatomic, assign, readonly) LiveRoomItemButtonState currentState;

- (instancetype)initWithState:(LiveRoomItemButtonState)state;

@end

NS_ASSUME_NONNULL_END
