package twim.melsecplc.core.message.e;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.core.MelsecClientOptions;
import twim.melsecplc.core.message.AsciiPrincipal;
import twim.melsecplc.core.message.Function;
import twim.melsecplc.core.message.Principal;
import twim.melsecplc.core.message.e.qheader.AsciiCommandQHeader;
import twim.melsecplc.core.message.e.subheader.Frame3EAsciiCommandSubheader;

/**
 * @author liumin
 */
public class Frame3EAsciiCommand extends AbstractFrameECommand {

    public Frame3EAsciiCommand() {
    }

    public Frame3EAsciiCommand(Function function, String address, int points) {
        super(function, address, points);
    }

    public Frame3EAsciiCommand(Function function, String address, int points, MelsecClientOptions options) {
        super(function, address, points, options);
    }

    public Frame3EAsciiCommand(Function function, String address, int points, ByteBuf data) {
        super(function, address, points, data);
    }

    public Frame3EAsciiCommand(Function function, String address, int points, ByteBuf data, MelsecClientOptions options) {
        super(function, address, points, data, options);
    }

    @Override
    public Principal newPrincipal() {
        return new AsciiPrincipal(getFunction(), getAddress(), getPoints(), getData());
    }

    @Override
    public Subheader newSubheader() {
        return (Subheader) Frame3EAsciiCommandSubheader.getInstance();
    }

    @Override
    public QHeader newQHeader() {
        return new AsciiCommandQHeader();
    }

    @Override
    public String toString() {
        return "Frame3EAsciiCommand{" +
            "qHeader=" + getQHeader() +
            ", principal=" + getPrincipal() +
            '}';
    }
}
