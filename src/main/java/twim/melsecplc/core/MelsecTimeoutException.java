package twim.melsecplc.core;

import java.time.Duration;

/**
 * @author liumin
 */
public class MelsecTimeoutException extends Exception {

    private final long durationMillis;

    public MelsecTimeoutException(Duration duration) {
        this(duration.toMillis());
    }

    public MelsecTimeoutException(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    @Override
    public String getMessage() {
        return String.format("request timed out after %sms milliseconds.", durationMillis);
    }

}