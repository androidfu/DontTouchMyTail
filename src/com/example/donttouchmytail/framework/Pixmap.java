package com.example.donttouchmytail.framework;

import com.example.donttouchmytail.framework.Graphics.PixmapFormat;

public interface Pixmap {

    public int getWidth();

    public int getHeight();

    public PixmapFormat getFormat();

    public void dispose();
}
