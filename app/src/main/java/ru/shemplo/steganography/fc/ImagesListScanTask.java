package ru.shemplo.steganography.fc;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

public class ImagesListScanTask extends AsyncTask <ImagesListAdapter, Void, Void> {

    private static final String [] CAMERA_PROJECTION = {
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    };

    @Override
    protected Void doInBackground (ImagesListAdapter... adapters) {
        ContentResolver contentResolver = adapters [0].getContext ().getContentResolver ();
        Uri contentURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Log.d ("ILST", "Scan task started...");
        Cursor cursor = contentResolver.query (
            contentURI, CAMERA_PROJECTION, null, null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        Log.d ("ILST", "Files query is over. Processing files...");
        if (cursor != null) {
            if (cursor.moveToFirst ()) {
                int idColumn = cursor.getColumnIndexOrThrow (MediaStore.Images.Media._ID);
                int amount = 0;
                do {
                    Uri imageURI = ContentUris.withAppendedId (contentURI, cursor.getLong (idColumn));
                    Log.d ("ILST", amount + " - id " + cursor.getLong (idColumn) + ", URI: " + imageURI);
                    try (ParcelFileDescriptor pfd = contentResolver.openFileDescriptor (imageURI, "r")) {
                        if (pfd.getStatSize () < 15_000_000) {
                            Bitmap bitmap = BitmapFactory.decodeFileDescriptor (pfd.getFileDescriptor ());
                            if (bitmap.getAllocationByteCount () < 50_000_000) {
                                adapters [0].appendItem (bitmap);
                            } else {
                                Log.w ("ILST", "Bitmap to big to be used: " + bitmap.getAllocationByteCount () + " bytes");
                            }
                        } else {
                            Log.w ("ILST", "Image to big to be used: " + pfd.getStatSize () + " bytes");
                        }
                    } catch (IOException ioe) {
                        Log.e ("ILST", "Failed to read image descriptor", ioe);
                    }
                } while (cursor.moveToNext () && amount++ < 50);
            }

            Log.d ("ILST", "Processing of files is finished. Closing cursor...");
            cursor.close ();
        }

        Log.d ("ILST", "Processing of files is finished");
        return null;
    }

}
