package twim.melsecplc.core.message.e.subheader;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.core.utils.ByteBufUtilities;

import java.util.Arrays;

/**
 * @author liumin
 */
public abstract class AbstractFrame4EAsciiSubheader extends AbstractFrame4ESubheader {

    public AbstractFrame4EAsciiSubheader() {
    }

    public AbstractFrame4EAsciiSubheader(int serialNo) {
        super(serialNo);
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeBytes(getFrontCodes());
        ByteBufUtilities.writeShortAscii(buf, getSerialNo());
        buf.writeBytes(getEndCodes());
    }

    @Override
    public boolean decode(ByteBuf buf) {
        byte[] frontCodes = new byte[4];
        buf.readBytes(frontCodes);

        setSerialNo(ByteBufUtilities.readShortAscii(buf));

        byte[] endCodes = new byte[4];
        buf.readBytes(endCodes);

        return Arrays.equals(getFrontCodes(), frontCodes)
            && Arrays.equals(getEndCodes(), endCodes);
    }
}
