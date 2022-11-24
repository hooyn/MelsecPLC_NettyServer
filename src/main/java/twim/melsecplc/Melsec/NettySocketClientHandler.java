package twim.melsecplc.Melsec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
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

import java.nio.charset.StandardCharsets;

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
//                    int remaining = response.getData().readableBytes();
//                    ByteBuf data = Unpooled.buffer(remaining);
//                    data.writeBytes(response.getData());
//                    response.setData(data);
                    if (command.getPrincipal().getDevice().getType() == UnitType.BIT){
                        //이 코드에서 데이터를 0또는 1로 변경
                        //byte[] bytes = BinaryConverters.convertBinaryOnBitToBoolArray(ByteBufUtilities.readAllBytes(response.getData()), command.getPrincipal().getPoints());
                        //response.setData(Unpooled.wrappedBuffer(bytes));
                        int remaining = response.getData().readableBytes();
                        ByteBuf data = Unpooled.buffer(remaining);
                        ByteBufUtilities.swapLEToBE(data, response.getData());
                        response.setData(data);
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
            log.warn("Response packet {}", response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        
        cause.printStackTrace();
        ctx.close();
    }
}