// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveRoomInfoModel.h"
#import "LiveUserModel.h"
#import <UIKit/UIKit.h>

typedef NS_ENUM(NSUInteger, LiveRoomSettingType) {
    LiveRoomSettingTypeCreateRoom,
    LiveRoomSettingTypeHostLiving,
    LiveRoomSettingTypeHostChat,
    LiveRoomSettingTypeAudience,
    LiveRoomSettingTypeGuest
};

@interface LiveRoomSettingComponent : NSObject

- (void)showWithType:(LiveRoomSettingType)type
       fromSuperView:(UIView *)superView
              roomID:(LiveRoomInfoModel *)liveRoomModel
           userModel:(LiveUserModel *)liveUserModel;

- (void)refreshGuestSettingView;

- (void)close;

@end
