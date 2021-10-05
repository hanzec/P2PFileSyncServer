package com.hanzec.P2PFileSyncServer.utils;

public class ByteArrayUtil {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String ByteArrayToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static byte[] convertFromBooleanArray(boolean[] booleans) {
        byte[] result = new byte[booleans.length / 8];

        for (int i = 0; i < result.length; i++) {
            int index = i * 8;
            byte b = (byte) ((booleans[index] ? 1 << 7 : 0) +
                            (booleans[index + 1] ? 1 << 6 : 0) +
                            (booleans[index + 2] ? 1 << 5 : 0) +
                            (booleans[index + 3] ? 1 << 4 : 0) +
                            (booleans[index + 4] ? 1 << 3 : 0) +
                            (booleans[index + 5] ? 1 << 2 : 0) +
                            (booleans[index + 6] ? 1 << 1 : 0) +
                            (booleans[index + 7] ? 1 : 0));
            result[i] = b;
        }

        return result;
    }
}
