package com.example.donttouchmytail.framework.impl;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.example.donttouchmytail.framework.Music;

/**
 * The AndroidMusic class implements the Music interface as well as the OnCompletionListener interface. In Chapter 4, we
 * briefly defined this interface as a means of informing ourselves about when a MediaPlayer has stopped playing back a
 * music file. If this happens, the MediaPlayer needs to be prepared again before we can invoke any of the other
 * methods. The method OnCompletionListener.onCompletion() might be called in a separate thread, and since we set the
 * isPrepared member in this method, we have to make sure that it is safe from concurrent modifications.
 * 
 * @author bill.mote
 * 
 */
public class AndroidMusic implements Music, OnCompletionListener {

    private static final String TAG = AndroidMusic.class.getSimpleName();
    MediaPlayer _mediaPlayer;
    boolean _isPrepared = false;

    public AndroidMusic(AssetFileDescriptor assetDescriptor) {
        _mediaPlayer = new MediaPlayer();
        try {
            _mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(), assetDescriptor.getStartOffset(), assetDescriptor.getLength());
            _mediaPlayer.prepare();
            _isPrepared = true;
            _mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load music");
        }
    }

    /**
     * If we are already playing, we simply return from the function. Next we have a mighty try. . .catch block within
     * which we check to see if the MediaPlayer is already prepared based on our flag; we prepare it if needed. If all
     * goes well, we call the MediaPlayer.start() method, which will start the playback. This is conducted in a
     * synchronized block, since we are using the isPrepared flag, which might get set on a separate thread because we
     * are implementing the OnCompletionListener interface. In case something goes wrong, we throw an unchecked
     * RuntimeException.
     */
    @Override
    public void play() {
        if (_mediaPlayer.isPlaying()) {
            return;
        }
        try {
            synchronized (this) {
                if (!_isPrepared) {
                    _mediaPlayer.prepare();
                }
                _mediaPlayer.start();
            }
        } catch (IllegalStateException ise) {
            Log.e(TAG, "IllegalStateException in play()", ise);
        } catch (IOException ioe) {
            Log.e(TAG, "IOException in play()", ioe);
        }
    }

    /**
     * The stop() method stops the MediaPlayer and sets the isPrepared flag in a synchronized block.
     */
    @Override
    public void stop() {
        _mediaPlayer.stop();
        synchronized (this) {
            _isPrepared = false;
        }
    }

    @Override
    public void pause() {
        if (_mediaPlayer.isPlaying()) {
            _mediaPlayer.pause();
        }
    }

    @Override
    public void setLooping(boolean looping) {
        _mediaPlayer.setLooping(looping);
    }

    @Override
    public void setVolume(float volume) {
        _mediaPlayer.setVolume(volume, volume);
    }

    @Override
    public boolean isPlaying() {
        return _mediaPlayer.isPlaying();
    }

    /**
     * The isStopped() method uses the isPrepared flag, which indicates if the MediaPlayer is stopped. This is
     * something MediaPlayer.isPlaying() does not necessarily tell us since it returns false if the MediaPlayer is
     * paused but not stopped.
     */
    @Override
    public boolean isStopped() {
        return !_isPrepared;
    }

    @Override
    public boolean isLooping() {
        return _mediaPlayer.isLooping();
    }

    /**
     * The dispose() method checks if the MediaPlayer is still playing and, if so, stops it. Otherwise, the call to
     * MediaPlayer.release() will throw a RuntimeException.
     */
    @Override
    public void dispose() {
        if (_mediaPlayer.isPlaying()) {
            _mediaPlayer.stop();
        }
        _mediaPlayer.release();
    }

    /**
     * Finally, there’s the OnCompletionListener.onCompletion() method that is implemented by the AndroidMusic class.
     * All it does is set the isPrepared flag in a synchronized block so that the other methods don’t start throwing
     * exceptions out of the blue.
     */
    @Override
    public void onCompletion(MediaPlayer player) {
        synchronized (this) {
            _isPrepared = false;
        }
    }

}
