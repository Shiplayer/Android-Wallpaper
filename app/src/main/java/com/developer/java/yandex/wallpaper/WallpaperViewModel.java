package com.developer.java.yandex.wallpaper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.SparseArray;

import com.developer.java.yandex.wallpaper.wallpaper.WallpaperLiveData;
import com.developer.java.yandex.wallpaper.wallpaper.entity.Wallpaper;

public class WallpaperViewModel extends ViewModel {
    private WallpaperLiveData mImageMutableLiveData;

    public LiveData<SparseArray<Wallpaper>> getImageFromGallery(Context context){
        if(mImageMutableLiveData == null){
            mImageMutableLiveData = new WallpaperLiveData(context);
        }
        return mImageMutableLiveData;
    }

}
