package com.example.donttouchmytail.framework.impl;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.donttouchmytail.framework.Audio;
import com.example.donttouchmytail.framework.Music;
import com.example.donttouchmytail.framework.Sound;

public class AndroidAudio implements Audio {

    AssetManager _assets;
    SoundPool _soundPool;

    public AndroidAudio(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this._assets = activity.getAssets();
        this._soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
    }

    @Override
    public Music newMusic(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = _assets.openFd(filename);
            return new AndroidMusic(assetDescriptor);
        } catch (IOException ioe) {
            throw new RuntimeException(String.format("Couldn't load music '%s'", filename));
        }
    }

    @Override
    public Sound newSound(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = _assets.openFd(filename);
            int soundId = _soundPool.load(assetDescriptor, 0);
            return new AndroidSound(_soundPool, soundId);
        } catch (IOException ioe) {
            throw new RuntimeException(String.format("Couldn't load sound '%s'", filename));
        }
    }

}
