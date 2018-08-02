package com.developer.java.yandex.wallpaper.wallpaper;

import android.arch.lifecycle.LiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.developer.java.yandex.wallpaper.wallpaper.entity.Wallpaper;

import java.util.concurrent.Executors;

public class WallpaperLiveData extends LiveData<SparseArray<Wallpaper>> {

    private final ContentResolver mContentResolver;
    private final ContentObserver mContentObserver;
    private boolean mIsFetchingAll = false;

    public WallpaperLiveData(Context context){
        mContentResolver = context.getApplicationContext().getContentResolver();
        mContentObserver = new ContentObserver(new Handler()){
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                applyDataChange(uri);
            }
        };
    }

    @Override
    protected void onActive() {
        super.onActive();

        mContentResolver.registerContentObserver(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true, mContentObserver);
        getAllWallpapers();
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        mContentResolver.unregisterContentObserver(mContentObserver);
    }


    private void applyDataChange(final Uri uri) {
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                SparseArray<Wallpaper> result = getValue() != null ? getValue() : new SparseArray<Wallpaper>();
                Cursor cursor = mContentResolver.query(uri,
                        new String[]{ MediaStore.MediaColumns.DATA,
                                MediaStore.MediaColumns.DISPLAY_NAME,
                                MediaStore.MediaColumns._ID,
                                MediaStore.Images.Media.BUCKET_DISPLAY_NAME }, null,null, null);
                if(cursor != null) {
                    int dataColumn, nameColumn, idColumn;
                    while (cursor.moveToNext()) {
                        idColumn = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                        nameColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                        dataColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                        Wallpaper wallpaper = new Wallpaper(cursor.getInt(idColumn), cursor.getString(nameColumn), cursor.getString(dataColumn));
                        result.put(wallpaper.getId(), wallpaper);
                    }
                    cursor.close();
                }

                postValue(result);
            }
        });
    }

    public void getAllWallpapers() {
        if(!mIsFetchingAll) {
            mIsFetchingAll = true;
            Executors.newCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    SparseArray<Wallpaper> result = new SparseArray<>();
                    Cursor cursor = mContentResolver.query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[]{ MediaStore.MediaColumns.DATA,
                                    MediaStore.MediaColumns.DISPLAY_NAME,
                                    MediaStore.MediaColumns._ID,
                                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME }, null,null, null);
                    if(cursor != null) {
                        int dataColumn, nameColumn, idColumn;
                        while (cursor.moveToNext()) {
                            idColumn = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                            nameColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                            dataColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                            Wallpaper wallpaper = new Wallpaper(cursor.getInt(idColumn), cursor.getString(nameColumn), cursor.getString(dataColumn));
                            result.put(wallpaper.getId(), wallpaper);
                        }
                        cursor.close();
                    }

                    postValue(result);
                    mIsFetchingAll = false;
                }
            });
        }
    }
}
