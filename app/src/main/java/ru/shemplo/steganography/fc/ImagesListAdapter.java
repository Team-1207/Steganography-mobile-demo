package ru.shemplo.steganography.fc;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ImagesListAdapter extends RecyclerView.Adapter <ImageViewHolder> {

    private final FileChooseFragment fragment;

    private final List <Bitmap> images = new ArrayList <> ();

    private AsyncTask <ImagesListAdapter, Void, Void> scanTask;

    public ImagesListAdapter (FileChooseFragment fragment) {
        this.fragment = fragment;

        scanTask = new ImagesListScanTask ().execute (this);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        return ImageViewHolder.create (fragment.getContext (), parent);
    }

    @Override
    public void onBindViewHolder (@NonNull ImageViewHolder holder, int position) {
        holder.imageView.setImageBitmap (images.get (position));
        holder.imageView.setOnClickListener (v -> {
            scanTask.cancel (true);

            fragment.onImageChosen (images.get (position));
        });
    }

    public Context getContext () {
        return fragment.getContext ();
    }

    @Override
    public int getItemCount () {
        return images.size();
    }

    public void appendItem (Bitmap bitmap) {
        synchronized (images) {
            images.add (bitmap);

            new Handler (Looper.getMainLooper ()).post (() -> {
                notifyItemInserted (images.size () - 1);
            });
        }
    }

}
