package twim.melsecplc.melsec.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import twim.melsecplc.setting.core.message.Function;
import twim.melsecplc.setting.core.message.e.FrameECommand;
import twim.melsecplc.setting.core.message.e.FrameEResponse;

@RequiredArgsConstructor
@Slf4j
public class MelSecPlcResponseHandler extends SimpleChannelInboundHandler<FrameEResponse> {

    private final MelSecPlcHandler melsecPlcHandler;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FrameEResponse response){

        try {
            FrameECommand command = this.melsecPlcHandler.getRequestQueue().poll();
            if (command != null){
                if (command.getPrincipal().getFunction() == Function.BATCH_READ){
                    response.setData(response.getData());
                }
                this.melsecPlcHandler.getResponseQueue().add(response);
            }
        } finally {
            ReferenceCountUtil.release(response);
            log.warn("Response Packet {}", response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        
        cause.printStackTrace();
        ctx.close();
    }
}