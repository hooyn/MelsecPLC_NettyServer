package twim.melsecplc.core.message.e.qheader;

import io.netty.buffer.ByteBuf;

/**
 * @author liumin
 */
public class BinaryCommandQHeader extends AbstractCommandQHeader {

    public BinaryCommandQHeader() {
    }

    public BinaryCommandQHeader(int networkNo, int pcNo, int requestDestinationModuleIoNo,
                                int requestDestinationModuleStationNo, int cpuMonitoringTimer) {
        super(networkNo, pcNo, requestDestinationModuleIoNo, requestDestinationModuleStationNo, cpuMonitoringTimer);
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte(getNetworkNo());
        buf.writeByte(getPcNo());
        buf.writeShortLE(getRequestDestinationModuleIoNo());
        buf.writeByte(getRequestDestinationModuleStationNo());
        buf.writeShortLE(getRequestDataLength());
        buf.writeShortLE(getCpuMonitoringTimer());
    }

    @Override
    public boolean decode(ByteBuf buf) {
        setNetworkNo(buf.readUnsignedByte());
        setPcNo(buf.readUnsignedByte());
        setRequestDestinationModuleIoNo(buf.readUnsignedShortLE());
        setRequestDestinationModuleStationNo(buf.readUnsignedByte());
        setRequestDataLength(buf.readUnsignedShortLE());
        setCpuMonitoringTimer(buf.readUnsignedShortLE());
        return true;
    }

    @Override
    public String toString() {
        return "BinaryCommandQHeader{" +
            "networkNo=" + getNetworkNo() +
            ", pcNo=" + getPcNo() +
            ", requestDestinationModuleIoNo=" + getRequestDestinationModuleIoNo() +
            ", requestDataLength=" + getRequestDataLength() +
            ", cpuMonitoringTimer=" + getCpuMonitoringTimer() +
            '}';
    }
}
