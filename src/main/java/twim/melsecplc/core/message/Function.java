package twim.melsecplc.core.message;

/**
 * @author liumin
 */
public enum Function {

    // 0x0001 （位）

    /**
     * 批量读取
     */
    BATCH_READ(0x0401),

    /**
     * 批量写入(位单位）
     */
    BATCH_WRITE(0x1401),

    /**
     * 随机写入(位单位）
     */
    RANDOM_WRITE(0x1402),

    ;

    private int command;

    Function(int command) {
        this.command = command;
    }

    public int getCommand() {
        return command;
    }

    public static Function from(int command) {
        for (Function function : Function.values()) {
            if (function.command == command) {
                return function;
            }
        }
        return null;
    }
}
