package ru.shemplo.steganography;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.io.OutputStream;

import ru.shemplo.steganography.util.T3;

public class ImageSaveTask extends AsyncTask <T3 <Context, Bitmap, String>, Void, Void> {

    @Override
    protected Void doInBackground (T3 <Context, Bitmap, String>... bitmaps) {
        ContentResolver contentResolver = bitmaps [0].a.getContentResolver ();
        Bitmap bitmap = bitmaps [0].b;

        ContentValues cv = new ContentValues ();
        cv.put (MediaStore.MediaColumns.DISPLAY_NAME, Math.random () + ".png");
        cv.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");

        Uri imageURI = contentResolver.insert (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        Log.i ("SA", "Picture path: " + imageURI.toString ());
        try (OutputStream os = contentResolver.openOutputStream (imageURI)) {
            SteganographyEngine.getInstance ().encode (bitmap, bitmaps [0].c);
            bitmap.compress (Bitmap.CompressFormat.PNG, 100, os);
        } catch (IOException ioe) {
            Log.e ("SA", "Failed to save modified image", ioe);
        }

        return null;
    }

}
