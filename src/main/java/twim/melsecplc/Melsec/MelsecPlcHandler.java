package twim.melsecplc.Melsec;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twim.melsecplc.codec.ClientFrame3EAsciiMessageDecoder;
import twim.melsecplc.codec.ClientFrameEMessageEncoder;
import twim.melsecplc.codec.Frame3EAsciiByteDecoder;
import twim.melsecplc.core.MelsecClientOptions;
import twim.melsecplc.core.message.Function;
import twim.melsecplc.core.message.e.Frame3EAsciiCommand;
import twim.melsecplc.core.message.e.FrameECommand;
import twim.melsecplc.core.message.e.FrameEResponse;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Getter
public class MelsecPlcHandler {
    
    private static final Logger log = LoggerFactory.getLogger(MelsecPlcHandler.class);
    private Thread plcThread;
    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();;
    private Channel channel;

    private final MelsecClientConfig melsecClientConfig;
    private final Queue<FrameECommand> requestQueue = new LinkedList<>();
    private final Queue<FrameEResponse> responseQueue = new LinkedList<>();

    private volatile int currentValue = 0;
    private volatile int nitrogenValue = 0;
    private volatile int vaccumValue = 0;

    @Setter
    private boolean RFID;
    @Setter
    private String macAddress = "";

    private final Lock lock = new ReentrantLock();

    public MelsecPlcHandler(String ip, int port){
        
        this.melsecClientConfig = MelsecClientConfig.builder().address(ip).port(port).build();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(this.workerGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(this.melsecClientConfig.getAddress(), this.melsecClientConfig.getPort())
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
                            pipeline.addLast(new Frame3EAsciiByteDecoder());
                            pipeline.addLast(new ClientFrame3EAsciiMessageDecoder());
                            pipeline.addLast(new NettySocketClientHandler(MelsecPlcHandler.this));
                        }
                    });

        this.plcThread = new Thread(() -> {

            log.info("Attempt connect to PLC({})...", ip);

            connect(bootstrap);

            ByteBuf data = Unpooled.buffer();
            data.writeBoolean(false);
            data.writeBoolean(true);
            data.writeBoolean(false);
            data.writeBoolean(false);
            data.writeBoolean(true);
            data.writeBoolean(false);
            data.writeBoolean(true);
            data.writeBoolean(true);

            try {
                log.info(batchRead("R6110", 1).get());
            } catch (InterruptedException | ExecutionException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            log.info("Connected to PLC({})...", ip);

            while (true){
                try {
                    // this.currentValue = Integer.valueOf(batchRead("R6110", 1).get());
                    // this.nitrogenValue = batchRead("R6110", 1).get();
                    // this.vaccumValue = batchRead("R6110", 1).get();

                    Thread.sleep(1000);

                    if (!isConnected()){
                        this.currentValue = 0;
                        this.nitrogenValue = 0;
                        this.vaccumValue = 0;

                        connect(bootstrap);
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage());

                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "plc-" + ip);

        this.plcThread.setDaemon(true);
        this.plcThread.start();

        // while (true){
        //     try {
        //         ByteBuf data = Unpooled.buffer();
        //         data.writeBoolean(false);
        //         data.writeBoolean(true);
        //         data.writeBoolean(false);
        //         data.writeBoolean(false);
        //         data.writeBoolean(true);
        //         data.writeBoolean(false);
        //         data.writeBoolean(true);
        //         data.writeBoolean(true);

        //         // log.info(batchRead("R6110", 1).get());
        //         log.info(batchWrite("M100", 1, data).get());
            
        //         Thread.sleep(3000);
        //     } catch (Exception e) {

        //     }
        // }
    }

    public void connect(Bootstrap bootstrap){

        try {
            ChannelFuture f = bootstrap.connect().sync();

            this.channel = f.channel();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ee) {
                log.warn("connect " + ee.getMessage());
            }

            connect(bootstrap);
        }
    }

    public void remove(){

        if (this.plcThread != null && !this.plcThread.isInterrupted())
            this.plcThread.interrupt();
        this.workerGroup.shutdownGracefully().awaitUninterruptibly();
    }

    public boolean isConnected(){

        if (this.channel == null)
            return false;
        else
            return this.channel.isActive();
    }

    public CompletableFuture<String> batchRead(String address, int points){

        return requestAPI(new Frame3EAsciiCommand(
            Function.BATCH_READ,
            address,
            points,
            new MelsecClientOptions(this.melsecClientConfig.getNetworkNo(),
                this.melsecClientConfig.getPcNo(),
                this.melsecClientConfig.getRequestDestinationModuleIoNo(),
                this.melsecClientConfig.getRequestDestinationModuleStationNo())));
    }

    public CompletableFuture<String> batchWrite(String address, int points, ByteBuf data){

        return requestAPI(new Frame3EAsciiCommand(
            Function.BATCH_WRITE,
            address,
            points,
            data,
            new MelsecClientOptions(this.melsecClientConfig.getNetworkNo(),
                this.melsecClientConfig.getPcNo(),
                this.melsecClientConfig.getRequestDestinationModuleIoNo(),
                this.melsecClientConfig.getRequestDestinationModuleStationNo())))
            .thenCompose(r -> batchRead(address, points));
    }

    private CompletableFuture<String> requestAPI(FrameECommand command){
        
        Supplier<String> responseSupplier = () -> {
            
            this.lock.lock();

            long start = System.currentTimeMillis();
            this.channel.writeAndFlush(command).addListener(listener -> {
                if (!listener.isSuccess())
                    log.info(this.melsecClientConfig.getAddress() + "- Request failed: " + command.getPrincipal().getAddress()
                            + ", Thread- " + Thread.currentThread().getName());
                else
                    this.requestQueue.add(command);
            });

            try {
                while (true){
                    try {
                        if (this.responseQueue.size() > 0){
                            log.info(this.melsecClientConfig.getAddress() + "- Tact time: " + (System.currentTimeMillis() - start)
                                    + ", Thread- " + Thread.currentThread().getName());
                            
                            FrameEResponse response = this.responseQueue.poll();

                            if (response.getData() == null)
                                return "";
                            else
                                return ByteBufUtil.hexDump(response.getData());
                        }

                        // TODO - Timeout 처리
                        if ((System.currentTimeMillis() - start) > 1000)
                            return "Timeout";

                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        return "Error";
                    }
                }
            } finally {
                this.lock.unlock();
            }
        };

        return CompletableFuture.supplyAsync(responseSupplier);
    }

    /**
     * PLC 에 명령 전달
     * @param data
     * todo: 추후에 data format 맞춰서 보내기
     */
    public void sendCommand(String data){
        ByteBuf buf = Unpooled.copiedBuffer(data, CharsetUtil.UTF_8);
        ChannelFuture cf = channel.writeAndFlush(buf);

        cf.addListener((ChannelFutureListener) channelFuture -> {
            if(channelFuture.isSuccess()){
                System.out.println("write success");
            } else {
                System.out.println("write error");
                channelFuture.cause().printStackTrace();
            }
        });
    }
}