package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs;

public class KJSHelper {
    public static Object[] drop(Object[] in, int c) {
        var n = new Object[in.length - c];
        System.arraycopy(in, c, n, 0, in.length - c);
        return n;
    }
}
