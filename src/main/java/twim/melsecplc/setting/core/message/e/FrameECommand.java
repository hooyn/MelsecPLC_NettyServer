package twim.melsecplc.setting.core.message.e;

import twim.melsecplc.setting.core.message.Principal;

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
