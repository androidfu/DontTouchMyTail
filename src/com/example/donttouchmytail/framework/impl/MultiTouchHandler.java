package com.example.donttouchmytail.framework.impl;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.view.MotionEvent;
import android.view.View;

import com.example.donttouchmytail.framework.Input.TouchEvent;
import com.example.donttouchmytail.framework.Pool;
import com.example.donttouchmytail.framework.Pool.PoolObjectFactory;

@TargetApi(5)
public class MultiTouchHandler implements TouchHandler {
    private static final int NO_POINTER_ID = -1;
    private static final int MAX_TOUCHPOINTS = 10;
    boolean[] _isTouched = new boolean[MAX_TOUCHPOINTS];
    int[] _touchX = new int[MAX_TOUCHPOINTS];
    int[] _touchY = new int[MAX_TOUCHPOINTS];
    int[] _id = new int[MAX_TOUCHPOINTS];
    Pool<TouchEvent> _touchEventPool;
    List<TouchEvent> _touchEvents = new ArrayList<TouchEvent>();
    List<TouchEvent> _touchEventsBuffer = new ArrayList<TouchEvent>();
    float _scaleX;
    float _scaleY;

    public MultiTouchHandler(View view, float scaleX, float scaleY) {
        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
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
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int pointerCount = event.getPointerCount();
            TouchEvent touchEvent;
            for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
                if (i >= pointerCount) {
                    _isTouched[i] = false;
                    _id[i] = NO_POINTER_ID;
                    continue;
                }
                int pointerId = event.getPointerId(i);
                if (event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex) {

                    continue;
                }
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        touchEvent = _touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_DOWN;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = _touchX[i] = (int) (event.getX(i) * _scaleX);
                        touchEvent.y = _touchY[i] = (int) (event.getY(i) * _scaleY);
                        _isTouched[i] = true;
                        _id[i] = pointerId;
                        _touchEventsBuffer.add(touchEvent);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        touchEvent = _touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_UP;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = _touchX[i] = (int) (event.getX(i) * _scaleX);
                        touchEvent.y = _touchY[i] = (int) (event.getY(i) * _scaleY);
                        _isTouched[i] = false;
                        _id[i] = NO_POINTER_ID;
                        _touchEventsBuffer.add(touchEvent);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchEvent = _touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = _touchX[i] = (int) (event.getX(i) * _scaleX);
                        touchEvent.y = _touchY[i] = (int) (event.getY(i) * _scaleY);
                        _isTouched[i] = true;
                        _id[i] = pointerId;
                        _touchEventsBuffer.add(touchEvent);
                        break;
                }
            }
            return true;
        }
    }

    @Override
    public boolean isTouchDown(int pointer) {
        synchronized (this) {
            int index = getIndex(pointer);
            if (index < 0 || index >= MAX_TOUCHPOINTS) {
                return false;
            } else {
                return _isTouched[index];
            }
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized (this) {
            int index = getIndex(pointer);
            if (index < 0 || index >= MAX_TOUCHPOINTS) {
                return 0;
            } else {
                return _touchX[index];
            }
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized (this) {
            int index = getIndex(pointer);
            if (index < 0 || index >= MAX_TOUCHPOINTS) {
                return 0;
            } else {
                return _touchY[index];
            }
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

    private int getIndex(int pointerId) {
        for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
            if (_id[i] == pointerId) {
                return i;
            }
        }
        return NO_POINTER_ID;
    }
}
