package twim.melsecplc.core.message.e.subheader;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;

/**
 * @author liumin
 */
public abstract class AbstractFrame4EBinarySubheader extends AbstractFrame4ESubheader {

    public AbstractFrame4EBinarySubheader() {
    }

    public AbstractFrame4EBinarySubheader(int serialNo) {
        super(serialNo);
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeBytes(getFrontCodes());
        buf.writeShortLE(getSerialNo());
        buf.writeBytes(getEndCodes());
    }

    @Override
    public boolean decode(ByteBuf buf) {
        byte[] frontCodes = new byte[getFrontCodes().length];
        buf.readBytes(frontCodes);

        setSerialNo(buf.readUnsignedShortLE());

        byte[] endCodes = new byte[getEndCodes().length];
        buf.readBytes(endCodes);

        return Arrays.equals(getFrontCodes(), frontCodes)
            && Arrays.equals(getEndCodes(), endCodes);
    }
}
