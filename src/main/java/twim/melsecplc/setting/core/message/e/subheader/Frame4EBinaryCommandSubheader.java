package twim.melsecplc.setting.core.message.e.subheader;

/**
 * @author liumin
 */
public class Frame4EBinaryCommandSubheader extends AbstractFrame4EBinarySubheader {

    public Frame4EBinaryCommandSubheader() {
    }

    public Frame4EBinaryCommandSubheader(int serialNo) {
        super(serialNo);
    }

    @Override
    protected byte[] getFrontCodes() {
        return new byte[]{0x54, 0x00};
    }

    @Override
    protected byte[] getEndCodes() {
        return new byte[]{0x00, 0x00};
    }
}
