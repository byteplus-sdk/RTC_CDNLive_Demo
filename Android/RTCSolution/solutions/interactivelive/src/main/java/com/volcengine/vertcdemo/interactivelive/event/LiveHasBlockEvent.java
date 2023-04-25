// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.interactivelive.event;
/**
 * Whether there is a mask event on the live screen
 */
public class LiveHasBlockEvent {

    public boolean hasBlock;

    public LiveHasBlockEvent(boolean hasBlock) {
        this.hasBlock = hasBlock;
    }
}
