package twim.melsecplc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twim.melsecplc.core.message.e.FrameECommand;
import twim.melsecplc.core.message.e.QHeader;
import twim.melsecplc.core.message.e.qheader.AsciiCommandQHeader;
import twim.melsecplc.core.message.e.qheader.BinaryCommandQHeader;

/**
 * @author liumin
 */
@ChannelHandler.Sharable
public class ClientFrameEMessageEncoder extends MessageToByteEncoder<FrameECommand> {

    private static final Logger log = LoggerFactory.getLogger(ClientFrameEMessageEncoder.class);

    public static final ClientFrameEMessageEncoder INSTANCE = new ClientFrameEMessageEncoder();

    @Override
    protected void encode(ChannelHandlerContext ctx, FrameECommand msg, ByteBuf out) throws Exception {

        try {
            ByteBuf commandBuf = UnpooledByteBufAllocator.DEFAULT.heapBuffer();
            msg.getPrincipal().encode(commandBuf);
            int commandBytesNum = commandBuf.readableBytes();

            QHeader qHeader = msg.getQHeader();
            if (qHeader instanceof AsciiCommandQHeader){
                AsciiCommandQHeader asciiCommandQHeader = (AsciiCommandQHeader)qHeader;
                asciiCommandQHeader.setRequestDataLength(commandBytesNum + 4);
            } else if (qHeader instanceof BinaryCommandQHeader){
                BinaryCommandQHeader binaryCommandQHeader = (BinaryCommandQHeader)qHeader;
                binaryCommandQHeader.setRequestDataLength(commandBytesNum + 2);
            }

            msg.getSubheader().encode(out);
            msg.getQHeader().encode(out);
            out.writeBytes(commandBuf);

            //log.info(String.format("Sending packet %s", msg));
            log.info("Sending Packet Check: true");
        } finally {
            ReferenceCountUtil.release(msg.getPrincipal().getData());
        }
    }
}
