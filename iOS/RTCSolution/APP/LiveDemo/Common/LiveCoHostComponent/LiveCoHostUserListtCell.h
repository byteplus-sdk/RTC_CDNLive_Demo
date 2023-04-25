// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveUserModel.h"
#import <UIKit/UIKit.h>

@class LiveCoHostUserListtCell;

NS_ASSUME_NONNULL_BEGIN

@protocol LiveCoHostUserListtCellDelegate <NSObject>

- (void)liveCoHostUserListtCell:(LiveCoHostUserListtCell *)liveCoHostUserListtCell clickButton:(LiveUserModel *)model;

@end

@interface LiveCoHostUserListtCell : UITableViewCell

@property (nonatomic, strong) LiveUserModel *model;

@property (nonatomic, weak) id<LiveCoHostUserListtCellDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
