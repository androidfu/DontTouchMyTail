package com.example.donttouchmytail.framework.impl;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;

import com.example.donttouchmytail.framework.Input.TouchEvent;
import com.example.donttouchmytail.framework.Pool;
import com.example.donttouchmytail.framework.Pool.PoolObjectFactory;

public class SingleTouchHandler implements TouchHandler {

    boolean isTouched;
    int _touchX;
    int _touchY;
    Pool<TouchEvent> _touchEventPool;
    List<TouchEvent> _touchEvents = new ArrayList<TouchEvent>();
    List<TouchEvent> _touchEventsBuffer = new ArrayList<TouchEvent>();
    float _scaleX;
    float _scaleY;

    public SingleTouchHandler(View view, float scaleX, float scaleY) {
        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }
        };
        _touchEventPool = new Pool<TouchEvent>(factory, 100);
        view.setOnTouchListener(this);
        this._scaleX = scaleX;
        this._scaleY = scaleY;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        synchronized (this) {
            TouchEvent touchEvent = _touchEventPool.newObject();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchEvent.type = TouchEvent.TOUCH_DOWN;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    touchEvent.type = TouchEvent.TOUCH_UP;
                    isTouched = false;
                    break;
            }
            touchEvent.x = _touchX = (int) (event.getX() * _scaleX);
            touchEvent.y = _touchY = (int) (event.getY() * _scaleY);
            _touchEventsBuffer.add(touchEvent);
            return true;
        }
    }

    @Override
    public boolean isTouchDown(int pointer) {
        synchronized (this) {
            if (pointer == 0) {
                return isTouched;
            } else {
                return false;
            }
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized (this) {
            return _touchX;
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized (this) {
            return _touchY;
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized (this) {
            int len = _touchEvents.size();
            for (int i = 0; i < len; i++) {
                _touchEventPool.free(_touchEvents.get(i));
            }
            _touchEvents.clear();
            _touchEvents.addAll(_touchEventsBuffer);
            _touchEventsBuffer.clear();
            return _touchEvents;
        }
    }

}
