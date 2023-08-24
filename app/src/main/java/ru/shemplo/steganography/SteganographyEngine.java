package ru.shemplo.steganography;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class SteganographyEngine {

    private static volatile SteganographyEngine instance;

    private static int SIZE_BITS = 10;

    public static SteganographyEngine getInstance () {
        if (instance == null) {
            synchronized (SteganographyEngine.class) {
                if (instance == null) {
                    instance = new SteganographyEngine ();
                }
            }
        }

        return instance;
    }

    public void encode (Bitmap bitmap, String message) {
        byte [] chars = message.getBytes (StandardCharsets.UTF_8);
        if (
            chars.length >= 1 << SIZE_BITS
            || chars.length * 8 >= (bitmap.getWidth () * bitmap.getHeight () - SIZE_BITS)
        ) {
            throw new IllegalArgumentException ("Message is too big for steganography");
        }

        for (int i = 0; i < SIZE_BITS; i++) {
            setPixelBit (bitmap, i, (chars.length >>> (15 - i)) & 1);
        }

        for (int i = 0; i < chars.length; i++) {
            for (int b = 0; b < 8; b++) {
                setPixelBit (bitmap, i * 8 + b + SIZE_BITS, (chars [i] >>> (7 - i)) & 1);
            }
        }
    }

    public String decode (Bitmap bitmap) {
        StringBuilder sb = new StringBuilder ();
        for (int i = 0; i < 20; i++) {
            int a = bitmap.getPixel (i * 2, 0), b = bitmap.getPixel (i * 2 + 1, 0);

            StringBuilder tsba = new StringBuilder ();
            StringBuilder tsbb = new StringBuilder ();
            for (int d = 3; d >= 0; d--) {
                int ta = (a >>> (d * 8)) & 0xFF, tb = (b >>> (d * 8)) & 0xFF;
                tsba.append (String.format ("0x%2h (%3d)  ", ta, ta)).append (" ");
                tsbb.append (String.format ("0x%2h (%3d)  ", tb, tb)).append (" ");
            }

            int character = takeAndShift (a, 2, 7)
                          | takeAndShift (a, 1, 6)
                          | takeAndShift (a, 0, 5)
                          | takeAndShift (a, 3, 4)
                          | takeAndShift (b, 2, 3)
                          | takeAndShift (b, 1, 2)
                          | takeAndShift (b, 0, 1)
                          | takeAndShift (b, 3, 0);
            Log.i ("SE", "с: " + String.format ("%8s (%d)", Integer.toBinaryString (character & 0xFF), character));

            sb.append (Character.toChars (character & 0xFF) [0]);
            Log.i ("SE", "r: " + sb);
        }

        return sb.toString ();
    }

    private static int getPixel (Bitmap bitmap, int index) {
        return bitmap.getPixel (index % bitmap.getWidth (), index / bitmap.getWidth ());
    }

    private static void setPixel (Bitmap bitmap, int index, int pixel) {
        bitmap.setPixel (index % bitmap.getWidth (), index / bitmap.getWidth (), pixel);
    }

    private static void setPixelBit (Bitmap bitmap, int index, int bit) {
        int pixel = getPixel (bitmap, index) & 0xFE; // clear last bit
        setPixel (bitmap, index, pixel | bit);
    }

    private static int takeAndShift (int number, int take, int shift) {
        return (((number >>> (take * 8)) & 0b1) << shift);
    }

}
