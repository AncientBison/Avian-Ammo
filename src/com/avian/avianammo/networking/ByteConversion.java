package avianammo.networking;

public class ByteConversion {

    private ByteConversion() {}

    public static byte[] doubleToBytes(double d) {
        byte[] output = new byte[8];
        long lng = Double.doubleToLongBits(d);
        for (int i = 0; i < 8; i++)
            output[i] = (byte) ((lng >> ((7 - i) * 8)) & 0xff);
        return output;
    }

    public static double bytesToDouble(byte[] bytes, int offset) {
        long longBits = ((long) (bytes[offset] & 0xFF) << 56) |
                ((long) (bytes[offset + 1] & 0xFF) << 48) |
                ((long) (bytes[offset + 2] & 0xFF) << 40) |
                ((long) (bytes[offset + 3] & 0xFF) << 32) |
                ((long) (bytes[offset + 4] & 0xFF) << 24) |
                ((long) (bytes[offset + 5] & 0xFF) << 16) |
                ((long) (bytes[offset + 6] & 0xFF) << 8) |
                (bytes[offset + 7] & 0xFF);

        return Double.longBitsToDouble(longBits);
    }

    public static byte[] intToBytes(int value) {
        byte[] output = new byte[4];
        for (int i = 0; i < 4; i++) {
            output[i] = (byte) ((value >> ((3 - i) * 8)) & 0xff);
        }
        return output;
    }

    public static int bytesToInt(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) << 24 |
                (bytes[offset + 1] & 0xFF) << 16 |
                (bytes[offset + 2] & 0xFF) << 8 |
                (bytes[offset + 3] & 0xFF);
    }

    public static byte[] longToBytes(long value) {
        byte[] output = new byte[8];
        for (int i = 0; i < 8; i++) {
            output[i] = (byte) ((value >> ((7 - i) * 8)) & 0xff);
        }
        return output;
    }

    public static long bytesToLong(byte[] bytes, int offset) {
        return ((long) (bytes[offset] & 0xFF) << 56) |
                ((long) (bytes[offset + 1] & 0xFF) << 48) |
                ((long) (bytes[offset + 2] & 0xFF) << 40) |
                ((long) (bytes[offset + 3] & 0xFF) << 32) |
                ((long) (bytes[offset + 4] & 0xFF) << 24) |
                ((long) (bytes[offset + 5] & 0xFF) << 16) |
                ((long) (bytes[offset + 6] & 0xFF) << 8) |
                (bytes[offset + 7] & 0xFF);
    }
}
