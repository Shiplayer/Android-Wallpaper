package com.developer.java.yandex.wallpaper.adaptor;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.developer.java.yandex.wallpaper.R;
import com.developer.java.yandex.wallpaper.wallpaper.entity.Wallpaper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class WallpaperAdaptor extends RecyclerView.Adapter<WallpaperAdaptor.ViewHolder> {
    private SparseArray<Wallpaper> mWallpaperArray;
    private View.OnClickListener mImageListener;
    private Context mContext;

    public WallpaperAdaptor(Context context){
        mContext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String pathToWallpaper = mWallpaperArray.get(mWallpaperArray.keyAt(mWallpaperArray.size() - position - 1)).getPath();
        Picasso.get().load(new File(mWallpaperArray.get(mWallpaperArray.keyAt(mWallpaperArray.size() - position - 1)).getPath())).fit().centerCrop()
                .into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(android.R.drawable.stat_notify_error).centerCrop().into(holder.imageView);
                holder.progressBar.setVisibility(View.GONE);
            }
        });

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.title_set_wallpaper);
                    builder.setMessage(R.string.message_set_wallpaper);
                    builder.setPositiveButton(R.string.accept_wallpaper, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            WallpaperManager myWallpaperManager
                                    = WallpaperManager.getInstance(mContext);
                            try {
                                myWallpaperManager.setBitmap(BitmapFactory.decodeFile(pathToWallpaper));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.create().show();

                }
            });
    }

    @Override
    public int getItemCount() {
        return mWallpaperArray == null ? 0 : mWallpaperArray.size();
    }

    public void setData(SparseArray<Wallpaper> mWallpaperArray) {
        this.mWallpaperArray = mWallpaperArray;
        notifyDataSetChanged();
    }

    public void setImageListener(View.OnClickListener onClickListener){
        mImageListener = onClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.wallpaper_image);
            progressBar = itemView.findViewById(R.id.rv_load_wallpaper_pb);
        }
    }
}
