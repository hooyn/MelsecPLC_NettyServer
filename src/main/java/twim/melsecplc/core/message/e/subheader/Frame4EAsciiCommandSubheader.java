package twim.melsecplc.core.message.e.subheader;

/**
 * @author liumin
 */
public class Frame4EAsciiCommandSubheader extends AbstractFrame4EAsciiSubheader {

    public Frame4EAsciiCommandSubheader() {
    }

    public Frame4EAsciiCommandSubheader(int serialNo) {
        super(serialNo);
    }

    @Override
    protected byte[] getFrontCodes() {
        return new byte[]{0x35, 0x34, 0x30, 0x30};
    }

    @Override
    protected byte[] getEndCodes() {
        return new byte[]{0x30, 0x30, 0x30, 0x30};
    }
}
