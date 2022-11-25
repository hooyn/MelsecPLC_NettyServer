package twim.melsecplc.setting.codec.NOT_USE;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import twim.melsecplc.setting.core.message.e.Frame3EAsciiResponse;
import twim.melsecplc.setting.core.message.e.FrameEResponse;
import twim.melsecplc.setting.core.message.e.Subheader;
import twim.melsecplc.setting.core.message.e.qheader.AbstractResponseQHeader;
import twim.melsecplc.setting.core.utils.ByteBufUtilities;

import java.util.List;

/**
 * @author liumin
 */
@Slf4j
public class ClientFrame3EAsciiMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        FrameEResponse response = new Frame3EAsciiResponse();

        Subheader subheader = response.getSubheader();
        subheader.decode(in);
        AbstractResponseQHeader qHeader = response.getQHeader();
        qHeader.decode(in);

        if (qHeader.getCompleteCode() == 0) {
            response.setData(ByteBufUtilities.readAsciiBufCustom(in));
        } else {
            response.getErrorInformationSection().decode(in);
        }
        out.add(response);
    }
}
