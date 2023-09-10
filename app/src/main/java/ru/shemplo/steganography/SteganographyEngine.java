package ru.shemplo.steganography;

import android.graphics.Bitmap;

import java.nio.charset.StandardCharsets;

public class SteganographyEngine {

    private static volatile SteganographyEngine instance;

    private static final int SIZE_PIXELS = 3;
    private static final int SIZE_BITS = SIZE_PIXELS * 4;

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

    private static final int [] bitsOrder = {2, 1, 0, 3};

    public void encode (Bitmap bitmap, String message) {
        byte [] bytes = message.getBytes (StandardCharsets.UTF_8);
        if (bytes.length >= 1 << SIZE_BITS) {
            throw new IllegalArgumentException ("Message is too big for steganography");
        }

        for (int i = 0; i < SIZE_PIXELS; i++) {
            for (int j = 0; j < 4; j++) {
                int bit = (bytes.length >>> ((SIZE_PIXELS - i) * 4 - j - 1)) & 0b1;
                setPixelBit (bitmap, i, bitsOrder [j], bit);
            }
        }

        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 4; k++) {
                    int bit = (bytes [i] >>> (7 - j * 4 - k)) & 1;
                    setPixelBit (bitmap, i * 2 + j + SIZE_PIXELS, bitsOrder [k], bit);
                }
            }
        }
    }

    public String decode (Bitmap bitmap) {
        StringBuilder sb = new StringBuilder ();

        int [] sizePixels = new int [SIZE_PIXELS];
        for (int i = 0; i < SIZE_PIXELS; i++) {
            sizePixels [i] = getPixel (bitmap, i);
        }

        int maxData = (1 << (SIZE_BITS + 1)) - 1;
        int length = Math.min (takeNumber (sizePixels), maxData);

        byte [] bytes = new byte [length];
        for (int i = 0; i < length; i++) {
            int a = getPixel (bitmap, i * 2 + SIZE_PIXELS), b = getPixel (bitmap, i * 2 + 1 + SIZE_PIXELS);
            bytes [i] = (byte) takeNumber (a, b);
        }

        sb.append (new String (bytes));

        return sb.toString ();
    }

    private static void setPixel (Bitmap bitmap, int index, int pixel) {
        bitmap.setPixel (index % bitmap.getWidth (), index / bitmap.getWidth (), pixel);
    }

    private static void setPixelBit (Bitmap bitmap, int pixelIndex, int bitIndex, int bit) {
        int pixel = getPixel (bitmap, pixelIndex);
        pixel = ((Integer.rotateRight (pixel, bitIndex * 8) >>> 1) << 1) | bit;
        pixel = Integer.rotateLeft (pixel, bitIndex * 8);

        setPixel (bitmap, pixelIndex, pixel);
    }

    private static int getPixel (Bitmap bitmap, int index) {
        return bitmap.getPixel (index % bitmap.getWidth (), index / bitmap.getWidth ());
    }

    private static int takeAndShift (int number, int take, int shift) {
        return (((number >>> (take * 8)) & 0b1) << shift);
    }

    private static int takeNumber (int ... rgbas) {
        int result = 0;
        for (int i = 0; i < rgbas.length; i++) {
            for (int j = 0; j < bitsOrder.length; j++) {
                int shift = (rgbas.length - i) * 4 - j - 1;
                int bit = takeAndShift (rgbas [i], bitsOrder [j], shift);
                result |= bit;
            }
        }

        return result;
    }

}
