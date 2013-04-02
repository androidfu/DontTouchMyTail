package com.example.donttouchmytail.framework.impl;

import android.media.SoundPool;

import com.example.donttouchmytail.framework.Sound;

public class AndroidSound implements Sound {

    int _soundId;
    SoundPool _soundPool;

    public AndroidSound(SoundPool soundPool, int soundId) {
        this._soundId = soundId;
        this._soundPool = soundPool;
    }

    @Override
    public void play(float volume) {
        _soundPool.play(_soundId, volume, volume, 0, 0, 1);
    }

    @Override
    public void dispose() {
        _soundPool.unload(_soundId);
    }

}
