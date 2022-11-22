package twim.melsecplc.core.message;

/**
 * @author liumin
 */
public enum Device {

    /**
     * X输入寄存器
     */
    X(UnitType.BIT, "X*", 0x9C, Radix.HEXADECIMAL, 2048),

    /**
     * Y输出寄存器
     */
    Y(UnitType.BIT, "Y*", 0x9D, Radix.HEXADECIMAL, 2048),

    /**
     * M中间寄存器
     */
    M(UnitType.BIT, "M*", 0x90, Radix.DECIMAL, 8192),

    /**
     * D数据寄存器
     */
    D(UnitType.WORD, "D*", 0xA8, Radix.DECIMAL, 11136),

    /**
     * W链接寄存器
     */
    W(UnitType.WORD, "W*", 0xB4, Radix.HEXADECIMAL, 2048),

    /**
     * L锁存继电器
     */
    L(UnitType.BIT, "L*", 0x92, Radix.DECIMAL, 2028),

    /**
     * F报警器
     */
    F(UnitType.BIT, "F*", 0x93, Radix.DECIMAL, 1024),

    /**
     * V边沿继电器
     */
    V(UnitType.BIT, "V*", 0x94, Radix.DECIMAL, 1024),

    /**
     * B链接继电器
     */
    B(UnitType.BIT, "B*", 0xA0, Radix.HEXADECIMAL, 2048),

    /**
     * R文件寄存器
     */
    R(UnitType.WORD, "R*", 0xAF, Radix.DECIMAL, 32768),

    /**
     * S步进继电器
     */
    S(UnitType.BIT, "S*", 0x98, Radix.DECIMAL, 2028),

    /**
     * 变址寄存器
     */
    Z(UnitType.WORD, "Z*", 0xCC, Radix.DECIMAL, 10),

    /**
     * 定时器的值
     */
    T(UnitType.WORD, "TN", 0xC2, Radix.DECIMAL, 512),

    /**
     * 计数器的值
     */
    C(UnitType.WORD, "CN", 0xC5, Radix.DECIMAL, 512);

    Device(UnitType type, String asciiCode, int binaryCode, Radix radix, int length) {
        this.type = type;
        this.asciiCode = asciiCode;
        this.binaryCode = binaryCode;
        this.radix = radix;
        this.length = length;
    }

    private UnitType type;

    private String asciiCode;

    private int binaryCode;

    private Radix radix;

    private int length;

    private Object data;

    public UnitType getType() {
        return type;
    }

    public String getAsciiCode() {
        return asciiCode;
    }

    public int getBinaryCode() {
        return binaryCode;
    }

    public Radix getRadix() {
        return radix;
    }

    public int getLength() {
        return length;
    }

    public Object getData() {
        if (data == null) {
            if (type == UnitType.BIT) {
                data = new byte[length];
            } else {
                data = new int[length];
            }
        }
        return data;
    }

    public static Device from(String name) {
        for (Device device : Device.values()) {
            if (device.toString().toLowerCase().equalsIgnoreCase(name)) {
                return device;
            }
        }
        return null;
    }

    public static Device fromAsciiCode(String asciiCode) {
        for (Device device : Device.values()) {
            if (device.getAsciiCode().equals(asciiCode)) {
                return device;
            }
        }
        return null;
    }

    public static Device fromBinaryCode(int binaryCode) {
        for (Device device : Device.values()) {
            if (device.getBinaryCode() == binaryCode) {
                return device;
            }
        }
        return null;
    }
}
