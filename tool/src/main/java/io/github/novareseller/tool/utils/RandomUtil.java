package io.github.novareseller.tool.utils;

import io.github.novareseller.tool.text.StringPool;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    private static final char[] C1 = "0123456789".toCharArray();
    private static final char[] C2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] C4 = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final Random random;

    public static final int DIGIT = 1;
    public static final int UPPER_CASE = 2;
    public static final int LOWER_CASE = 4;

    static {
        random = new Random(System.currentTimeMillis() ^ 0xaa1eacb8aae1c438L);
        random.nextInt();
    }

    private static String getString(char[] raw, int length, Random random) {
        char[] array = new char[length];
        while ( length-- > 0 ) {
            array[length] = raw[random.nextInt(raw.length)];
        }
        return new String(array);
    }

    private static String getString(char[] raw, int length) {
        return getString(raw, length, random);
    }

    private static char[] getCharArray(int type) {
        StringBuilder sb = new StringBuilder();
        if ( (type & DIGIT) != 0 ) {
            sb.append(C1);
        }
        if ( (type & UPPER_CASE) != 0 ) {
            sb.append(C2);
        }
        if ( (type & LOWER_CASE) != 0 ) {
            sb.append(C4);
        }
        return sb.toString().toCharArray();
    }

    public static String getString(int length, int type) {
        char[] raw = getCharArray(type);
        return getString(raw, length);
    }

    public static String getString(int length) {
        return getString(length, DIGIT | UPPER_CASE | LOWER_CASE);
    }

    public static int getNumber() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    public static int getNumber(int max) {
        return random.nextInt(max);
    }

    public static byte[] getBytes(int length) {
        byte[] data = new byte[length];
        random.nextBytes(data);
        return data;
    }

    public static String getLocalString(int length) {
        return getLocalString(length, DIGIT | UPPER_CASE | LOWER_CASE);
    }

    public static String getLocalString(int length, int type) {
        char[] raw = getCharArray(type);
        return getString(raw, length, ThreadLocalRandom.current());
    }

    public static String randomUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString().replace(StringPool.DASH, StringPool.EMPTY);
    }
}
