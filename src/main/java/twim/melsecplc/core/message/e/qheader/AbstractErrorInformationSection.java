package twim.melsecplc.core.message.e.qheader;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.core.message.Function;

/**
 * @author liumin
 */
public abstract class AbstractErrorInformationSection extends AbstractQHeader implements ErrorInformationSection {

    private Function function;

    private int subcommand;

    @Override
    public Function getFunction() {
        return function;
    }

    @Override
    public void setFunction(Function function) {
        this.function = function;
    }

    @Override
    public int getSubcommand() {
        return subcommand;
    }

    @Override
    public void setSubcommand(int subcommand) {
        this.subcommand = subcommand;
    }

    public abstract void encode(ByteBuf buf);

    public abstract boolean decode(ByteBuf buf);
}
