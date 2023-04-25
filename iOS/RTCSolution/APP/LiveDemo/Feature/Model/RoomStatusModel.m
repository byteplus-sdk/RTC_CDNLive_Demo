// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "RoomStatusModel.h"

@implementation RoomStatusModel

+ (NSDictionary *)modelContainerPropertyGenericClass {
    return @{@"interactUserList" : [LiveUserModel class]};
}

+ (NSDictionary *)modelCustomPropertyMapper {
    return @{@"interactStatus" : @"interact_status",
             @"interactUserList" : @"interact_user_list"};
}

@end
