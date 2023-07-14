package ru.shemplo.steganography.fc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.shemplo.steganography.R;

public class ImageViewHolder extends RecyclerView.ViewHolder {

    public final ImageView imageView;

    private ImageViewHolder (@NonNull View view) {
        super (view);

        imageView = view.findViewById (R.id.image_tile_preview);
    }

    public static ImageViewHolder create (Context context, ViewGroup parent) {
        /*
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams (
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );

        ImageView iv = new ImageView (context);
        //iv.setScaleType (ImageView.ScaleType.FIT_START);
        //iv.setMaxWidth ();
        //iv.setMaxHeight (50);
        //iv.setMinimumHeight (100);
        //iv.setMinimumWidth (200);
        //iv.setBackgroundColor (0xFF0000);
        iv.setLayoutParams (lp);
        */
        View view = LayoutInflater.from (context).inflate (R.layout.image_tile, parent, false);

        return new ImageViewHolder (view);
    }

}
