package twim.melsecplc.setting.codec.NOT_USE;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twim.melsecplc.setting.core.message.e.FrameEResponse;
import twim.melsecplc.setting.core.message.e.qheader.AbstractResponseQHeader;
import twim.melsecplc.setting.core.message.e.qheader.AsciiResponseQHeader;
import twim.melsecplc.setting.core.message.e.qheader.BinaryResponseQHeader;
import twim.melsecplc.setting.core.utils.ByteBufUtilities;

/**
 * @author liumin
 */
@ChannelHandler.Sharable
public class ServerFrameEMessageEncoder extends MessageToByteEncoder<FrameEResponse> {

    private static final Logger log = LoggerFactory.getLogger(ServerFrameEMessageEncoder.class);

    public static final ServerFrameEMessageEncoder INSTANCE = new ServerFrameEMessageEncoder();

    @Override
    protected void encode(ChannelHandlerContext ctx, FrameEResponse msg, ByteBuf out) throws Exception {
        msg.getSubheader().encode(out);

        AbstractResponseQHeader responseQHeader = msg.getQHeader();
        if (responseQHeader instanceof AsciiResponseQHeader) {
            if (responseQHeader.getCompleteCode() == 0) {
                if (msg.getData() != null) {
                    ByteBuf data = Unpooled.buffer(msg.getData().readableBytes() * 2);
                    ByteBufUtilities.writeAsciiBuf(data, msg.getData());
                    responseQHeader.setResponseDataLength(data.readableBytes() + 4);
                    responseQHeader.encode(out);
                    out.writeBytes(data);
                } else {
                    responseQHeader.setResponseDataLength(4);
                    responseQHeader.encode(out);
                }
            } else {
                responseQHeader.setResponseDataLength(22);
                responseQHeader.encode(out);
                msg.getErrorInformationSection().encode(out);
            }
        } else if (responseQHeader instanceof BinaryResponseQHeader) {
            if (responseQHeader.getCompleteCode() == 0) {
                if (msg.getData() != null) {
                    responseQHeader.setResponseDataLength(msg.getData().readableBytes() + 2);
                    responseQHeader.encode(out);
                    out.writeBytes(msg.getData());
                } else {
                    responseQHeader.setResponseDataLength(2);
                    responseQHeader.encode(out);
                }
            } else {
                responseQHeader.setResponseDataLength(11);
                responseQHeader.encode(out);
                msg.getErrorInformationSection().encode(out);
            }
        }

        log.debug(String.format("Sending packet %s", msg));
    }
}
