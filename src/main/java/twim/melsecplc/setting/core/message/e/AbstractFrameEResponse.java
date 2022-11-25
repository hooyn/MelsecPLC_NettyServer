package twim.melsecplc.setting.core.message.e;

import twim.melsecplc.setting.core.message.e.qheader.AbstractResponseQHeader;

/**
 * @author liumin
 */
public abstract class AbstractFrameEResponse implements FrameEResponse {

    private Subheader subheader;

    private AbstractResponseQHeader qHeader;

    @Override
    public Subheader getSubheader() {
        if (subheader == null) {
            subheader = newSubheader();
        }
        return subheader;
    }

    @Override
    public AbstractResponseQHeader getQHeader() {
        if (qHeader == null) {
            qHeader = newQHeader();
        }
        return qHeader;
    }

    /**
     * 新建Subheader
     *
     * @return Subheader
     */
    protected abstract Subheader newSubheader();

    /**
     * 新建QHeader
     *
     * @return QHeader
     */
    protected abstract AbstractResponseQHeader newQHeader();

}
