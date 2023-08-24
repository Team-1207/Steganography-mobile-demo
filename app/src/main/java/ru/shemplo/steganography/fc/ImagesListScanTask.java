package ru.shemplo.steganography.fc;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

import java.util.List;

public class ImagesListScanTask extends AsyncTask <ImagesListAdapter, Void, Void> {

    private static final String [] CAMERA_PROJECTION = {
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    };

    @Override
    protected Void doInBackground (ImagesListAdapter... adapters) {
        Cursor cursor = adapters [0].getContext ().getContentResolver ().query (
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            CAMERA_PROJECTION, null, null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            if (cursor.moveToFirst ()) {
                int bucketColumn = cursor.getColumnIndexOrThrow (MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int dataColumn = cursor.getColumnIndexOrThrow (MediaStore.Images.Media.DATA);
                int amount = 0;
                do {
                    Log.d ("ILA", "Bucket " + cursor.getString (bucketColumn));
                    Log.d ("ILA", "Data " + cursor.getString (dataColumn));

                    adapters [0].appendItem (BitmapFactory.decodeFile (cursor.getString (dataColumn)));
                } while (cursor.moveToNext () && amount++ < 300);
            }

            cursor.close ();
        }

        return null;
    }

}
