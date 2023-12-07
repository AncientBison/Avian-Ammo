package avianammo.networking;

public class ByteConversion {

    private ByteConversion() {};
    
    public static void writeDoubleToBytes(double d, int offset, byte[] output) {
        long lng = Double.doubleToLongBits(d);
        for(int i = offset; i < offset + 8; i++) {
            output[i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);
        }
    }

    public static byte[] doubleToBytes(double d) {
        byte[] output = new byte[8];
        long lng = Double.doubleToLongBits(d);
        for(int i = 0; i < 8; i++) output[i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);
        return output;
    }

    public static double bytesToDouble(byte[] bytes, int offset) {
        long lng = 0;
        for (int i = offset; i < offset + 8; i++) {
            lng |= ((long) (bytes[i] & 0xff)) << ((7 - i) * 8);
        }
        return Double.longBitsToDouble(lng);
    }

    public static void writeIntToBytes(int value, int offset, byte[] output) {
        for (int i = offset; i < offset + 4; i++) {
            output[i] = (byte) ((value >> ((3 - i + offset) * 8)) & 0xff);
        }
    }
    
    public static byte[] intToBytes(int value) {
        byte[] output = new byte[4];
        for (int i = 0; i < 4; i++) {
            output[i] = (byte) ((value >> ((3 - i) * 8)) & 0xff);
        }
        return output;
    }
    
    public static int bytesToInt(byte[] bytes, int offset) {
        int intValue = 0;
        for (int i = offset; i < offset + 4; i++) {
            intValue |= ((bytes[i] & 0xff) << ((3 - i + offset) * 8));
        }
        return intValue;
    }    
}
