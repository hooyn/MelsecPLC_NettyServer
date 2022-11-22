package twim.melsecplc.core.utils;

import java.nio.charset.StandardCharsets;

/**
 * @author liumin
 */
public class BinaryConverters {

    public static byte[] convertBoolArrayToAsciiOnBit(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < result.length; i++) {
            if (data[i] == (byte) 1) {
                result[i] = 0x31;
            } else {
                result[i] = 0x30;
            }
        }
        return result;
    }

    public static byte[] convertBoolArrayToAsciiOnWord(boolean[] data) {
        assert (data.length & 3) == 0;

        int length = data.length >> 2;
        byte[] result = new byte[length];

        for (int i = 0; i < result.length; i++) {
            int j = i << 2;

            int k;
            if (data.length > 16) {
                k = (15 - (j & 15)) + (j >> 4 << 4);
            } else {
                k = data.length - 1 - j;
            }

            int d = (byte) (((data[k] ? 15 : 0) & 8)
                | ((data[k - 1] ? 15 : 0) & 4)
                | ((data[k - 2] ? 15 : 0) & 2)
                | ((data[k - 3] ? 15 : 0) & 1));

            String s = Integer.toHexString(d).toUpperCase();
            result[i] = s.getBytes(StandardCharsets.US_ASCII)[0];
        }

        return result;
    }

    public static byte[] convertAsciiOnBitToBoolArray(byte[] data, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) ((data[i] == 0x31) ? 1 : 0);
        }
        return result;
    }

    public static boolean[] convertAsciiOnWordToBoolArray(byte[] data) {
        int length = data.length << 2;
        boolean[] result = new boolean[length];

        for (int i = 0; i < data.length; i++) {
            int v = Character.getNumericValue(data[i]);

            int j = i << 2;
            int k;
            if (length > 16) {
                k = (15 - (j & 15)) + (j >> 4 << 4);
            } else {
                k = length - 1 - j;
            }

            result[k] = (v & 8) == 8;
            result[k - 1] = (v & 4) == 4;
            result[k - 2] = (v & 2) == 2;
            result[k - 3] = (v & 1) == 1;
        }
        return result;
    }

    public static byte[] convertBoolArrayToBinaryOnBit(byte[] value) {
        int length = value.length;
        int h = length >> 1;
        byte[] buffer = new byte[(length & 1) == 0 ? h : h + 1];

        for (int i = 0; i < buffer.length; i++) {
            int d = (i << 1);
            if (value[d] == (byte) 1) {
                buffer[i] |= 0x10;
            }
            if ((d + 1) < value.length) {
                if (value[d + 1] == (byte) 1) {
                    buffer[i] |= 0x01;
                }
            }
        }

        return buffer;
    }

    public static byte[] convertBinaryOnBitToBoolArray(byte[] buffer, int length) {
        byte[] value = new byte[length];
        for (int i = 0; i < length; i++) {
            int h = i >> 1;
            if ((i & 1) == 0) {
                value[i] = (byte) ((buffer[h] & 0x10) == 0x10 ? 1 : 0);
            } else {
                value[i] = (byte) ((buffer[h] & 0x01) == 0x01 ? 1 : 0);
            }
        }
        return value;
    }

    public static byte[] convertBoolArrayToBinaryOnWord(boolean[] data) {
        assert (data.length & 3) == 0;

        int length;
        if (data.length > 7) {
            length = data.length >> 3;
        } else {
            length = 1;
        }
        byte[] result = new byte[length];

        for (int i = 0; i < result.length; i++) {
            int j = i << 3;

            int k;
            if (data.length > 7) {
                k = (7 - (j & 7)) + (j >> 3 << 3);

                result[i] = (byte) (((data[k] ? 255 : 0) & 128)
                    | ((data[k - 1] ? 255 : 0) & 64)
                    | ((data[k - 2] ? 255 : 0) & 32)
                    | ((data[k - 3] ? 255 : 0) & 16)
                    | ((data[k - 4] ? 255 : 0) & 8)
                    | ((data[k - 5] ? 255 : 0) & 4)
                    | ((data[k - 6] ? 255 : 0) & 2)
                    | ((data[k - 7] ? 255 : 0) & 1));
            } else {
                k = data.length - 1 - j;

                result[i] = (byte) (((data[k] ? 15 : 0) & 8)
                    | ((data[k - 1] ? 15 : 0) & 4)
                    | ((data[k - 2] ? 15 : 0) & 2)
                    | ((data[k - 3] ? 15 : 0) & 1));
            }
        }

        return result;
    }

    public static boolean[] convertBinaryOnWordToBoolArray(byte[] data) {
        int length = data.length << 3;
        boolean[] result = new boolean[length];
        for (int i = 0; i < data.length; i++) {
            int j = i << 3;
            int k = (7 - (j & 7)) + (j >> 3 << 3);

            result[k] = (data[i] & 128) == 128;
            result[k - 1] = (data[i] & 64) == 64;
            result[k - 2] = (data[i] & 32) == 32;
            result[k - 3] = (data[i] & 16) == 16;
            result[k - 4] = (data[i] & 8) == 8;
            result[k - 5] = (data[i] & 4) == 4;
            result[k - 6] = (data[i] & 2) == 2;
            result[k - 7] = (data[i] & 1) == 1;
        }
        return result;
    }
}
