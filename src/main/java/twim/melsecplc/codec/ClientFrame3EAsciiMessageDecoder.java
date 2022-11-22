package twim.melsecplc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import twim.melsecplc.core.message.e.Frame3EAsciiResponse;
import twim.melsecplc.core.message.e.FrameEResponse;
import twim.melsecplc.core.message.e.Subheader;
import twim.melsecplc.core.message.e.qheader.AbstractResponseQHeader;
import twim.melsecplc.core.utils.ByteBufUtilities;

import java.util.List;

/**
 * @author liumin
 */
public class ClientFrame3EAsciiMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        FrameEResponse response = new Frame3EAsciiResponse();
        Subheader subheader = response.getSubheader();
        subheader.decode(in);
        AbstractResponseQHeader qHeader = response.getQHeader();
        qHeader.decode(in);
        if (qHeader.getCompleteCode() == 0) {
            response.setData(ByteBufUtilities.readAsciiBuf(in));
        } else {
            response.getErrorInformationSection().decode(in);
        }
        out.add(response);
    }
}
