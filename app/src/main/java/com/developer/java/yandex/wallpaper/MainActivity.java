package com.developer.java.yandex.wallpaper;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;

import com.developer.java.yandex.wallpaper.adaptor.WallpaperAdaptor;
import com.developer.java.yandex.wallpaper.wallpaper.entity.Wallpaper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_CODE = 1001;
    private boolean mPermissionIsGranted = false;
    private WallpaperViewModel mWallpaperViewModel;
    private WallpaperAdaptor mWallpaperAdaptor;
    private ProgressBar mLoadWallpaperBar;
    private View mWallpaperView;
    private Dialog mAskDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWallpaperViewModel = ViewModelProviders.of(this).get(WallpaperViewModel.class);
        init();
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.rv_wallpapers);
        mWallpaperAdaptor = new WallpaperAdaptor(MainActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mWallpaperAdaptor);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mLoadWallpaperBar = findViewById(R.id.wallpaperBar);
        mLoadWallpaperBar.setVisibility(View.VISIBLE);
        mWallpaperView = recyclerView;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Не достаточно прав");
        builder.setMessage(R.string.error);
        builder.setPositiveButton(R.string.repeat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
        });
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finishAffinity();
            }
        });
        mAskDialog = builder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            else if (!mAskDialog.isShowing())
                mAskDialog.show();
        } else {
            mLoadWallpaperBar.setVisibility(View.GONE);
            setDataInAdapter();
            mWallpaperView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        mPermissionIsGranted = true;
                        setDataInAdapter();
                        mWallpaperView.setVisibility(View.VISIBLE);
                    } else {
                        mPermissionIsGranted = false;
                    }
                    mLoadWallpaperBar.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    public void setDataInAdapter(){
        mWallpaperViewModel.getImageFromGallery(getApplicationContext()).observe(this, new Observer<SparseArray<Wallpaper>>() {
            @Override
            public void onChanged(@Nullable SparseArray<Wallpaper> wallpaperSparseArray) {
                Log.w(TAG, "set Data in adaptor");
                mWallpaperAdaptor.setData(wallpaperSparseArray);
            }
        });
    }
}
