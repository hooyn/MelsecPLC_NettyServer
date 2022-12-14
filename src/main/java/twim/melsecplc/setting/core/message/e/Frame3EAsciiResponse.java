package twim.melsecplc.setting.core.message.e;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import twim.melsecplc.setting.core.message.e.qheader.AbstractResponseQHeader;
import twim.melsecplc.setting.core.message.e.qheader.AsciiErrorInformationSection;
import twim.melsecplc.setting.core.message.e.qheader.AsciiResponseQHeader;
import twim.melsecplc.setting.core.message.e.qheader.ErrorInformationSection;
import twim.melsecplc.setting.core.message.e.subheader.Frame3EAsciiResponseSubheader;

/**
 * @author liumin
 */
public class Frame3EAsciiResponse extends AbstractFrameEResponse {

    private ByteBuf data;

    private ErrorInformationSection errorInformationSection;

    @Override
    protected Subheader newSubheader() {
        return (Subheader) Frame3EAsciiResponseSubheader.getInstance();
    }

    @Override
    protected AbstractResponseQHeader newQHeader() {
        return new AsciiResponseQHeader();
    }

    @Override
    public ByteBuf getData() {
        return data;
    }

    @Override
    public void setData(ByteBuf data) {
        this.data = data;
    }

    @Override
    public ErrorInformationSection getErrorInformationSection() {
        if (errorInformationSection == null) {
            errorInformationSection = new AsciiErrorInformationSection();
        }
        return errorInformationSection;
    }

    @Override
    public String toString() {
        return "Frame3EAsciiResponse{" +
            "qHeader=" + getQHeader() +
            ", data=" + (getData() != null ? getData().toString(CharsetUtil.UTF_8) + " / raw: " + ByteBufUtil.hexDump(getData()) : null) +
            ", errorInformationSection=" + errorInformationSection +
            '}';
    }
}