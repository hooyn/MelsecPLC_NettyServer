package twim.melsecplc.setting.core.message;

import io.netty.buffer.ByteBuf;

/**
 * 字节编码解码
 *
 * @author liumin
 */
public interface BinaryCodec {

    /**
     * 编码
     *
     * @param buf 需要编入的ByteBuf
     */
    void encode(ByteBuf buf);

    /**
     * 解码
     *
     * @param buf 待编码ByteBuf
     * @return 解码成功与否
     */
    boolean decode(ByteBuf buf) throws Exception;
}
