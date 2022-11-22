package twim.melsecplc.core.message.e;

import twim.melsecplc.core.message.Principal;

/**
 * @author liumin
 */
public interface FrameECommand extends FrameEMessage {

    /**
     * 获取主体
     *
     * @return 主体
     */
    Principal getPrincipal();
}
