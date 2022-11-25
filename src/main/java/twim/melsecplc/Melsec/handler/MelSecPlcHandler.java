package twim.melsecplc.melsec.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import twim.melsecplc.melsec.config.MelSecPlcConfig;
import twim.melsecplc.setting.codec.ClientFrame3EBinaryMessageDecoder;
import twim.melsecplc.setting.codec.ClientFrameEMessageEncoder;
import twim.melsecplc.setting.codec.Frame3EBinaryByteDecoder;
import twim.melsecplc.setting.core.MelsecClientOptions;
import twim.melsecplc.setting.core.message.Function;
import twim.melsecplc.setting.core.message.e.Frame3EBinaryCommand;
import twim.melsecplc.setting.core.message.e.FrameECommand;
import twim.melsecplc.setting.core.message.e.FrameEResponse;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Getter
@Slf4j
public class MelSecPlcHandler {
    private final MelSecPlcConfig melSecPlcConfig;
    private Thread plcThread;
    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();;
    private Channel channel;
    private final Queue<FrameECommand> requestQueue = new LinkedList<>();
    private final Queue<FrameEResponse> responseQueue = new LinkedList<>();
    private final Lock lock = new ReentrantLock();

    public MelSecPlcHandler(String ip, int port){
        
        this.melSecPlcConfig = MelSecPlcConfig.builder().address(ip).port(port).build();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(this.workerGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(this.melSecPlcConfig.getAddress(), this.melSecPlcConfig.getPort())
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_LINGER, 0)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<SocketChannel>(){

                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();
                            // handler setting
                            pipeline.addLast(ClientFrameEMessageEncoder.INSTANCE);
                            pipeline.addLast(new ClientFrame3EBinaryMessageDecoder());
                            pipeline.addLast(new Frame3EBinaryByteDecoder());
                            pipeline.addLast(new MelSecPlcResponseHandler(MelSecPlcHandler.this));
                        }
                    });

        this.plcThread = new Thread(() -> {

            connect(bootstrap);

            try {
                batchRead("D45010", 1).get();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            log.info("Connected to PLC [IP: {}]", ip);

            while (true){
                try {
                    batchRead("D45000", 1).get();
                    Thread.sleep(500);

                    if (!isConnected()){
                        connect(bootstrap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "PLC-" + ip);

        this.plcThread.setDaemon(true);
        this.plcThread.start();
    }

    public void connect(Bootstrap bootstrap){

        try {
            ChannelFuture f = bootstrap.connect().sync();
            this.channel = f.channel();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ee) {
                log.warn("Connect Error: " + ee.getMessage());
            }

            connect(bootstrap);
        }
    }

    public CompletableFuture<String> batchRead(String address, int points){

        return requestAPI(new Frame3EBinaryCommand(
                Function.BATCH_READ,
                address,
                points,
                new MelsecClientOptions(this.melSecPlcConfig.getNetworkNo(),
                        this.melSecPlcConfig.getPcNo(),
                        this.melSecPlcConfig.getRequestDestinationModuleIoNo(),
                        this.melSecPlcConfig.getRequestDestinationModuleStationNo())));
    }

    public CompletableFuture<String> batchWrite(String address, int points, ByteBuf data){

        return requestAPI(new Frame3EBinaryCommand(
                Function.BATCH_WRITE,
                address,
                points,
                data,
                new MelsecClientOptions(this.melSecPlcConfig.getNetworkNo(),
                        this.melSecPlcConfig.getPcNo(),
                        this.melSecPlcConfig.getRequestDestinationModuleIoNo(),
                        this.melSecPlcConfig.getRequestDestinationModuleStationNo())))
                .thenCompose(r -> batchRead(address, points));
    }

    private CompletableFuture<String> requestAPI(FrameECommand command){

        Supplier<String> responseSupplier = () -> {

            this.lock.lock();

            long start = System.currentTimeMillis();
            this.channel.writeAndFlush(command).addListener(listener -> {
                if (!listener.isSuccess())
                    log.error(this.melSecPlcConfig.getAddress() + "- Request Failed: " + command.getPrincipal().getAddress()
                            + ", Thread- " + Thread.currentThread().getName());
                else
                    this.requestQueue.add(command);
            });

            try {
                while (true){
                    if (this.responseQueue.size() > 0){
                        log.warn(this.melSecPlcConfig.getAddress() + "- Tact time: " + (System.currentTimeMillis() - start)
                                + ", Thread- " + Thread.currentThread().getName());

                        FrameEResponse response = this.responseQueue.poll();

                        if (response.getData() == null)
                            return "";
                        else
                            return ByteBufUtil.hexDump(response.getData());
                    }

                    // TODO: 2022-11-22 TIME OUT 필요없을 시 제거
                    if ((System.currentTimeMillis() - start) > 3000){
                        this.responseQueue.poll();
                        return "TIMEOUT";
                    }


                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                return "Request Error: " + e.getMessage();
            } finally {
                this.lock.unlock();
            }
        };

        return CompletableFuture.supplyAsync(responseSupplier);
    }

    public boolean isConnected(){

        if (this.channel == null)
            return false;
        else
            return this.channel.isActive();
    }

    public void remove(){
        if (this.plcThread != null && !this.plcThread.isInterrupted())
            this.plcThread.interrupt();
        this.workerGroup.shutdownGracefully().awaitUninterruptibly();
    }

    /**
     * PLC 에 명령 전달
     */
    public void sendCommand(){

        ByteBuf data = Unpooled.buffer();
        data.writeBoolean(false);
        data.writeBoolean(true);
        data.writeBoolean(false);
        data.writeBoolean(false);
        data.writeBoolean(true);
        data.writeBoolean(false);
        data.writeBoolean(true);
        data.writeBoolean(true);

        log.info("Send Data ByteBuf: " + data);

        try {
            ChannelFuture cf = channel.writeAndFlush(data);
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if(channelFuture.isSuccess()){
                    log.info("write success");
                    log.info(batchWrite("D45500", 1, data).get());
                } else {
                    log.error("write error");
                    channelFuture.cause().printStackTrace();
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}