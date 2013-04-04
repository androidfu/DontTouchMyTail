package com.example.donttouchmytail.framework.impl;

import android.graphics.Bitmap;

import com.example.donttouchmytail.framework.Graphics.PixmapFormat;
import com.example.donttouchmytail.framework.Pixmap;

public class AndroidPixmap implements Pixmap {

    Bitmap _bitmap;
    PixmapFormat _format;

    public AndroidPixmap(Bitmap bitmap, PixmapFormat format) {
        this._bitmap = bitmap;
        this._format = format;
    }

    @Override
    public int getWidth() {
        return _bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return _bitmap.getHeight();
    }

    @Override
    public PixmapFormat getFormat() {
        return _format;
    }

    @Override
    public void dispose() {
        _bitmap.recycle();
    }

}
