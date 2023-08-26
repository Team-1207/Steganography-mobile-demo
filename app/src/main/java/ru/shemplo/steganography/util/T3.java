package ru.shemplo.steganography.util;

public class T3 <A, B, C> {

    public final A a;
    public  final B b;
    public final C c;

    public T3 (A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <A, B, C> T3 <A, B, C> of (A a, B b, C c) {
        return new T3 <> (a, b, c);
    }

}
