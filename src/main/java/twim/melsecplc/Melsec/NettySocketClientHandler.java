package twim.melsecplc.Melsec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twim.melsecplc.core.message.Function;
import twim.melsecplc.core.message.UnitType;
import twim.melsecplc.core.message.e.FrameECommand;
import twim.melsecplc.core.message.e.FrameEResponse;
import twim.melsecplc.core.utils.BinaryConverters;
import twim.melsecplc.core.utils.ByteBufUtilities;

@RequiredArgsConstructor
public class NettySocketClientHandler extends SimpleChannelInboundHandler<FrameEResponse> {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final MelsecPlcHandler melsecPlcHandler;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FrameEResponse response){

        try {
            FrameECommand command = this.melsecPlcHandler.getRequestQueue().poll();

            if (command != null){
                if (command.getPrincipal().getFunction() == Function.BATCH_READ){
                    if (command.getPrincipal().getDevice().getType() == UnitType.BIT){
                        byte[] bytes = BinaryConverters.convertBinaryOnBitToBoolArray(
                                ByteBufUtilities.readAllBytes(response.getData()), command.getPrincipal().getPoints());
                        response.setData(Unpooled.wrappedBuffer(bytes));
                    }
                    else {
                        int remaining = response.getData().readableBytes();
                        ByteBuf data = Unpooled.buffer(remaining);
                        ByteBufUtilities.swapLEToBE(data, response.getData());
                        response.setData(data);
                    }
                }
                this.melsecPlcHandler.getResponseQueue().add(response);
            }
        } finally {
            ReferenceCountUtil.release(response);
            log.info("Response packet {}", response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        
        cause.printStackTrace();
        ctx.close();
    }
}