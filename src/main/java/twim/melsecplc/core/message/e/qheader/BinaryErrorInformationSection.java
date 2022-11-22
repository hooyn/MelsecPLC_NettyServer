package twim.melsecplc.core.message.e.qheader;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.core.message.Function;

/**
 * @author liumin
 */
public class BinaryErrorInformationSection extends AbstractErrorInformationSection {

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte(getNetworkNo());
        buf.writeByte(getPcNo());
        buf.writeShortLE(getRequestDestinationModuleIoNo());
        buf.writeByte(getRequestDestinationModuleStationNo());
        buf.writeShortLE(getFunction().getCommand());
        buf.writeShortLE(getSubcommand());
    }

    @Override
    public boolean decode(ByteBuf buf) {
        setNetworkNo(buf.readUnsignedByte());
        setPcNo(buf.readUnsignedByte());
        setRequestDestinationModuleIoNo(buf.readUnsignedShortLE());
        setRequestDestinationModuleStationNo(buf.readUnsignedByte());
        int command = buf.readUnsignedShortLE();
        // subcommand
        buf.readUnsignedShortLE();
        setFunction(Function.from(command));
        return true;
    }

    @Override
    public String toString() {
        return "BinaryErrorInformationSection{" +
            "networkNo(response station)=" + getNetworkNo() +
            ", pcNo(response station)=" + getPcNo() +
            ", requestDestinationModuleIoNo=" + getRequestDestinationModuleIoNo() +
            ", requestDestinationModuleStationNo=" + getRequestDestinationModuleStationNo() +
            ", function=" + getFunction() +
            '}';
    }
}
