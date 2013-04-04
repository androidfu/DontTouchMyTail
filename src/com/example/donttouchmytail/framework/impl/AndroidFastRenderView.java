package com.example.donttouchmytail.framework.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidFastRenderView extends SurfaceView implements Runnable {
    AndroidGame _game;
    Bitmap _frameBuffer;
    Thread _renderThread = null;
    SurfaceHolder _holder;
    volatile boolean _running = false;

    public AndroidFastRenderView(AndroidGame game, Bitmap frameBuffer) {
        super(game);
        this._game = game;
        this._frameBuffer = frameBuffer;
        this._holder = getHolder();
    }

    public void resume() {
        _running = true;
        _renderThread = new Thread(this);
        _renderThread.start();
    }

    @Override
    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime();
        while (_running) {
            if (!_holder.getSurface().isValid()) {
                continue;
            }
            float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
            startTime = System.nanoTime();
            _game.getCurrentScreen().update(deltaTime);
            _game.getCurrentScreen().present(deltaTime);
            Canvas canvas = _holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(_frameBuffer, null, dstRect, null);
            _holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        _running = false;
        while (true) {
            try {
                _renderThread.join();
                return;
            } catch (InterruptedException ie) {
                // retry
            }
        }
    }
}
