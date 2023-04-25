// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveRoomItemButton.h"

@interface LiveRoomItemButton ()

@property (nonatomic, strong) UILabel *desLabel;

@end

@implementation LiveRoomItemButton

- (instancetype)initWithState:(LiveRoomItemButtonState)state {
    self = [super init];
    if (self) {
        _currentState = state;
        self.clipsToBounds = NO;

        [self addSubview:self.desLabel];
        [self.desLabel mas_makeConstraints:^(MASConstraintMaker *make) {
          make.bottom.equalTo(self).offset(-10);
          make.centerX.equalTo(self);
        }];

        [self updateState:state];
    }
    return self;
}

- (void)updateState:(LiveRoomItemButtonState)state {
    NSString *imageName = @"";
    if (LiveRoomItemButtonStatePK == state) {
        imageName = @"InteractiveLive_pk";
    } else if (LiveRoomItemButtonStateChat == state) {
        imageName = @"InteractiveLive_chat";
    } else if (LiveRoomItemButtonStateBeauty == state) {
        imageName = @"InteractiveLive_be";
    } else if (LiveRoomItemButtonStateSet == state) {
        imageName = @"InteractiveLive_set";
    } else if (LiveRoomItemButtonStateEnd == state) {
        imageName = @"InteractiveLive_end";
    } else if (LiveRoomItemButtonStateGift == state) {
        imageName = @"InteractiveLive_gift";
    }
    if (NOEmptyStr(imageName)) {
        [self setImage:[UIImage imageNamed:imageName bundleName:HomeBundleName] forState:UIControlStateNormal];
    }
}

- (void)setTouchStatus:(LiveRoomItemTouchStatus)touchStatus {
    _touchStatus = touchStatus;
    NSString *imageName = @"";
    switch (_currentState) {
        case LiveRoomItemButtonStatePK:
            if (touchStatus == LiveRoomItemTouchStatusClose) {
                imageName = @"InteractiveLive_pk_un";
            } else if (touchStatus == LiveRoomItemTouchStatusIng) {
                imageName = @"InteractiveLive_pk_temp";
            } else {
                imageName = @"InteractiveLive_pk";
            }
            break;
        case LiveRoomItemButtonStateChat:
            if (touchStatus == LiveRoomItemTouchStatusClose) {
                imageName = @"InteractiveLive_chat_un";
            } else {
                imageName = @"InteractiveLive_chat";
            }
            break;
        default:
            break;
    }
    if (NOEmptyStr(imageName)) {
        [self setImage:[UIImage imageNamed:imageName bundleName:HomeBundleName] forState:UIControlStateNormal];
    }
}

#pragma mark - Getter

- (UILabel *)desLabel {
    if (!_desLabel) {
        _desLabel = [[UILabel alloc] init];
        _desLabel.textColor = [UIColor colorFromHexString:@"#86909C"];
        _desLabel.font = [UIFont systemFontOfSize:12];
    }
    return _desLabel;
}

@end
