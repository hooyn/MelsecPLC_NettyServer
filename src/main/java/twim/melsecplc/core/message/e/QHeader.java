package twim.melsecplc.core.message.e;


import twim.melsecplc.core.message.BinaryCodec;

/**
 * @author liumin
 */
public interface QHeader extends BinaryCodec {

    /**
     * 获取网络编号
     *
     * @return 网络编号
     */
    int getNetworkNo();

    /**
     * 设置网络编号
     *
     * @param networkNo 网络编号
     */
    void setNetworkNo(int networkNo);

    /**
     * 获取可编程控制器编号
     *
     * @return 可编程控制器编号
     */
    int getPcNo();

    /**
     * 设置可编程控制器编号
     *
     * @param PcNo 可编程控制器编号
     */
    void setPcNo(int PcNo);

    /**
     * 获取请求目标模块I/O编号
     *
     * @return 请求目标模块I/O编号
     */
    int getRequestDestinationModuleIoNo();

    /**
     * 设置请求目标模块I/O编号
     *
     * @param requestDestinationModuleIoNo 请求目标模块I/O编号
     */
    void setRequestDestinationModuleIoNo(int requestDestinationModuleIoNo);

    /**
     * 获取请求目标模块站号
     *
     * @return 请求目标模块站号
     */
    int getRequestDestinationModuleStationNo();

    /**
     * 设置请求目标模块站号
     *
     * @param requestDestinationModuleStationNo 请求目标模块站号
     */
    void setRequestDestinationModuleStationNo(int requestDestinationModuleStationNo);

    /**
     * 从另一个Q Header拷贝
     *
     * @param other Q Header
     */
    void copy(QHeader other);
}
