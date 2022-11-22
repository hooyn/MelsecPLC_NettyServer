package twim.melsecplc.core.message.e;

/**
 * @author liumin
 */
public interface FrameEMessage {

    /**
     * 获取副头部
     *
     * @return 副头部
     */
    Subheader getSubheader();

    /**
     * 获取Q Header
     *
     * @return Q Header
     */
    QHeader getQHeader();

}
