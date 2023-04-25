// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.playerkit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.volcengine.vertcdemo.protocol.IVideoPlayer;
import com.volcengine.vertcdemo.rtmpplayer.databinding.ActivityIjkplayerBinding;

public class IJKPlayerActivity extends AppCompatActivity {

    private IVideoPlayer mIVideoPlayer;

    private final String testUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityIjkplayerBinding viewBinding = ActivityIjkplayerBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        viewBinding.config.setOnClickListener(v -> {
            mIVideoPlayer = new IVideoPlayerImpl();
            mIVideoPlayer.startWithConfiguration(getApplicationContext());
        });
        viewBinding.setUrl.setOnClickListener(v -> {
            if (mIVideoPlayer != null) {
                mIVideoPlayer.setPlayerUrl(
                        testUrl,
                        viewBinding.videoContainer,
                        null);
            }
        });
        viewBinding.play.setOnClickListener(v -> {
            if (mIVideoPlayer != null) {
                mIVideoPlayer.play();
            }
        });
        viewBinding.stop.setOnClickListener(v -> {
            if (mIVideoPlayer != null) {
                mIVideoPlayer.stop();
            }
        });
        viewBinding.destroy.setOnClickListener(v -> {
            if (mIVideoPlayer != null) {
                mIVideoPlayer.destroy();
            }
        });

        viewBinding.scaleNone.setOnClickListener(v -> {
            if (mIVideoPlayer != null) {
                mIVideoPlayer.updatePlayScaleModel(IVideoPlayer.MODE_NONE);
            }
        });
        viewBinding.scaleAspectFit.setOnClickListener(v -> {
            if (mIVideoPlayer != null) {
                mIVideoPlayer.updatePlayScaleModel(IVideoPlayer.MODE_ASPECT_FIT);
            }
        });
        viewBinding.scaleAspectFill.setOnClickListener(v -> {
            if (mIVideoPlayer != null) {
                mIVideoPlayer.updatePlayScaleModel(IVideoPlayer.MODE_ASPECT_FILL);
            }
        });
        viewBinding.scaleFill.setOnClickListener(v -> {
            if (mIVideoPlayer != null) {
                mIVideoPlayer.updatePlayScaleModel(IVideoPlayer.MODE_FILL);
            }
        });
    }
}