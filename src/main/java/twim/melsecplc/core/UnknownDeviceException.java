package twim.melsecplc.core;

/**
 * @author liumin
 */
public class UnknownDeviceException extends Exception {

    private String asciiCode;

    private int binaryCode;

    public UnknownDeviceException(String asciiCode) {
        this.asciiCode = asciiCode;
    }

    public UnknownDeviceException(int binaryCode) {
        this.binaryCode = binaryCode;
    }

    @Override
    public String getMessage() {
        if (asciiCode != null) {
            return String.format("Unknown device ascii code: %s", asciiCode);
        } else {
            return String.format("Unknown device binary code: %d", binaryCode);
        }
    }
}
