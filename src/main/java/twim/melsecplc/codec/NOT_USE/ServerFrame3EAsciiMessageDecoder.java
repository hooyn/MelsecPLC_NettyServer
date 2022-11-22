package twim.melsecplc.codec.NOT_USE;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import twim.melsecplc.core.message.e.Frame3EAsciiCommand;
import twim.melsecplc.core.message.e.FrameECommand;

import java.util.List;

/**
 * @author liumin
 */
public class ServerFrame3EAsciiMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        FrameECommand command = new Frame3EAsciiCommand();
        command.getSubheader().decode(in);
        command.getQHeader().decode(in);
        command.getPrincipal().decode(in);
        out.add(command);
    }
}
