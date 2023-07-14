package ru.shemplo.steganography;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.List;

public class SteganographyEngine {

    private static volatile SteganographyEngine instance;

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

    public String decode (Bitmap bitmap) {
        /*
        Log.i ("SE", bitmap.getWidth() + "x" + bitmap.getHeight());
        int [][] pixels = {
            {0, 0},
            {1, 0},
            {2, 0},
            {3, 0},
            {4, 0},
            {5, 0},
            {6, 0},

            {0, 1},
            {0, 2},
            {0, 3},
            {0, 4},
            {0, 5},
            {0, 6},

            {0, bitmap.getHeight() - 1},
            {bitmap.getWidth() - 1, 0},
            {bitmap.getWidth() - 1, bitmap.getHeight() - 1}
        };
        for (int [] option : pixels) {
            Log.i ("SE", "(" + option [0] + ", " + option [1] + ")");
            int pixel = bitmap.getPixel (option [0], option [1]);
            //Log.i ("SE", "    " + pixel);
            Log.i ("SE", "    " + Integer.toBinaryString(pixel));

            StringBuilder sb = new StringBuilder ();
            for (int i = 3; i >= 0; i--) {
                sb.append (String.format ("0x%2h", (pixel >>> (i * 8)) & 0xFF)).append (" ");
            }
            Log.i ("SE", "    " + sb.toString ());
        }
        Log.i ("SE", bitmap.getPixel (0, bitmap.getHeight() - 1) + " " + Integer.toBinaryString(bitmap.getPixel (0, bitmap.getHeight() - 1)));
        Log.i ("SE", bitmap.getPixel (bitmap.getWidth() - 1, 0) + " " + Integer.toBinaryString(bitmap.getPixel (bitmap.getWidth() - 1, 0)));
        Log.i ("SE", bitmap.getPixel (bitmap.getWidth() - 1, bitmap.getHeight() - 1) + " " + Integer.toBinaryString(bitmap.getPixel (bitmap.getWidth() - 1, bitmap.getHeight() - 1)));
        */

        /*
        StringBuilder sb = new StringBuilder ();
        for (int i = 0; i < 5; i++) {
            int x = (i * 8) / 3;

            Log.i ("SE", "Take pixel (" + x + ", 0), t=" + ((i * 8) % 3) + ", initial");
            int pixel = bitmap.getPixel (x, 0);

            int character = 0;
            for (int j = 0, t = (i * 8) % 3; j < 8; j++) {
                character <<= 1;
                character += (pixel >>> ((2 - t) * 8)) & 0b1;

                t = (t + 1) % 3;
                if (t == 0) {
                    x++;
                    Log.i ("SE", "Take pixel (" + x + ", 0)");
                    pixel = bitmap.getPixel (x, 0);
                }
            }

            sb.append (Character.toChars (character) [0]);
        }
        */

        StringBuilder sb = new StringBuilder ();
        for (int i = 0; i < 20; i++) {
            int a = bitmap.getPixel (i * 2, 0), b = bitmap.getPixel (i * 2 + 1, 0);
            Log.i ("SE", "a: " + Integer.toBinaryString (a));
            Log.i ("SE", "b: " + Integer.toBinaryString (b));

            StringBuilder tsba = new StringBuilder ();
            StringBuilder tsbb = new StringBuilder ();
            for (int d = 3; d >= 0; d--) {
                int ta = (a >>> (d * 8)) & 0xFF, tb = (b >>> (d * 8)) & 0xFF;
                tsba.append (String.format ("0x%2h (%3d)  ", ta, ta)).append (" ");
                tsbb.append (String.format ("0x%2h (%3d)  ", tb, tb)).append (" ");
            }
            Log.i ("SE", "ARGB a: " + tsba.toString ());
            Log.i ("SE", "ARGB b: " + tsbb.toString ());

            int character = takeAndShift (a, 2, 7)
                          | takeAndShift (a, 1, 6)
                          | takeAndShift (a, 0, 5)
                          | takeAndShift (a, 3, 4)
                          | takeAndShift (b, 2, 3)
                          | takeAndShift (b, 1, 2)
                          | takeAndShift (b, 0, 1)
                          | takeAndShift (b, 3, 0);
            //Log.i ("SE", "7: " + String.format ("%8s", Integer.toBinaryString (takeAndShift (a, 0, 7))));
            //Log.i ("SE", "6: " + String.format ("%8s", Integer.toBinaryString (takeAndShift (a, 3, 6))));
            //Log.i ("SE", "5: " + String.format ("%8s", Integer.toBinaryString (takeAndShift (a, 2, 5))));
            //Log.i ("SE", "4: " + String.format ("%8s", Integer.toBinaryString (takeAndShift (a, 1, 4))));
            //Log.i ("SE", "3: " + String.format ("%8s", Integer.toBinaryString (takeAndShift (b, 0, 3))));
            //Log.i ("SE", "2: " + String.format ("%8s", Integer.toBinaryString (takeAndShift (b, 3, 2))));
            //Log.i ("SE", "1: " + String.format ("%8s", Integer.toBinaryString (takeAndShift (b, 2, 1))));
            //Log.i ("SE", "0: " + String.format ("%8s", Integer.toBinaryString (takeAndShift (b, 1, 0))));
            Log.i ("SE", "с: " + String.format ("%8s (%d)", Integer.toBinaryString (character & 0xFF), character));

            sb.append (Character.toChars (character & 0xFF) [0]);
            Log.i ("SE", "r: " + sb);
        }

        return sb.toString ();
    }

    private static int takeAndShift (int number, int take, int shift) {
        return (((number >>> (take * 8)) & 0b1) << shift);
    }

}
