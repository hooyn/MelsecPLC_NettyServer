package twim.melsecplc.core.message.e;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.core.message.e.qheader.AbstractResponseQHeader;
import twim.melsecplc.core.message.e.qheader.ErrorInformationSection;

/**
 * @author liumin
 */
public interface FrameEResponse extends FrameEMessage {

    /**
     * 获取Q Header
     *
     * @return Q Header
     */
    @Override
    AbstractResponseQHeader getQHeader();

    /**
     * 设置读取到的数据
     *
     * @param data 数据Buffer
     */
    void setData(ByteBuf data);

    /**
     * 获取读取到的数据
     *
     * @return 数据Buffer
     */
    ByteBuf getData();

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    ErrorInformationSection getErrorInformationSection();
}
