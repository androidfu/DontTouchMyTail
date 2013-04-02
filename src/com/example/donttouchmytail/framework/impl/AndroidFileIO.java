/**
 * 
 */
package com.example.donttouchmytail.framework.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.example.donttouchmytail.framework.FileIO;

/**
 * @author bill.mote
 * 
 */
public class AndroidFileIO implements FileIO {
    Context _context;
    AssetManager _assets;
    String _externalStoragePath;

    public AndroidFileIO(Context context) {
        this._context = context;
        this._assets = context.getAssets();
        this._externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    @Override
    public InputStream readAsset(String filename) throws IOException {
        return _assets.open(filename);
    }

    @Override
    public InputStream readFile(String filename) throws IOException {
        return new FileInputStream(_externalStoragePath + filename);
    }

    @Override
    public OutputStream writeFile(String filename) throws IOException {
        return new FileOutputStream(_externalStoragePath + filename);
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(_context);
    }

}
