//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveSheetModel.h"
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface LiveSheetComponent : NSObject

+ (LiveSheetComponent *_Nullable)shareSheet;

- (void)show:(NSArray<LiveSheetModel *> *)list;

- (void)dismissUserListView;

@end

NS_ASSUME_NONNULL_END
