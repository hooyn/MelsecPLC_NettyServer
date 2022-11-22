package twim.melsecplc.core.message.e.qheader;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.core.utils.ByteBufUtilities;

/**
 * @author liumin
 */
public class AsciiResponseQHeader extends AbstractResponseQHeader {

    public AsciiResponseQHeader() {
    }

    public AsciiResponseQHeader(int networkNo, int pcNo, int requestDestinationModuleIoNo,
                                int requestDestinationModuleStationNo, int completeCode) {
        super(networkNo, pcNo, requestDestinationModuleIoNo, requestDestinationModuleStationNo, completeCode);
    }

    @Override
    public void encode(ByteBuf buf) {
        ByteBufUtilities.writeByteAscii(buf, getNetworkNo());
        ByteBufUtilities.writeByteAscii(buf, getPcNo());
        ByteBufUtilities.writeShortAscii(buf, getRequestDestinationModuleIoNo());
        ByteBufUtilities.writeByteAscii(buf, getRequestDestinationModuleStationNo());
        ByteBufUtilities.writeShortAscii(buf, getResponseDataLength());
        ByteBufUtilities.writeShortAscii(buf, getCompleteCode());
    }

    @Override
    public boolean decode(ByteBuf buf) {
        setNetworkNo(ByteBufUtilities.readByteAscii(buf));
        setPcNo(ByteBufUtilities.readByteAscii(buf));
        setRequestDestinationModuleIoNo(ByteBufUtilities.readShortAscii(buf));
        setRequestDestinationModuleStationNo(ByteBufUtilities.readByteAscii(buf));
        setResponseDataLength(ByteBufUtilities.readShortAscii(buf));
        setCompleteCode(ByteBufUtilities.readShortAscii(buf));
        return true;
    }

    @Override
    public String toString() {
        return "AsciiResponseQHeader{" +
            "networkNo=" + getNetworkNo() +
            ", pcNo=" + getPcNo() +
            ", requestDestinationModuleIoNo=" + getRequestDestinationModuleIoNo() +
            ", responseDataLength=" + getResponseDataLength() +
            ", completeCode=" + getCompleteCode() +
            '}';
    }
}
