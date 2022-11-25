# melsecPLC_NettyServer
Melsec PLC Communication Server By Netty

I have referred to the link at https://github.com/netty

## **Mc Protocol Structure**
## D*045000 Test

---

### <PLC Response>

- first - `5b3f3f3f`  **UTF_8: ?[??**
    
    ```prolog
    2022-11-23 11:37:12.968  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler : Response packet Frame3EAsciiResponse
    {qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
    2022-11-23 11:37:14.437  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
    ```
    
- second - `3f5b3f3f` **UTF_8: ?[??**
    
    ```prolog
    2022-11-23 11:37:14.530  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler : Response packet Frame3EAsciiResponse
    {qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=3f5b3f3f, errorInformationSection=null}
    2022-11-23 11:37:15.946  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
    ```
    
- third - `503f` **UTF_8: ?P**
    
    ```prolog
    2022-11-23 11:37:19.008  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler : Response packet Frame3EAsciiResponse
    {qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=503f, errorInformationSection=null}
    2022-11-23 11:37:20.492  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
    ```
    

### <Solution Plan>

- `ClientFrame3EAsciiMessageDecoder` Response가 들어오는지 확인
    - [log.info](http://log.info) 찍어보기 → false
    
    ```java
    <Before>
    pipeline.addLast(ClientFrameEMessageEncoder.INSTANCE);
    pipeline.addLast(new Frame3EAsciiByteDecoder());
    pipeline.addLast(new ClientFrame3EAsciiMessageDecoder());
    pipeline.addLast(new NettySocketClientHandler(MelsecPlcHandler.this));
    ```
    
    ```java
    <After>
    pipeline.addLast(ClientFrameEMessageEncoder.INSTANCE);
    pipeline.addLast(new ClientFrame3EAsciiMessageDecoder()); **//위치 변경**
    pipeline.addLast(new Frame3EAsciiByteDecoder());
    pipeline.addLast(new NettySocketClientHandler(MelsecPlcHandler.this));
    ```
    
    > 
    > 
- `ClientFrame3EAsciiMessageDecoder` Response 확인
    - 데이터를 찍어보니까 `[[?[` 다음과 같이 넘어왔음
    - 그리고 ByteBuf를 읽는 사이즈가 맞지 않았음
        - PLC ByteBuf Size =  4 → 읽으려고 하는 사이즈(4)는 그보다 큰 26을 읽는다.
        - subHeader, qHeader decode() 하는 부분 제거 (Subheader, AccessRoute 등 정보는 보내지 않고, Response Data만 보냄
        - readAsciiBuf() 코드 새로 작성 `readAsciiBufCustom`
            - `result.writeByte((byte) Integer.parseInt(hex, 16));` Error 발생
            - Response 데이터가 String이어서 integer로 형변환 부분에서 Error 발생
            - 형변환 코드 제거하고, 바로 읽어서 반환
    
    ```java
        public static ByteBuf readAsciiBufCustom(ByteBuf buf) {
            int remaining = buf.readableBytes(); //this.writerIndex - this.readerIndex
            if (remaining < 0) {
                return null;
            }
    
            ByteBuf result = Unpooled.buffer(remaining);
    
            while (buf.readableBytes() > 0) {
                byte[] bytes = new byte[2];
                buf.readBytes(bytes);
                String hex = new String(bytes, StandardCharsets.US_ASCII);
                result.writeBytes(hex.getBytes()); **//이부분 추가**
    						result.writeByte((byte) Integer.parseInt(hex, 16)); **//제거**
            }
            return result;
        }
    ```
    

Success

```scala
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.5)

**2022-11-23 11:37:04.365  INFO 66908 --- [           main] twim.melsecplc.MelsecPlcApplication      : Starting MelsecPlcApplication v0.0.1-SNAPSHOT using Java 17.0.5 on DESKTOP-GJRCHMS with PID 66908 (C:\Users\user\Desktop\melsecPLC_netty_server_ver_0.0.7.jar started by user in C:\Program Files\Java\jdk-17.0.5)
2022-11-23 11:37:04.366  INFO 66908 --- [           main] twim.melsecplc.MelsecPlcApplication      : No active profile set, falling back to 1 default profile: "default"
2022-11-23 11:37:05.012  INFO 66908 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): ********* (http)
2022-11-23 11:37:05.012  INFO 66908 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2022-11-23 11:37:05.012  INFO 66908 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.68]
2022-11-23 11:37:05.059  INFO 66908 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2022-11-23 11:37:05.059  INFO 66908 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 659 ms
2022-11-23 11:37:05.106  INFO 66908 --- [       Thread-1] twim.melsecplc.Melsec.MelsecController   : Start McProtocol TCP
2022-11-23 11:37:05.200  INFO 66908 --- [-*********] twim.melsecplc.Melsec.MelsecPlcHandler   : Attempt connect to PLC(*********)...
2022-11-23 11:37:05.302  INFO 66908 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): ********* (http) with context path ''
2022-11-23 11:37:05.315  INFO 66908 --- [           main] twim.melsecplc.MelsecPlcApplication      : Started MelsecPlcApplication in 1.221 seconds (JVM running for 1.47)
2022-11-23 11:37:05.330  INFO 66908 --- [-*********] twim.melsecplc.Melsec.MelsecPlcHandler   : Connected to PLC(*********)...
2022-11-23 11:37:05.343  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:05.390  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:05.390  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 2048)
2022-11-23 11:37:05.390  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ?[??
2022-11-23 11:37:05.390  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:05.390  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ?[??
2022-11-23 11:37:05.390  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:05.390  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:06.858  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:06.858  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:06.905  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:06.905  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 2048)
2022-11-23 11:37:06.905  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:06.905  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:06.905  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:06.905  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:06.905  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:06.936  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:06.936  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 1024)
2022-11-23 11:37:06.936  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:06.936  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:06.936  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:06.936  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:06.936  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=3f5b3f3f, errorInformationSection=null}
2022-11-23 11:37:08.374  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:08.374  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:08.389  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:08.389  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 1024)
2022-11-23 11:37:08.389  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:08.389  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:08.389  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:08.389  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:08.389  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:09.889  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:09.889  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:11.404  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 512)
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:11.437  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 2, cap: 512)
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��P
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?P
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��P
2022-11-23 11:37:11.437  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:11.453  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=503f, errorInformationSection=null}
2022-11-23 11:37:12.921  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:12.921  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:12.953  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:12.953  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 496)
2022-11-23 11:37:12.953  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ?[??
2022-11-23 11:37:12.968  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:12.968  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ?[??
2022-11-23 11:37:12.968  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:12.968  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:14.437  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:14.437  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:14.484  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:14.484  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 496)
2022-11-23 11:37:14.484  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:14.484  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:14.484  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:14.484  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:14.484  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:14.530  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:14.530  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 480)
2022-11-23 11:37:14.530  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:14.530  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:14.530  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:14.530  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:14.530  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=3f5b3f3f, errorInformationSection=null}
2022-11-23 11:37:15.946  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:15.946  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:15.993  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:15.993  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 480)
2022-11-23 11:37:15.993  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:15.993  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:15.993  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:15.993  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:15.993  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:17.462  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:17.462  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:18.977  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:18.977  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 464)
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:19.008  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 2, cap: 464)
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��P
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?P
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��P
2022-11-23 11:37:19.008  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:19.008  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=503f, errorInformationSection=null}
2022-11-23 11:37:20.492  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:20.492  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:20.539  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:20.539  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 448)
2022-11-23 11:37:20.539  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ?[??
2022-11-23 11:37:20.539  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:20.539  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ?[??
2022-11-23 11:37:20.539  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:20.539  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:22.007  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:22.007  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:22.039  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:22.039  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 448)
2022-11-23 11:37:22.039  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:22.039  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:22.054  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:22.054  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:22.054  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
2022-11-23 11:37:22.070  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:22.070  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 432)
2022-11-23 11:37:22.070  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:22.070  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:22.070  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:22.070  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:22.070  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=3f5b3f3f, errorInformationSection=null}
2022-11-23 11:37:23.523  INFO 66908 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 0, Thread- ForkJoinPool.commonPool-worker-1
2022-11-23 11:37:23.523  INFO 66908 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Sending Packet Check: true
2022-11-23 11:37:23.554  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output Start----------
2022-11-23 11:37:23.554  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : toString(): PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 432)
2022-11-23 11:37:23.554  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : US_ASCII: ��[??
2022-11-23 11:37:23.554  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : UTF_8: ?[??
2022-11-23 11:37:23.554  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ISO_8859_1: ��[??
2022-11-23 11:37:23.554  WARN 66908 --- [ntLoopGroup-2-1] t.m.c.ClientFrame3EAsciiMessageDecoder   : ----------Data Output End----------
2022-11-23 11:37:23.554  INFO 66908 --- [ntLoopGroup-2-1] NettySocketClientHandler                 : Response packet Frame3EAsciiResponse{qHeader=AsciiResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=0, completeCode=0}, data=5b3f3f3f, errorInformationSection=null}
C:\Program Files\Java\jdk-17.0.5>2022-11-23 11:37:24.116  INFO 66908 --- [ionShutdownHook] twim.melsecplc.Melsec.MelsecController   : Destroy McProtocol TCP { Port: ********* }**
```

<data 값 이상>
1. Request에 대한 Response 값인지 확인 : False
    1. Request를 보내지 않으면 Response도 오지 않는다. 
2. 데이터는 정확히 오지만 파싱하는 로직에서 이상이 있는지 확인 : False
    1. Response를 받자마자 로그를 찍어봐도 같은 값 출력
    2. 또한 다른 Address로 데이터를 보내도 같은 값 출력
        1. 현재 반환되는 Response값은 빈 메모리에 저장되어 있는 null과 같은 값이라고 판단
        2. 현재 통신을 시도하는 Address값에 아무 값도 없어서 Response가 제대로 오지 않는다.
3. 정확하지 않은 PLC Address로 데이터를 보내면 요청 실패
4. 보내고 있는 PLC 주소가 맞는지 확인
5. PLC 주소가 맞다면 Request를 어떻게 보내야하는지 확인
    1. 구조가 다르다면 구조에 맞게 변환


### **→ 최종 ASCII 에서 Binary로 데이터 형식을 변경해주니까 됐습니다!**

```java
**C:\*********\melsecPlc-0.0.1-SNAPSHOT_ver2.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.5)

2022-11-25 13:26:06.396  INFO 81216 --- [           main] twim.melsecplc.MelsecPlcApplication      : Starting MelsecPlcApplication v0.0.1-SNAPSHOT using Java 17.0.5 on DESKTOP-GJRCHMS with PID 81216 (C:\Users\user\Desktop\melsecPlc-0.0.1-SNAPSHOT_ver2.jar started by user in *********)
2022-11-25 13:26:06.396  INFO 81216 --- [           main] twim.melsecplc.MelsecPlcApplication      : No active profile set, falling back to 1 default profile: "default"
2022-11-25 13:26:07.045  INFO 81216 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): ********* (http)
2022-11-25 13:26:07.045  INFO 81216 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2022-11-25 13:26:07.045  INFO 81216 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.68]
2022-11-25 13:26:07.092  INFO 81216 --- [           main] o.a.c.c.C.[Tomcat].[*********].[/]       : Initializing Spring embedded WebApplicationContext
2022-11-25 13:26:07.107  INFO 81216 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 676 ms
2022-11-25 13:26:07.139  INFO 81216 --- [       Thread-1] twim.melsecplc.Melsec.MelsecController   : Netty Server Open [IP: *********, Port: *********]
2022-11-25 13:26:07.349  INFO 81216 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): ********* (http) with context path ''
2022-11-25 13:26:07.349  INFO 81216 --- [           main] twim.melsecplc.MelsecPlcApplication      : Started MelsecPlcApplication in 1.238 seconds (JVM running for 1.489)
2022-11-25 13:26:07.397  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45010', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:07.428  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=*********, errorInformationSection=null}
2022-11-25 13:26:07.430  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 65, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:07.946  INFO 81216 --- [-*********] twim.melsecplc.Melsec.MelsecPlcHandler   : Connected to PLC [IP: *********]
2022-11-25 13:26:07.946  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:07.955  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0100, errorInformationSection=null}
2022-11-25 13:26:07.956  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 10, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:08.472  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:08.483  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0100, errorInformationSection=null}
2022-11-25 13:26:08.484  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 12, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:09.000  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:09.012  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0000, errorInformationSection=null}
2022-11-25 13:26:09.014  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 15, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:09.529  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:09.542  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0000, errorInformationSection=null}
2022-11-25 13:26:09.543  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 14, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:10.059  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:10.083  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0100, errorInformationSection=null}
2022-11-25 13:26:10.085  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 26, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:10.601  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:10.626  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0100, errorInformationSection=null}
2022-11-25 13:26:10.627  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 26, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:11.142  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:11.165  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0000, errorInformationSection=null}
2022-11-25 13:26:11.166  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 24, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:11.682  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:11.706  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0000, errorInformationSection=null}
2022-11-25 13:26:11.708  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 26, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:12.224  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:12.242  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0100, errorInformationSection=null}
2022-11-25 13:26:12.244  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 20, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:12.759  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:12.781  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0100, errorInformationSection=null}
2022-11-25 13:26:12.782  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 23, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:13.297  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:13.304  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0000, errorInformationSection=null}
2022-11-25 13:26:13.305  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 8, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:13.821  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:13.836  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0000, errorInformationSection=null}
2022-11-25 13:26:13.837  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 16, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:14.352  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:14.359  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0100, errorInformationSection=null}
2022-11-25 13:26:14.360  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 8, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:14.876  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:14.884  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0100, errorInformationSection=null}
2022-11-25 13:26:14.886  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 10, Thread- ForkJoinPool.commonPool-worker-1
2022-11-25 13:26:15.402  INFO 81216 --- [ntLoopGroup-2-1] t.m.codec.ClientFrameEMessageEncoder     : Request packet Frame3EBinaryCommand{qHeader=BinaryCommandQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, requestDataLength=12, cpuMonitoringTimer=16}, principal=AsciiPrincipal{address='D45000', points=1, function=BATCH_READ, subcommand=0, data=null}}
2022-11-25 13:26:15.412  WARN 81216 --- [ntLoopGroup-2-1] t.m.Melsec.NettySocketClientHandler      : Response Packet Frame3EBinaryResponse
{qHeader=BinaryResponseQHeader{networkNo=0, pcNo=255, requestDestinationModuleIoNo=1023, responseDataLength=4, completeCode=0}, data=0000, errorInformationSection=null}
2022-11-25 13:26:15.414  WARN 81216 --- [onPool-worker-1] twim.melsecplc.Melsec.MelsecPlcHandler   : *********- Tact time: 12, Thread- ForkJoinPool.commonPool-worker-1
C:\Users\user>2022-11-25 13:26:21.856  INFO 81216 --- [ionShutdownHook] twim.melsecplc.Melsec.MelsecController   : Netty Server Close [IP: *********, Port: *********]**
```
