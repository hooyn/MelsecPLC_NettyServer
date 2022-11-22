package twim.melsecplc.core.message.e.subheader;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.core.message.e.Subheader;

import java.util.Arrays;

/**
 * @author liumin
 */
public abstract class AbstractFrame3ESubheader implements Subheader {

    /**
     * 获取报文
     *
     * @return 报文
     */
    protected abstract byte[] getCodes();

    @Override
    public void encode(ByteBuf buf) {
        buf.writeBytes(getCodes());
    }

    @Override
    public boolean decode(ByteBuf buf) {
        byte[] bytes = new byte[getCodes().length];
        buf.readBytes(bytes);
        return Arrays.equals(getCodes(), bytes);
    }
}
