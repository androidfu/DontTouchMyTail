package com.example.donttouchmytail.framework.impl;

import java.util.List;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

import com.example.donttouchmytail.framework.Input;

public class AndroidInput implements Input {
    AccelerometerHandler _accelHandler;
    KeyboardHandler _keyHandler;
    TouchHandler _touchHandler;

    public AndroidInput(Context context, View view, float scaleX, float scaleY) {
        _accelHandler = new AccelerometerHandler(context);
        _keyHandler = new KeyboardHandler(view);
        if (VERSION.SDK_INT < 5) {
            _touchHandler = new SingleTouchHandler(view, scaleX, scaleY);
        } else {
            _touchHandler = new MultiTouchHandler(view, scaleX, scaleY);
        }
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        return _keyHandler.isKeyPressed(keyCode);
    }

    @Override
    public boolean isTouchDown(int pointer) {
        return _touchHandler.isTouchDown(pointer);
    }

    @Override
    public int getTouchX(int pointer) {
        return _touchHandler.getTouchX(pointer);
    }

    @Override
    public int getTouchY(int pointer) {
        return _touchHandler.getTouchY(pointer);
    }

    @Override
    public float getAccelX() {
        return _accelHandler.getAccelX();
    }

    @Override
    public float getAccelY() {
        return _accelHandler.getAccelY();
    }

    @Override
    public float getAccelZ() {
        return _accelHandler.getAccelZ();
    }

    @Override
    public List<KeyEvent> getKeyEvents() {
        return _keyHandler.getKeyEvents();
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        return _touchHandler.getTouchEvents();
    }

}
