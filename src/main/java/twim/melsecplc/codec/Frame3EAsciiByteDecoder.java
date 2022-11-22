package twim.melsecplc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import twim.melsecplc.core.utils.ByteBufUtilities;

import java.nio.ByteOrder;

/**
 * @author liumin
 */
public class Frame3EAsciiByteDecoder extends LengthFieldBasedFrameDecoder {

    public Frame3EAsciiByteDecoder() {
        super(3606, 14, 4);
    }

    @Override
    protected long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {
        return ByteBufUtilities.getShortAscii(offset, buf);
    }
}
