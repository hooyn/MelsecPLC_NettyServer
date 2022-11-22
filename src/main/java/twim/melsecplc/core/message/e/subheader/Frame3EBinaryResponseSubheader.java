package twim.melsecplc.core.message.e.subheader;

/**
 * @author liumin
 */
public class Frame3EBinaryResponseSubheader extends AbstractFrame3ESubheader {

    private static final Frame3EBinaryResponseSubheader INSTANCE = new Frame3EBinaryResponseSubheader();

    private Frame3EBinaryResponseSubheader() {
    }

    public static Frame3EBinaryResponseSubheader getInstance() {
        return INSTANCE;
    }

    @Override
    protected byte[] getCodes() {
        return new byte[]{(byte) 0xD0, 0x00};
    }
}
