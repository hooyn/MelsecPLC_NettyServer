package twim.melsecplc.codec.NOT_USE;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * @author liumin
 */
public class Frame3EBinaryByteDecoder extends LengthFieldBasedFrameDecoder {

    public Frame3EBinaryByteDecoder() {
        super(ByteOrder.LITTLE_ENDIAN, 7179, 7, 2, 0, 0, true);
    }
}
