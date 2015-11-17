package mavlink;

public class MavLinkCRC {
    private static final int[] MAVLINK_MESSAGE_CRCS = {50, 124, 137, 0, 237,
            217, 104, 119, 0, 0, 0, 89, 0, 0, 0, 0, 0, 0, 0, 0, 214,
            159, 220, 168, 24, 23, 170, 144, 67, 115, 39, 246, 185,
            104, 237, 244, 222, 212, 9, 254, 230, 28, 28, 132, 221,
            232, 11, 153, 41, 39, 0, 0, 0, 0, 15, 3, 0, 0, 0, 0, 0,
            153, 183, 51, 82, 118, 148, 21, 0, 243, 124, 0, 0, 38,
            20, 158, 152, 143, 0, 0, 0, 106, 49, 22, 29, 12, 241,
            233, 0, 231, 183, 63, 54, 0, 0, 0, 0, 0, 0, 0, 175, 102,
            158, 208, 56, 93, 211, 108, 32, 185, 84, 0, 0, 124, 119,
            4, 76, 128, 56, 116, 134, 237, 203, 250, 87, 203, 220,
            25, 226, 0, 29, 223, 85, 6, 229, 203, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 42, 49, 0, 134, 219, 208, 188, 84, 22,
            19, 21, 134, 0, 78, 68, 189, 127, 154, 21, 21, 144, 1,
            234, 73, 181, 22, 83, 167, 138, 234, 240, 47, 189, 52,
            174, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 204, 49, 170,
            44, 83, 46, 0};

    public static int calcCRC(byte[] data) {
        int crc = 0xffff;
        //прокускаем magic
        for (int i = 1; i < data.length; i++) {
            crc = update(data[i], crc);
        }
        crc = update((byte) MAVLINK_MESSAGE_CRCS[data[5] & 0xFF], crc);
        return crc;
    }

    private static int update(int data, int crc) {
        int tmp;
        data = (data & 0xff);    //cast because we want an unsigned type
        tmp = data ^ (crc & 0xff);
        tmp ^= (tmp << 4) & 0xff;
        return ((crc >> 8) & 0xff) ^ (tmp << 8) ^ (tmp << 3)
                ^ ((tmp >> 4) & 0xf);
    }

    public static void main(String[] args) {
        byte[] data = {(byte)0xfe,0x33,0x01,0x01,0x01,(byte)0xfd,0x04,0x43,0x61,0x6c,0x69,0x62,0x72,0x61,0x74,0x69,0x6e,0x67,0x20,0x62,0x61,0x72,0x6f,0x6d,0x65,0x74,0x65,0x72,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        int crc = calcCRC(data);
        System.out.println(crc & 0xFF);
        System.out.println(crc >> 8);

    }
}
