package twim.melsecplc.core.message.e.qheader;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.core.message.Function;
import twim.melsecplc.core.utils.ByteBufUtilities;

/**
 * @author liumin
 */
public class AsciiErrorInformationSection extends AbstractErrorInformationSection {

    @Override
    public void encode(ByteBuf buf) {
        ByteBufUtilities.writeByteAscii(buf, getNetworkNo());
        ByteBufUtilities.writeByteAscii(buf, getPcNo());
        ByteBufUtilities.writeShortAscii(buf, getRequestDestinationModuleIoNo());
        ByteBufUtilities.writeByteAscii(buf, getRequestDestinationModuleStationNo());
        ByteBufUtilities.writeShortAscii(buf, getFunction().getCommand());
        ByteBufUtilities.writeShortAscii(buf, getSubcommand());
    }

    @Override
    public boolean decode(ByteBuf buf) {
        setNetworkNo(ByteBufUtilities.readByteAscii(buf));
        setPcNo(ByteBufUtilities.readByteAscii(buf));
        setRequestDestinationModuleIoNo(ByteBufUtilities.readShortAscii(buf));
        setRequestDestinationModuleStationNo(ByteBufUtilities.readByteAscii(buf));
        int command = ByteBufUtilities.readShortAscii(buf);
        // subcommand
        ByteBufUtilities.readShortAscii(buf);
        setFunction(Function.from(command));
        return true;
    }

    @Override
    public String toString() {
        return "AsciiErrorInformationSection{" +
            "networkNo(response station)=" + getNetworkNo() +
            ", pcNo(response station)=" + getPcNo() +
            ", requestDestinationModuleIoNo=" + getRequestDestinationModuleIoNo() +
            ", requestDestinationModuleStationNo=" + getRequestDestinationModuleStationNo() +
            ", function=" + getFunction() +
            '}';
    }
}
