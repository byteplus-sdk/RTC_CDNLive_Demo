// Copyright (c) 2023 BytePlus Pte. Ltd.
// SPDX-License-Identifier: MIT

package com.volcengine.vertcdemo.playerkit;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.volcengine.vertcdemo.common.IAction;
import com.volcengine.vertcdemo.protocol.IVideoPlayer;
import com.volcengine.vertcdemo.utils.Utils;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * IVideoPlayer implements
 *
 * @see com.volcengine.vertcdemo.protocol.IVideoPlayer
 * @see com.volcengine.vertcdemo.protocol.ProtocolUtil#getPlayerInstance()
 */
@SuppressWarnings("unused")
@Keep
public class IVideoPlayerImpl implements IVideoPlayer {

    private static final String TAG = "ijkVideoPlayerImpl";
    private FrameLayout mRenderViewContainer;
    private IMediaPlayer mMediaPlayer = null;
    private TextureView mTextureView;
    private Surface mSurface;
    private String mSourcePath = "";

    private int mScaleMode = MODE_NONE;

    private int mLastVideoWidth = 0;
    private int mLastVideoHeight = 0;

    private final IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = (iMediaPlayer, width, height, i2, i3) -> {
        Log.d(TAG, String.format("OnVideoSizeChangedListener: %d, %d", width, height));
        if (mLastVideoWidth != width || mLastVideoHeight != height) {
            ensureScaleModel();
        }
        mLastVideoWidth = width;
        mLastVideoHeight = height;
    };

    private final IMediaPlayer.OnErrorListener mOnErrorListener = (iMediaPlayer, i, i1) -> {
        Log.d(TAG, String.format("OnErrorListener: %d, %d", i, i1));
        replacePlayWithUrl(mSourcePath);
        play();
        return false;
    };

    @Override
    public void startWithConfiguration(Context context) {
        Log.d(TAG, "startWithConfiguration");
        createTextureView(context);
    }

    @Override
    public void setPlayerUrl(String url, View container, IAction<String> seiCallback) {
        Log.d(TAG, String.format("setPlayerUrl: %s", url));
        mSourcePath = url;
        if (container instanceof ViewGroup && mRenderViewContainer != null) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            Utils.attachViewToViewGroup((ViewGroup) container, mRenderViewContainer, params);

            configPlayer();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.prepareAsync();
        }
    }

    @Override
    public void updatePlayScaleModel(int scalingMode) {
        Log.d(TAG, String.format("updatePlayScaleModel: %d", scalingMode));
        mScaleMode = scalingMode;
        ensureScaleModel();
    }

    @Override
    public void play() {
        Log.d(TAG, "play");
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void replacePlayWithUrl(String url) {
        Log.d(TAG, String.format("replacePlayWithUrl: %s", url));
        stop();
        if (mRenderViewContainer == null) {
            return;
        }
        ViewParent parent = mRenderViewContainer.getParent();
        if (!(parent instanceof ViewGroup)) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) parent;
        setPlayerUrl(url, viewGroup, null);
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public boolean isSupportSEI() {
        Log.d(TAG, "isSupportSEI, always false");
        return false;
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mSourcePath = null;
        removeFromParent(mRenderViewContainer);
        mRenderViewContainer = null;
        mTextureView = null;
        mScaleMode = MODE_NONE;
    }

    private void createTextureView(Context context) {
        //生成一个新的texture view
        mTextureView = new TextureView(context);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                mSurface = new Surface(surface);
                configPlayer();
                if (!TextUtils.isEmpty(mSourcePath)) {
                    replacePlayWithUrl(mSourcePath);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                surface.release();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        mRenderViewContainer = new FrameLayout(context);
        mRenderViewContainer.addView(mTextureView, params);
        mRenderViewContainer.addOnLayoutChangeListener(
                (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom)
                        -> ensureScaleModel());
    }

    private void configPlayer() {
        createPlayer();
        try {
            mMediaPlayer.setDataSource(mSourcePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setSurface(mSurface);
    }
    private void createPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        ijkMediaPlayer.setScreenOnWhilePlaying(true);
        mMediaPlayer = ijkMediaPlayer;

        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
    }
    private void removeFromParent(@Nullable View view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    private void ensureScaleModel() {
        if (mRenderViewContainer == null) {
            return;
        }

        mRenderViewContainer.post(() -> {
            if (mRenderViewContainer == null) {
                return;
            }
            int viewWidth = mRenderViewContainer.getWidth();
            int viewHeight = mRenderViewContainer.getHeight();

            changeRenderViewToFitScaleMode(viewWidth, viewHeight, mLastVideoWidth, mLastVideoHeight);
        });
    }

    private void changeRenderViewToFitScaleMode(int viewWidth, int viewHeight,
                                                int videoWidth, int videoHeight) {
        if (mTextureView == null) {
            return;
        }
        if (mScaleMode == MODE_NONE) {
            return;
        }
        float viewAspect = (float) viewWidth / viewHeight;
        float videoAspect = (float) videoWidth / videoHeight;
        if ((viewAspect == videoAspect) || (mScaleMode == MODE_FILL)) {
            setViewSize(mTextureView, viewWidth, viewHeight);
            return;
        }
        if (mScaleMode == MODE_ASPECT_FIT) {
            if (viewAspect < videoAspect) {
                int h = (int) (viewWidth / videoAspect);
                setViewSize(mTextureView, viewWidth, h);
            } else {
                int w = (int) (viewHeight * videoAspect);
                setViewSize(mTextureView, w, viewHeight);
            }
        } else if (mScaleMode == MODE_ASPECT_FILL) {
            if (viewAspect < videoAspect) {
                int w = (int) (viewHeight * videoAspect);
                setViewSize(mTextureView, w, viewHeight);
            } else {
                int h = (int) (viewWidth / videoAspect);
                setViewSize(mTextureView, viewWidth, h);
            }
        }
    }

    private void setViewSize(View view, int width, int height) {
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
            layoutParams.gravity = Gravity.CENTER;
            view.setLayoutParams(layoutParams);
        }
    }
}
