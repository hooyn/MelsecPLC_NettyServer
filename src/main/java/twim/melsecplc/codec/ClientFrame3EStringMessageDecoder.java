package twim.melsecplc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import twim.melsecplc.core.message.e.Frame3EAsciiResponse;
import twim.melsecplc.core.message.e.FrameEResponse;
import twim.melsecplc.core.utils.ByteBufUtilities;

import java.util.List;

/**
 * @author liumin
 */
@Slf4j
public class ClientFrame3EStringMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        FrameEResponse response = new Frame3EAsciiResponse();

//        Subheader subheader = response.getSubheader();
//        subheader.decode(in);
//        AbstractResponseQHeader qHeader = response.getQHeader();
//        qHeader.decode(in);
        response.setData(ByteBufUtilities.readAsciiBufCustom(in));

        out.add(response);
    }
}
