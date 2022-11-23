package twim.melsecplc.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author liumin
 */
public class ByteBufUtilities {

    public static void swapBEToLE(ByteBuf dest, ByteBuf source) {
        while (source.readableBytes() > 0) {
            dest.writeShortLE(source.readUnsignedShort());
        }
    }

    public static void swapLEToBE(ByteBuf dest, ByteBuf source) {
        while (source.readableBytes() > 0) {
            dest.writeShort(source.readUnsignedShortLE());
        }
    }

    public static int writeAsciiBuf(ByteBuf dest, ByteBuf source) {
        int length = 0;
        int bytes = source.readableBytes();
        for (int i = 0; i < bytes; i++) {
            length += writeByteAscii(dest, source.getByte(i));
        }
        return length;
    }

    public static ByteBuf readAsciiBuf(ByteBuf buf) {
        int remaining = buf.readableBytes();
        if (remaining < 0) {
            return null;
        }
        ByteBuf result = Unpooled.buffer(remaining / 2);
        while (buf.readableBytes() > 0) {
            byte[] bytes = new byte[2];
            buf.readBytes(bytes);
            String hex = new String(bytes, StandardCharsets.US_ASCII);
            result.writeByte((byte) Integer.parseInt(hex, 16));
        }
        return result;
    }

    public static ByteBuf readAsciiBufCustom(ByteBuf buf) {
        int remaining = buf.readableBytes(); //this.writerIndex - this.readerIndex
        if (remaining < 0) {
            return null;
        }

        ByteBuf result = Unpooled.buffer(remaining);

        while (buf.readableBytes() > 0) {
            byte[] bytes = new byte[2];
            buf.readBytes(bytes);
            String hex = new String(bytes, StandardCharsets.US_ASCII);
            result.writeBytes(hex.getBytes());
        }
        return result;
    }


    public static byte[] readAllBytes(ByteBuf buf) {
        int length = buf.readableBytes();
        if (length > 0) {
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            return bytes;
        }
        return null;
    }

    public static int writeByteAscii(ByteBuf buf, int data) {
        String hex = Integer.toHexString(data).toUpperCase();
        hex = ("00" + hex).substring(hex.length());
        return ByteBufUtil.writeAscii(buf, hex);
    }

    public static int writeShortAscii(ByteBuf buf, int data) {
        String hex = Integer.toHexString(data).toUpperCase();
        hex = ("0000" + hex).substring(hex.length());
        return ByteBufUtil.writeAscii(buf, hex);
    }

    public static int writeIntAscii(ByteBuf buf, int data) {
        String hex = Integer.toHexString(data).toUpperCase();
        hex = ("00000000" + hex).substring(hex.length());
        return ByteBufUtil.writeAscii(buf, hex);
    }

    public static int writeLongAscii(ByteBuf buf, long data) {
        String hex = Long.toHexString(data).toUpperCase();
        hex = ("0000000000000000" + hex).substring(hex.length());
        return ByteBufUtil.writeAscii(buf, hex);
    }

    public static int writeFloatAscii(ByteBuf buf, float data) {
        String hex = Float.toHexString(data).toUpperCase();
        hex = ("00000000" + hex).substring(hex.length());
        return ByteBufUtil.writeAscii(buf, hex);
    }

    public static int writeDoubleAscii(ByteBuf buf, double data) {
        String hex = Double.toHexString(data).toUpperCase();
        hex = ("0000000000000000" + hex).substring(hex.length());
        return ByteBufUtil.writeAscii(buf, hex);
    }

    public static int readByteAscii(ByteBuf buf) {
        byte[] bytes = new byte[2];
        buf.readBytes(bytes);
        String hex = new String(bytes, StandardCharsets.US_ASCII);
        return Integer.parseInt(hex, 16);
    }

    public static int readShortAscii(ByteBuf buf) {
        byte[] bytes = new byte[4];
        buf.readBytes(bytes);
        String hex = new String(bytes, StandardCharsets.US_ASCII);
        return Integer.parseInt(hex, 16);
    }

    public static int getShortAscii(int offset, ByteBuf buf) {
        byte[] bytes = new byte[4];
        buf.getBytes(offset, bytes);
        String hex = new String(bytes, StandardCharsets.US_ASCII);
        return Integer.parseInt(hex, 16);
    }

    public static int readMediumUnsignedIntAscii(ByteBuf buf) {
        byte[] bytes = new byte[6];
        buf.readBytes(bytes);
        String hex = new String(bytes, StandardCharsets.US_ASCII);
        return Integer.parseUnsignedInt(hex, 16);
    }

    public static int readUnsignedIntAscii(ByteBuf buf) {
        byte[] bytes = new byte[8];
        buf.readBytes(bytes);
        String hex = new String(bytes, StandardCharsets.US_ASCII);
        return Integer.parseUnsignedInt(hex, 16);
    }
}
