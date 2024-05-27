//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveCoHostUserListtCell.h"
#import <UIKit/UIKit.h>
@class LiveCoHostRaiseHandListsView;

NS_ASSUME_NONNULL_BEGIN

@protocol LiveCoHostRaiseHandListsViewDelegate <NSObject>

- (void)liveCoHostRaiseHandListsView:(LiveCoHostRaiseHandListsView *)liveCoHostRaiseHandListsView clickButton:(LiveUserModel *)model;

@end

@interface LiveCoHostRaiseHandListsView : UIView

@property (nonatomic, copy) NSArray<LiveUserModel *> *dataLists;

@property (nonatomic, weak) id<LiveCoHostRaiseHandListsViewDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
