//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "AlertActionModel.h"
#import <Foundation/Foundation.h>

@interface AlertActionManager : NSObject

/*
 * Alert Singletons
 */
+ (AlertActionManager *)shareAlertActionManager;

/*
 * Show Alert window
 * @param message Prompt information
 */
- (void)showWithMessage:(NSString *)message;

/*
 * Show Alert window
 * @param message Prompt information
 * @param actions Button model
 */
- (void)showWithMessage:(NSString *)message actions:(NSArray<AlertActionModel *> *)actions;

/*
 * Show Alert window
 * @param message Prompt information
 * @param actions Button model
 * @param hideDelay dismiss delay, if <= 0 keep show
 */
- (void)showWithMessage:(NSString *)message actions:(NSArray<AlertActionModel *> *)actions hideDelay:(NSTimeInterval)delay;

/*
 * Dismiss Alert window
 * @param message Prompt information
 * @param actions Button model
 */
- (void)dismiss:(void (^)(void))completion;

@end
