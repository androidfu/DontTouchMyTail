package com.example.donttouchmytail.framework.impl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

import com.example.donttouchmytail.framework.Audio;
import com.example.donttouchmytail.framework.FileIO;
import com.example.donttouchmytail.framework.Game;
import com.example.donttouchmytail.framework.Graphics;
import com.example.donttouchmytail.framework.Input;
import com.example.donttouchmytail.framework.Screen;

public abstract class AndroidGame extends Activity implements Game {

    private static final String TAG = AndroidGame.class.getSimpleName();
    AndroidFastRenderView _renderView;
    Graphics _graphics;
    Audio _audio;
    Input _input;
    FileIO _fileIO;
    Screen _screen;
    WakeLock _wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? 480 : 320;
        int frameBufferHeight = isLandscape ? 320 : 480;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);
        float scaleX = (float) frameBufferWidth / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();

        _renderView = new AndroidFastRenderView(this, frameBuffer);
        _graphics = new AndroidGraphics(getAssets(), frameBuffer);
        _fileIO = new AndroidFileIO(this);
        _audio = new AndroidAudio(this);
        _input = new AndroidInput(this, _renderView, scaleX, scaleY);
        _screen = getStartScreen();

        setContentView(_renderView);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        _wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        _wakeLock.release();
        _renderView.pause();
        _screen.pause();
        if (isFinishing()) {
            _screen.dispose();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        _wakeLock.acquire();
        _screen.resume();
        _renderView.resume();
    }

    @Override
    public Input getInput() {
        return _input;
    }

    @Override
    public FileIO getFileIO() {
        return _fileIO;
    }

    @Override
    public Graphics getGraphics() {
        return _graphics;
    }

    @Override
    public Audio getAudio() {
        return _audio;
    }

    @Override
    public void setScreen(Screen screen) {
        if (_screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }
        this._screen.pause();
        this._screen.dispose();
        _screen.resume();
        _screen.update(0);
        this._screen = screen;
    }

    @Override
    public Screen getCurrentScreen() {
        return _screen;
    }
}
