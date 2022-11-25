package twim.melsecplc.setting.codec.NOT_USE;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import twim.melsecplc.setting.core.message.e.Frame3EAsciiResponse;
import twim.melsecplc.setting.core.message.e.FrameEResponse;
import twim.melsecplc.setting.core.utils.ByteBufUtilities;

import java.util.List;

/**
 * @author liumin
 */
@Slf4j
public class ClientFrame3EStringMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        FrameEResponse response = new Frame3EAsciiResponse();
        StringBuffer data = new StringBuffer();
        byte[] bytes = ByteBufUtilities.readAllBytes(in);
        for (byte aByte : bytes) {
            data.append(aByte);
        }
        log.error("-----------------byte value: " + data + "-----------------");

        response.setData(ByteBufUtilities.readAsciiBufCustom(in));
        out.add(response);
    }
}
