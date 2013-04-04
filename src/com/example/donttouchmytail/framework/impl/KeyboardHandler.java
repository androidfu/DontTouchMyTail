package com.example.donttouchmytail.framework.impl;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnKeyListener;

import com.example.donttouchmytail.framework.Input.KeyEvent;
import com.example.donttouchmytail.framework.Pool;
import com.example.donttouchmytail.framework.Pool.PoolObjectFactory;

/**
 * The KeyboardHandler class implements the OnKeyListener interface so that it can receive key events from a View.
 * 
 * @author bill.mote
 * 
 */
public class KeyboardHandler implements OnKeyListener {

    boolean[] _pressedKeys = new boolean[128];
    Pool<KeyEvent> _keyEventPool;
    List<KeyEvent> _keyEventsBuffer = new ArrayList<KeyEvent>();
    List<KeyEvent> _keyEvents = new ArrayList<KeyEvent>();

    public KeyboardHandler(View view) {
        PoolObjectFactory<KeyEvent> factory = new PoolObjectFactory<KeyEvent>() {
            public KeyEvent createObject() {
                return new KeyEvent();
            }
        };
        _keyEventPool = new Pool<KeyEvent>(factory, 100);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    public boolean onKey(View v, int keyCode, android.view.KeyEvent event) {
        if (event.getAction() == android.view.KeyEvent.ACTION_MULTIPLE) {
            return false;
        }
        synchronized (this) {
            KeyEvent keyEvent = _keyEventPool.newObject();
            keyEvent.keyCode = keyCode;
            keyEvent.keyChar = (char) event.getUnicodeChar();
            if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                keyEvent.type = KeyEvent.KEY_DOWN;
                if (keyCode > 0 && keyCode < 127) {
                    _pressedKeys[keyCode] = true;
                }
            }
            if (event.getAction() == android.view.KeyEvent.ACTION_UP) {
                keyEvent.type = KeyEvent.KEY_UP;
                if (keyCode > 0 && keyCode < 127) {
                    _pressedKeys[keyCode] = false;
                }
            }
            _keyEventsBuffer.add(keyEvent);
        }
        return false;
    }

    /**
     * The next method of our handler is the isKeyPressed() method, which implements the semantics of
     * Input.isKeyPressed(). First, we pass in an integer that specifies the key code (one of the Android
     * KeyEvent.KEYCODE_XXX constants) and returns whether that key is pressed or not. We
     * do this by looking up the state of the key in the pressedKey array after some range checking. Remember, we set
     * the elements of this array in the previous method, which gets called on the UI thread. Since we are working with
     * primitive types again, there’s no need for synchronization.
     * 
     * @param keyCode
     * @return
     */
    public boolean isKeyPressed(int keyCode) {
        if (keyCode < 0 || keyCode > 127) {
            return false;
        }
        return _pressedKeys[keyCode];
    }

    /**
     * The last method of our handler is called getKeyEvents(), and it implements the semantics of the
     * Input.getKeyEvents() method. Once again, we start with a synchronized block and remember that this method will be
     * called from a different thread.
     * 
     * Next, we loop through the keyEvents array and insert all of its KeyEvents into our Pool. Remember, we fetch
     * instances from the Pool in the onKey() method on the UI thread. Here, we reinsert them into the Pool. But isn’t
     * the keyEvents list empty? Yes, but only the first time we invoke that method. To understand why, you have to
     * grasp the rest of the method.
     * 
     * After our mysterious Pool insertion loop, we clear the keyEvents list and fill it with the events in our
     * keyEventsBuffer list. Finally, we clear the keyEventsBuffer list and return the newly filled keyEvents list to
     * the caller.
     * 
     * @return
     */
    public List<KeyEvent> getKeyEvents() {
        synchronized (this) {
            int len = _keyEvents.size();
            for (int i = 0; i < len; i++) {
                _keyEventPool.free(_keyEvents.get(i));
            }
            _keyEvents.clear();
            _keyEvents.addAll(_keyEventsBuffer);
            _keyEventsBuffer.clear();
            return _keyEvents;
        }
    }
}
