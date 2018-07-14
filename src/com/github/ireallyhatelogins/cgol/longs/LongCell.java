package com.github.ireallyhatelogins.cgol.longs;

public final class LongCell {

    private static final long HIGH_LONG = 0xFFFF_FFFF_0000_0000L;
    private static final long LOW_LONG = 0xFFFF_FFFFL;

    public static int getY(long cell) {
        return (int) ((cell >>> 32) & LOW_LONG);
    }

    public static int getX(long cell) {
        return (int) (cell & LOW_LONG);
    }

    public static long fromCoordinates(int x, int y) {
        return (((long) y << 32) & HIGH_LONG) | (x & LOW_LONG);
    }

    public static boolean inRange(long cell, int startX, int startY, int endX, int endY) {
        int x = getX(cell);
        int y = getY(cell);

        //make it a giant torus...
        //checking x
        if (endX >= startX) {
            if ((x < startX) || (x > endX)) {
                return false;
            }
        } else {
            if ((x < startX) && (x > endX)) {
                return false;
            }
        }

        //checking y
        if (endY >= startY) {
            if ((y < startY) || (y > endY)) {
                return false;
            }
        } else {
            if ((y < startY) && (y > endY)) {
                return false;
            }
        }

        return true;
    }

    static public boolean equals(long l, long l1) {
        return l == l1;
    }

    static public int hashCode(long l) {
        return (int) (((l >> 16) & 0xFFFF_0000) | (l & 0xFFFF));
    }

}
