package twim.melsecplc.core.message.e;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import twim.melsecplc.core.message.e.qheader.AbstractResponseQHeader;
import twim.melsecplc.core.message.e.qheader.BinaryErrorInformationSection;
import twim.melsecplc.core.message.e.qheader.BinaryResponseQHeader;
import twim.melsecplc.core.message.e.qheader.ErrorInformationSection;
import twim.melsecplc.core.message.e.subheader.Frame3EBinaryResponseSubheader;

/**
 * @author liumin
 */
public class Frame3EBinaryResponse extends AbstractFrameEResponse {

    private ByteBuf data;

    private ErrorInformationSection errorInformationSection;

    @Override
    protected Subheader newSubheader() {
        return Frame3EBinaryResponseSubheader.getInstance();
    }

    @Override
    protected AbstractResponseQHeader newQHeader() {
        return new BinaryResponseQHeader();
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
            errorInformationSection = new BinaryErrorInformationSection();
        }
        return errorInformationSection;
    }

    @Override
    public String toString() {
        return "Frame3EBinaryResponse{" +
            "qHeader=" + getQHeader() +
            ", data=" + (getData() != null ? ByteBufUtil.hexDump(getData()) : null) +
            ", errorInformationSection=" + errorInformationSection +
            '}';
    }
}
