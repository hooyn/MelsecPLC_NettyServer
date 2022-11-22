package twim.melsecplc.core.message.e.qheader;

import io.netty.buffer.ByteBuf;

/**
 * @author liumin
 */
public class BinaryResponseQHeader extends AbstractResponseQHeader {

    public BinaryResponseQHeader() {
    }

    public BinaryResponseQHeader(int networkNo, int pcNo, int requestDestinationModuleIoNo,
                                 int requestDestinationModuleStationNo, int completeCode) {
        super(networkNo, pcNo, requestDestinationModuleIoNo, requestDestinationModuleStationNo, completeCode);
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte(getNetworkNo());
        buf.writeByte(getPcNo());
        buf.writeShortLE(getRequestDestinationModuleIoNo());
        buf.writeByte(getRequestDestinationModuleStationNo());
        buf.writeShortLE(getResponseDataLength());
        buf.writeShortLE(getCompleteCode());
    }

    @Override
    public boolean decode(ByteBuf buf) {
        setNetworkNo(buf.readUnsignedByte());
        setPcNo(buf.readUnsignedByte());
        setRequestDestinationModuleIoNo(buf.readUnsignedShortLE());
        setRequestDestinationModuleStationNo(buf.readUnsignedByte());
        setResponseDataLength(buf.readUnsignedShortLE());
        setCompleteCode(buf.readUnsignedShortLE());
        return true;
    }

    @Override
    public String toString() {
        return "BinaryResponseQHeader{" +
            "networkNo=" + getNetworkNo() +
            ", pcNo=" + getPcNo() +
            ", requestDestinationModuleIoNo=" + getRequestDestinationModuleIoNo() +
            ", responseDataLength=" + getResponseDataLength() +
            ", completeCode=" + getCompleteCode() +
            '}';
    }
}
