package twim.melsecplc.setting.core.message.e.subheader;

/**
 * @author liumin
 */
public class Frame3EAsciiCommandSubheader extends AbstractFrame3ESubheader {

    private static final Frame3EAsciiCommandSubheader INSTANCE = new Frame3EAsciiCommandSubheader();

    private Frame3EAsciiCommandSubheader() {
    }

    public static Frame3EAsciiCommandSubheader getInstance() {
        return INSTANCE;
    }

    @Override
    protected byte[] getCodes() {
        return new byte[]{0x35, 0x30, 0x30, 0x30};
    }
}
