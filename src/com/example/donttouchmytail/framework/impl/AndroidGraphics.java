package com.example.donttouchmytail.framework.impl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import com.example.donttouchmytail.framework.Graphics;
import com.example.donttouchmytail.framework.Pixmap;

public class AndroidGraphics implements Graphics {

    AssetManager _assets;
    Bitmap _frameBuffer;
    Canvas _canvas;
    Paint _paint;
    Rect _srcRect = new Rect();
    Rect _dstRect = new Rect();

    public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
        this._assets = assets;
        this._frameBuffer = frameBuffer;
        this._canvas = new Canvas(frameBuffer);
        this._paint = new Paint();
    }

    @Override
    public Pixmap newPixmap(String filename, PixmapFormat format) {
        Config config = null;
        if (format == PixmapFormat.RGB565) {
            config = Config.RGB_565;
        } else if (format == PixmapFormat.ARGB4444) {
            config = Config.ARGB_4444;
        } else {
            config = Config.ARGB_8888;
        }
        Options options = new Options();
        options.inPreferredConfig = config;
        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = _assets.open(filename);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null) {
                throw new RuntimeException(String.format("Couldn't load bitmat from asset '%s'", filename));
            }
        } catch (IOException ioe) {
            throw new RuntimeException(String.format("Couldn't load bitmat from asset '%s'", filename));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                }
            }
        }
        if (bitmap.getConfig() == Config.RGB_565) {
            format = PixmapFormat.RGB565;
        } else if (bitmap.getConfig() == Config.ARGB_4444) {
            format = PixmapFormat.ARGB4444;
        } else {
            format = PixmapFormat.ARGB8888;
        }
        return new AndroidPixmap(bitmap, format);
    }

    /**
     * The clear() method extracts the red, green, and blue components of the specified 32-bit ARGB color parameter and
     * calls the Canvas.drawRGB() method, which clears our artificial framebuffer with that color. This method ignores
     * any alpha value of the specified color, so we don’t have to extract it.
     */
    @Override
    public void clear(int color) {
        _canvas.drawRGB((color & 0xff0000) >> 16, (color & 0x00ff00) >> 8, (color & 0x0000ff));
    }

    @Override
    public void drawPixel(int x, int y, int color) {
        _paint.setColor(color);
        _canvas.drawPoint(x, y, _paint);
    }

    @Override
    public void drawLine(int x, int y, int x2, int y2, int color) {
        _paint.setColor(color);
        _canvas.drawLine(x, y, x2, y2, _paint);
    }

    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        _paint.setColor(color);
        _paint.setStyle(Style.FILL);
        _canvas.drawRect(x, y, x + width - 1, y + width - 1, _paint);
    }

    /**
     * The drawPixmap() method, which allows us to draw a portion of a Pixmap, sets up the source and destination of the
     * Rect members that are used in the actual drawing call. As with drawing a rectangle, we have to translate the x
     * and y coordinates together with the width and height to the top-left and bottom-right corners. Again, we have to
     * subtract 1, or else we will overshoot by 1 pixel. Next, we perform the actual drawing via the Canvas.drawBitmap()
     * method, which will automatically do the blending if the Pixmap we draw has a PixmapFormat.ARGB4444 or
     * a PixmapFormat.ARGB8888 color depth. Note that we have to cast the Pixmap parameter to an AndroidPixmap in order
     * to fetch the bitmap member for drawing with the Canvas. That’s a bit complicated, but we can be sure that the
     * Pixmap instance that is passed in will be an AndroidPixmap.
     */
    @Override
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {
        _srcRect.left = srcX;
        _srcRect.top = srcY;
        _srcRect.right = srcX + srcWidth - 1;
        _srcRect.bottom = srcY + srcHeight - 1;

        _dstRect.left = x;
        _dstRect.top = y;
        _dstRect.right = x + srcWidth - 1;
        _dstRect.bottom = y + srcHeight - 1;

        _canvas.drawBitmap(((AndroidPixmap) pixmap)._bitmap, _srcRect, _dstRect, null);
    }

    /**
     * The second drawPixmap() method draws the complete Pixmap to the artificial framebuffer at the given coordinates.
     * Again, we must do some casting to get to the Bitmap member of the AndroidPixmap.
     */
    @Override
    public void drawPixmap(Pixmap pixmap, int x, int y) {
        _canvas.drawBitmap(((AndroidPixmap) pixmap)._bitmap, x, y, null);
    }

    @Override
    public int getWidth() {
        return _frameBuffer.getWidth();
    }

    @Override
    public int getHeight() {
        return _frameBuffer.getHeight();
    }

}
