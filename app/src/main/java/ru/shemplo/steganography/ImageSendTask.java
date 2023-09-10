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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import ru.shemplo.steganography.util.T3;

public class ImageSendTask extends AsyncTask <T3 <Bitmap, String, InetAddress>, Void, Void> {

    @Override
    @SafeVarargs
    protected final Void doInBackground(T3 <Bitmap, String, InetAddress>... bitmaps) {
        InetAddress address = bitmaps [0].c;
        Bitmap bitmap = bitmaps [0].a;

        Log.i ("ISeT", "Sending modified image to " + address + ":1207...");
        try (
            Socket socket = new Socket (address, 1207);
            OutputStream os = socket.getOutputStream();
        ) {
            byte [] filename = (Math.random () + ".png").getBytes (StandardCharsets.UTF_8);
            os.write (filename.length);
            os.write(filename);

            SteganographyEngine.getInstance ().encode (bitmap, bitmaps [0].b);
            bitmap.compress (Bitmap.CompressFormat.PNG, 100, os);
        } catch (IOException ioe) {
            Log.e ("ISeT", "Failed to send modified image", ioe);
        }

        return null;
    }

}
