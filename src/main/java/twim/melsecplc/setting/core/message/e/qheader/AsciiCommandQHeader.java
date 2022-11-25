package twim.melsecplc.setting.core.message.e.qheader;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.setting.core.utils.ByteBufUtilities;

/**
 * @author liumin
 */
public class AsciiCommandQHeader extends AbstractCommandQHeader {

    public AsciiCommandQHeader() {
    }

    public AsciiCommandQHeader(int networkNo, int pcNo, int requestDestinationModuleIoNo,
                               int requestDestinationModuleStationNo, int cpuMonitoringTimer) {
        super(networkNo, pcNo, requestDestinationModuleIoNo, requestDestinationModuleStationNo, cpuMonitoringTimer);
    }

    @Override
    public void encode(ByteBuf buf) {
        ByteBufUtilities.writeByteAscii(buf, getNetworkNo());
        ByteBufUtilities.writeByteAscii(buf, getPcNo());
        ByteBufUtilities.writeShortAscii(buf, getRequestDestinationModuleIoNo());
        ByteBufUtilities.writeByteAscii(buf, getRequestDestinationModuleStationNo());
        ByteBufUtilities.writeShortAscii(buf, getRequestDataLength());
        ByteBufUtilities.writeShortAscii(buf, getCpuMonitoringTimer());
    }

    @Override
    public boolean decode(ByteBuf buf) {
        setNetworkNo(ByteBufUtilities.readByteAscii(buf));
        setPcNo(ByteBufUtilities.readByteAscii(buf));
        setRequestDestinationModuleIoNo(ByteBufUtilities.readShortAscii(buf));
        setRequestDestinationModuleStationNo(ByteBufUtilities.readByteAscii(buf));
        setRequestDataLength(ByteBufUtilities.readShortAscii(buf));
        setCpuMonitoringTimer(ByteBufUtilities.readShortAscii(buf));
        return true;
    }

    @Override
    public String toString() {
        return "AsciiCommandQHeader{" +
            "networkNo=" + getNetworkNo() +
            ", pcNo=" + getPcNo() +
            ", requestDestinationModuleIoNo=" + getRequestDestinationModuleIoNo() +
            ", requestDataLength=" + getRequestDataLength() +
            ", cpuMonitoringTimer=" + getCpuMonitoringTimer() +
            '}';
    }
}
