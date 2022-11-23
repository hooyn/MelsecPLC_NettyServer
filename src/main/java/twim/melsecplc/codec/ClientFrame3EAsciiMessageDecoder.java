package twim.melsecplc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import twim.melsecplc.core.message.e.Frame3EAsciiResponse;
import twim.melsecplc.core.message.e.FrameEResponse;
import twim.melsecplc.core.message.e.Subheader;
import twim.melsecplc.core.message.e.qheader.AbstractResponseQHeader;
import twim.melsecplc.core.utils.ByteBufUtilities;

import java.util.List;

/**
 * @author liumin
 */
@Slf4j
public class ClientFrame3EAsciiMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.warn("----------Data Output Start----------");
        log.warn("toString(): " + in.toString());
        log.warn("US_ASCII: " + in.toString(CharsetUtil.US_ASCII));
        log.warn("UTF_8: " + in.toString(CharsetUtil.UTF_8));
        log.warn("ISO_8859_1: " + in.toString(CharsetUtil.ISO_8859_1));
        log.warn("----------Data Output End----------");

        FrameEResponse response = new Frame3EAsciiResponse();
        response.setData(ByteBufUtilities.readAsciiBufCustom(in));
//        Subheader subheader = response.getSubheader();
//        subheader.decode(in);
//        AbstractResponseQHeader qHeader = response.getQHeader();
//        qHeader.decode(in);
//        if (qHeader.getCompleteCode() == 0) {
//            response.setData(ByteBufUtilities.readAsciiBuf(in));
//        } else {
//            response.getErrorInformationSection().decode(in);
//        }
        out.add(response);
    }
}
