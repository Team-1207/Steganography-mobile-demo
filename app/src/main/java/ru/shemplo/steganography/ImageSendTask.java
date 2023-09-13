package ru.shemplo.steganography;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

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
            os.write (filename);

            SteganographyEngine.getInstance ().encode (bitmap, bitmaps [0].b);

            byte [] tmpFile;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream ()) {
                bitmap.compress (Bitmap.CompressFormat.PNG, 100, baos);
                tmpFile = baos.toByteArray ();
            }

            byte [] exifBytes;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream ()) {
                byte [] message = "Non-secret image description\0".getBytes (StandardCharsets.US_ASCII);
                byte [] copyright = "Team-1207,2023\0".getBytes (StandardCharsets.US_ASCII);

                baos.write ("EXIF".getBytes (StandardCharsets.US_ASCII));
                //baos.write (new byte [] {0x49, 0x49, 0x00, 0x2A}); // Little endian bits order (II) + magic number
                baos.write (new byte [] {0x4D, 0x4D, 0x00, 0x2A}); // Big endian bits order (MM) + magic number
                baos.write (new byte [] {0x00, 0x00, 0x00, 0x08}); // First IDF chunk offset
                baos.write (new byte [] {0x00, 0x14}); // Interoperability number

                baos.write (new byte [] {0x01, 0x0E, 0x00, 0x02}); // Tag `ImageDescription` ID (2) + Tag type (2)
                baos.write (new byte [] {0x00, 0x00, 0x00, (byte) message.length}); // Message length
                baos.write (new byte [] {0x00, 0x00, 0x00, 0x26}); // Offset where message is written

                baos.write (new byte [] {(byte) 0x82, (byte) 0x98, 0x00, 0x02}); // Tag `Copyright` ID (2) + Tag type (2)
                baos.write (new byte [] {0x00, 0x00, 0x00, (byte) copyright.length}); // Message length
                baos.write (new byte [] {0x00, 0x00, 0x00, (byte) (0x26 + message.length)}); // Offset where message is written

                baos.write (new byte [] {0x00, 0x00, 0x00, 0x00}); // Next IFD chunk offset (0 means end)
                baos.write (message);
                baos.write (copyright);
                exifBytes = baos.toByteArray ();
            }

            CRC32 exifCRC32 = new CRC32 ();
            exifCRC32.update (exifBytes, 4, exifBytes.length - 4);
            int exifCRC32Value = (int) exifCRC32.getValue ();

            Log.i ("ISeT", "IHDR length: " + tmpFile [11]);
            os.write (tmpFile, 0, 8);                // write PNG signature
            os.write (tmpFile, 8, 4);                // write length of IHDR tags
            os.write (tmpFile, 12,8 + tmpFile [11]); // write IHDR tags with CRC32 of them
            writeInt (os, exifBytes.length - 4);       // write length of eXIF data
            os.write (exifBytes);                            // write eXIF data
            writeInt (os, exifCRC32Value);                   // write CRC32 value of eXIF chunk
            os.write (tmpFile, 20 + tmpFile [11], tmpFile.length - 20 - tmpFile [11]);
            os.flush ();
        } catch (IOException ioe) {
            Log.e ("ISeT", "Failed to patch and send modified image", ioe);
        }

        return null;
    }

    private void writeInt (OutputStream os, int value) throws IOException {
        os.write ((value >>> 24) & 0xFF);
        os.write ((value >>> 16) & 0xFF);
        os.write ((value >>> 8) & 0xFF);
        os.write ((value >>> 0) & 0xFF);
    }

}
