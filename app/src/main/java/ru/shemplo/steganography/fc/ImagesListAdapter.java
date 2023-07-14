package ru.shemplo.steganography.fc;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.style.IconMarginSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagesListAdapter extends RecyclerView.Adapter <ImageViewHolder> {

    private final FileChooseFragment fragment;

    private final List <Bitmap> images = new ArrayList <> ();

    public ImagesListAdapter (FileChooseFragment fragment) {
        this.fragment = fragment;

        images.addAll (getCameraImages ());
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
            fragment.onImageChosen (images.get (position));
        });
    }

    @Override
    public int getItemCount () {
        return images.size();
    }

    private static final String [] CAMERA_PROJECTION = {
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    };

    private List <Bitmap> getCameraImages () {
        Cursor cursor = fragment.getContext ().getContentResolver ().query (
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            CAMERA_PROJECTION, null, null,
            MediaStore.Images.Media.DATE_TAKEN + " DESC"
        );

        List <Bitmap> result = new ArrayList <> ();
        if (cursor.moveToFirst ()) {
            int bucketColumn = cursor.getColumnIndexOrThrow (MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dataColumn = cursor.getColumnIndexOrThrow (MediaStore.Images.Media.DATA);
            int amount = 0;
            do {
                if (cursor.getString (dataColumn).contains ("secret")) {
                    Log.d ("ILA", "Bucket " + cursor.getString (bucketColumn));
                    Log.d ("ILA", "Data " + cursor.getString (dataColumn));
                    result.add (BitmapFactory.decodeFile (cursor.getString (dataColumn)));
                }
            } while (cursor.moveToNext () && amount++ < 300);
        }

        //Log.d ("ILA", result.subList (0, Math.min (10, result.size ())).toString ());
        Log.d ("ILA", result.toString ());
        cursor.close ();
        return result;
    }

}
