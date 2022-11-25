package twim.melsecplc.setting.core.message.e;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.setting.core.MelsecClientOptions;
import twim.melsecplc.setting.core.message.BinaryPrincipal;
import twim.melsecplc.setting.core.message.Function;
import twim.melsecplc.setting.core.message.Principal;
import twim.melsecplc.setting.core.message.e.qheader.BinaryCommandQHeader;
import twim.melsecplc.setting.core.message.e.subheader.Frame3EBinaryCommandSubheader;

/**
 * @author liumin
 */
public class Frame3EBinaryCommand extends AbstractFrameECommand {

    public Frame3EBinaryCommand() {
    }

    public Frame3EBinaryCommand(Function function, String address, int points) {
        super(function, address, points);
    }

    public Frame3EBinaryCommand(Function function, String address, int points, ByteBuf data) {
        super(function, address, points, data);
    }

    public Frame3EBinaryCommand(Function function, String address, int points, MelsecClientOptions options) {
        super(function, address, points, options);
    }

    public Frame3EBinaryCommand(Function function, String address, int points, ByteBuf data, MelsecClientOptions options) {
        super(function, address, points, data, options);
    }

    @Override
    public Principal newPrincipal() {
        return new BinaryPrincipal(getFunction(), getAddress(), getPoints(), getData());
    }

    @Override
    public Subheader newSubheader() {
        return (Subheader) Frame3EBinaryCommandSubheader.getInstance();
    }

    @Override
    public QHeader newQHeader() {
        return new BinaryCommandQHeader();
    }

    @Override
    public String toString() {
        return "Frame3EBinaryCommand{" +
            "qHeader=" + getQHeader() +
            ", principal=" + getPrincipal() +
            '}';
    }
}
