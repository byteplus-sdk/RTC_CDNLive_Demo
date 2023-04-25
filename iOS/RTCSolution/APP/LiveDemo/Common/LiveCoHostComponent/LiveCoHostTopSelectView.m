// 
// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT
// 

#import "LiveCoHostTopSelectView.h"

@interface LiveCoHostTopSelectView ()

@property (nonatomic, strong) BaseButton *raiseButton;
@property (nonatomic, strong) BaseButton *audienceButton;
@property (nonatomic, strong) UIView *selectLineView;
@property (nonatomic, strong) UILabel *titleLabel;

@end

@implementation LiveCoHostTopSelectView

- (instancetype)init {
    self = [super init];
    if (self) {
        self.backgroundColor = [UIColor colorFromHexString:@"#272E3B"];

        [self addSubview:self.raiseButton];
        [self addSubview:self.audienceButton];
        [self addSubview:self.selectLineView];
        [self addSubview:self.titleLabel];

        [self addConstraints];
    }
    return self;
}

- (void)addConstraints {
    [self.raiseButton mas_makeConstraints:^(MASConstraintMaker *make) {
      make.left.bottom.height.equalTo(self);
      make.width.mas_equalTo(SCREEN_WIDTH / 2);
    }];

    [self.audienceButton mas_makeConstraints:^(MASConstraintMaker *make) {
      make.bottom.height.equalTo(self);
      make.left.equalTo(self.raiseButton.mas_right);
      make.width.mas_equalTo(SCREEN_WIDTH / 2);
    }];

    [self.selectLineView mas_makeConstraints:^(MASConstraintMaker *make) {
      make.width.mas_equalTo(64);
      make.height.mas_equalTo(2);
      make.bottom.mas_equalTo(-2);
      make.centerX.equalTo(self.raiseButton);
    }];

    [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
      make.center.equalTo(self);
    }];
}

- (void)setTitleStr:(NSString *)titleStr {
    _titleStr = titleStr;
    if (NOEmptyStr(titleStr)) {
        self.raiseButton.hidden = YES;
        self.audienceButton.hidden = YES;
        self.selectLineView.hidden = YES;
        self.titleLabel.hidden = NO;
        self.titleLabel.text = titleStr;
    } else {
        self.raiseButton.hidden = NO;
        self.audienceButton.hidden = NO;
        self.selectLineView.hidden = NO;
        self.titleLabel.hidden = YES;
    }
}

- (void)raiseButtonAction {
    [self.raiseButton setTitleColor:[UIColor colorFromHexString:@"#4080FF"] forState:UIControlStateNormal];
    [self.audienceButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];

    [self.selectLineView mas_remakeConstraints:^(MASConstraintMaker *make) {
      make.width.mas_equalTo(64);
      make.height.mas_equalTo(2);
      make.bottom.mas_equalTo(-2);
      make.centerX.equalTo(self.raiseButton);
    }];

    if ([self.delegate respondsToSelector:@selector(liveCoHostTopSelectView:clickSwitchItem:)]) {
        [self.delegate liveCoHostTopSelectView:self clickSwitchItem:NO];
    }
}

- (void)audienceButtonAction {
    [self.audienceButton setTitleColor:[UIColor colorFromHexString:@"#4080FF"] forState:UIControlStateNormal];
    [self.raiseButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];

    [self.selectLineView mas_remakeConstraints:^(MASConstraintMaker *make) {
      make.width.mas_equalTo(64);
      make.height.mas_equalTo(2);
      make.bottom.mas_equalTo(-2);
      make.centerX.equalTo(self.audienceButton);
    }];

    if ([self.delegate respondsToSelector:@selector(liveCoHostTopSelectView:clickSwitchItem:)]) {
        [self.delegate liveCoHostTopSelectView:self clickSwitchItem:YES];
    }
}

#pragma mark - Getter

- (BaseButton *)raiseButton {
    if (!_raiseButton) {
        _raiseButton = [[BaseButton alloc] init];
        _raiseButton.backgroundColor = [UIColor clearColor];
        [_raiseButton setTitle:LocalizedString(@"host_live") forState:UIControlStateNormal];
        [_raiseButton setTitleColor:[UIColor colorFromHexString:@"#4080FF"] forState:UIControlStateNormal];
        _raiseButton.titleLabel.font = [UIFont systemFontOfSize:16];
        [_raiseButton addTarget:self action:@selector(raiseButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _raiseButton;
}

- (BaseButton *)audienceButton {
    if (!_audienceButton) {
        _audienceButton = [[BaseButton alloc] init];
        _audienceButton.backgroundColor = [UIColor clearColor];
        [_audienceButton setTitle:LocalizedString(@"battle") forState:UIControlStateNormal];
        [_audienceButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _audienceButton.titleLabel.font = [UIFont systemFontOfSize:16];
        [_audienceButton addTarget:self action:@selector(audienceButtonAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _audienceButton;
}

- (UIView *)selectLineView {
    if (!_selectLineView) {
        _selectLineView = [[UIView alloc] init];
        _selectLineView.backgroundColor = [UIColor colorFromRGBHexString:@"#4080FF"];
    }
    return _selectLineView;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightRegular];
        _titleLabel.numberOfLines = 1;
        _titleLabel.hidden = YES;
    }
    return _titleLabel;
}

- (void)dealloc {
    NSLog(@"dealloc %@", NSStringFromClass([self class]));
}

@end
