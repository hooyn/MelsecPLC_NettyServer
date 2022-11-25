package twim.melsecplc.setting.core.message.e.subheader;

/**
 * @author liumin
 */
public class Frame3EAsciiResponseSubheader extends AbstractFrame3ESubheader {

    private static final Frame3EAsciiResponseSubheader INSTANCE = new Frame3EAsciiResponseSubheader();

    private Frame3EAsciiResponseSubheader() {
    }

    public static Frame3EAsciiResponseSubheader getInstance() {
        return INSTANCE;
    }

    @Override
    protected byte[] getCodes() {
        return new byte[]{0x44, 0x30, 0x30, 0x30};
    }
}
