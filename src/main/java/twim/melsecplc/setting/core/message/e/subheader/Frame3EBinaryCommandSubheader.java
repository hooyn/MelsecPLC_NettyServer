package twim.melsecplc.setting.core.message.e.subheader;

/**
 * @author liumin
 */
public class Frame3EBinaryCommandSubheader extends AbstractFrame3ESubheader {

    private static final Frame3EBinaryCommandSubheader INSTANCE = new Frame3EBinaryCommandSubheader();

    private Frame3EBinaryCommandSubheader() {
    }

    public static Frame3EBinaryCommandSubheader getInstance() {
        return INSTANCE;
    }

    @Override
    protected byte[] getCodes() {
        return new byte[]{0x50, 0x00};
    }

}
