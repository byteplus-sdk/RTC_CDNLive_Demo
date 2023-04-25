// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveUserModel.h"
#import <UIKit/UIKit.h>

@class LiveAddGuestsUserListtCell;

NS_ASSUME_NONNULL_BEGIN

@protocol LiveAddGuestsUserListtCellDelegate <NSObject>

- (void)liveAddGuestsUserListtCell:(LiveAddGuestsUserListtCell *)liveAddGuestsUserListtCell clickButton:(LiveUserModel *)model;

@end

@interface LiveAddGuestsUserListtCell : UITableViewCell

@property (nonatomic, strong) LiveUserModel *model;

@property (nonatomic, weak) id<LiveAddGuestsUserListtCellDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
