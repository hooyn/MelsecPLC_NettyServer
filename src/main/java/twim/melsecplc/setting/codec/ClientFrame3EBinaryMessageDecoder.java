package twim.melsecplc.setting.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import twim.melsecplc.setting.core.message.e.Frame3EBinaryResponse;
import twim.melsecplc.setting.core.message.e.FrameEResponse;
import twim.melsecplc.setting.core.message.e.Subheader;
import twim.melsecplc.setting.core.message.e.qheader.AbstractResponseQHeader;

import java.util.List;

/**
 * @author liumin
 */
public class ClientFrame3EBinaryMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        FrameEResponse response = new Frame3EBinaryResponse();
        Subheader subheader = response.getSubheader();
        subheader.decode(in);
        AbstractResponseQHeader qHeader = response.getQHeader();
        qHeader.decode(in);
        if (qHeader.getCompleteCode() == 0) {
            int remaining = in.readableBytes();
            if (remaining > 0) {
                byte[] bytes = new byte[remaining];
                in.readBytes(bytes);
                response.setData(Unpooled.wrappedBuffer(bytes));
            }
        } else {
            response.getErrorInformationSection().decode(in);
        }
        out.add(response);
    }
}
