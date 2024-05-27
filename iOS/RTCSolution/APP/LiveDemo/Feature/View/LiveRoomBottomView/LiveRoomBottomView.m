//
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
//

#import "LiveRoomBottomView.h"
#import "UIView+Fillet.h"

@interface LiveRoomBottomView ()

@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, assign) BottomRoleStatus roleStatus;
@property (nonatomic, strong) NSMutableArray *buttonLists;

@end

@implementation LiveRoomBottomView

- (instancetype)init {
    self = [super init];
    if (self) {
        self.clipsToBounds = NO;
        self.backgroundColor = [UIColor clearColor];

        [self addSubview:self.contentView];
        [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
    }
    return self;
}

- (void)buttonAction:(LiveRoomItemButton *)sender {
    if ([self.delegate respondsToSelector:@selector(liveRoomBottomView:itemButton:roleStatus:)]) {
        [self.delegate liveRoomBottomView:self itemButton:sender roleStatus:self.roleStatus];
    }
}

#pragma mark - Publish Action

- (void)updateButtonStatus:(LiveRoomItemButtonState)status
               touchStatus:(LiveRoomItemTouchStatus)touchStatus {
    if (self.buttonLists.count <= 0) {
        return;
    }
    LiveRoomItemButton *itemButton = nil;
    for (LiveRoomItemButton *tempItemButton in self.buttonLists) {
        if (tempItemButton.currentState == status) {
            itemButton = tempItemButton;
            break;
        }
    }
    if (itemButton) {
        itemButton.touchStatus = touchStatus;
    }
}

- (void)updateButtonRoleStatus:(BottomRoleStatus)status {
    _roleStatus = status;
    NSMutableArray *list = [[NSMutableArray alloc] init];
    switch (status) {
        case BottomRoleStatusAudience:
            [list addObject:@(LiveRoomItemButtonStateGift)];
            [list addObject:@(LiveRoomItemButtonStateChat)];
            [list addObject:@(LiveRoomItemButtonStateSet)];
            [list addObject:@(LiveRoomItemButtonStateEnd)];
            break;

        case BottomRoleStatusGuests:
            [list addObject:@(LiveRoomItemButtonStateGift)];
            [list addObject:@(LiveRoomItemButtonStateChat)];
            [list addObject:@(LiveRoomItemButtonStateBeauty)];
            [list addObject:@(LiveRoomItemButtonStateSet)];
            [list addObject:@(LiveRoomItemButtonStateEnd)];
            break;

        case BottomRoleStatusHost:
            [list addObject:@(LiveRoomItemButtonStatePK)];
            [list addObject:@(LiveRoomItemButtonStateChat)];
            [list addObject:@(LiveRoomItemButtonStateBeauty)];
            [list addObject:@(LiveRoomItemButtonStateSet)];
            [list addObject:@(LiveRoomItemButtonStateEnd)];
            break;

        default:
            break;
    }
    [self addSubviewAndConstraints:[list copy]];
    switch (status) {
        case BottomRoleStatusAudience:
            [self updateButtonStatus:LiveRoomItemButtonStateChat touchStatus:LiveRoomItemTouchStatusNone];
            break;

        case BottomRoleStatusGuests:
            [self updateButtonStatus:LiveRoomItemButtonStateChat touchStatus:LiveRoomItemTouchStatusClose];
            break;
        default:
            break;
    }
}

- (LiveRoomItemTouchStatus)getButtonTouchStatus:(LiveRoomItemButtonState)buttonState {
    LiveRoomItemTouchStatus touchStatus = LiveRoomItemTouchStatusNone;
    if (self.buttonLists.count <= 0) {
        return touchStatus;
    }
    LiveRoomItemButton *itemButton = nil;
    for (LiveRoomItemButton *tempItemButton in self.buttonLists) {
        if (tempItemButton.currentState == buttonState) {
            itemButton = tempItemButton;
            break;
        }
    }
    if (itemButton) {
        touchStatus = itemButton.touchStatus;
    }
    return touchStatus;
}

#pragma mark - Private Action

- (void)addSubviewAndConstraints:(NSArray *)list {
    [self.contentView removeAllSubviews];
    [self.buttonLists removeAllObjects];

    for (int i = 0; i < list.count; i++) {
        NSNumber *stateNumber = list[i];
        LiveRoomItemButton *button = [[LiveRoomItemButton alloc] initWithState:stateNumber.integerValue];
        [button addTarget:self action:@selector(buttonAction:) forControlEvents:UIControlEventTouchUpInside];
        button.imageView.contentMode = UIViewContentModeScaleAspectFit;
        [self.buttonLists addObject:button];
        [self.contentView addSubview:button];
    }

    CGFloat itemWidth = 36;
    [self.buttonLists mas_remakeConstraints:^(MASConstraintMaker *make){

    }];
    [self.buttonLists mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedItemLength:itemWidth leadSpacing:0 tailSpacing:0];
    [self.buttonLists mas_updateConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.equalTo(self.contentView);
    }];

    CGFloat width = (self.buttonLists.count * 36) + ((self.buttonLists.count - 1) * 12);
    [self mas_updateConstraints:^(MASConstraintMaker *make) {
        make.width.mas_equalTo(width);
    }];
}

#pragma mark - Getter

- (UIView *)contentView {
    if (!_contentView) {
        _contentView = [[UIView alloc] init];
    }
    return _contentView;
}

- (NSMutableArray *)buttonLists {
    if (!_buttonLists) {
        _buttonLists = [[NSMutableArray alloc] init];
    }
    return _buttonLists;
}

@end
