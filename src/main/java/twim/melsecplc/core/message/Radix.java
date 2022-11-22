package twim.melsecplc.core.message;

/**
 * @author liumin
 */
public enum Radix {

    /**
     * 16进制
     */
    HEXADECIMAL(16),

    /**
     * 10进制
     */
    DECIMAL(10);

    private int value;

    Radix(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
