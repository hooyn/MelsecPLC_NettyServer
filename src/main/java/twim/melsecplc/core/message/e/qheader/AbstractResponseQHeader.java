package twim.melsecplc.core.message.e.qheader;

import io.netty.buffer.ByteBuf;

/**
 * @author liumin
 */
public abstract class AbstractResponseQHeader extends AbstractQHeader {

    private int responseDataLength = 0;

    private int completeCode = 0;

    public AbstractResponseQHeader() {
    }

    public AbstractResponseQHeader(int networkNo, int pcNo, int requestDestinationModuleIoNo,
                                   int requestDestinationModuleStationNo, int completeCode) {
        super(networkNo, pcNo, requestDestinationModuleIoNo, requestDestinationModuleStationNo);
        this.completeCode = completeCode;
    }

    public int getResponseDataLength() {
        return responseDataLength;
    }

    public void setResponseDataLength(int responseDataLength) {
        this.responseDataLength = responseDataLength;
    }

    public int getCompleteCode() {
        return completeCode;
    }

    public void setCompleteCode(int completeCode) {
        this.completeCode = completeCode;
    }

    public abstract void encode(ByteBuf buf);

    public abstract boolean decode(ByteBuf buf);
}
