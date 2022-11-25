package twim.melsecplc.setting.core;

/**
 * @author liumin
 */
public class MelsecClientOptions {

    private int networkNo;

    private int pcNo;

    private int requestDestinationModuleIoNo;

    private int requestDestinationModuleStationNo;

    public MelsecClientOptions(int networkNo, int pcNo, int requestDestinationModuleIoNo, int requestDestinationModuleStationNo) {
        this.networkNo = networkNo;
        this.pcNo = pcNo;
        this.requestDestinationModuleIoNo = requestDestinationModuleIoNo;
        this.requestDestinationModuleStationNo = requestDestinationModuleStationNo;
    }

    public int getNetworkNo() {
        return networkNo;
    }

    public void setNetworkNo(int networkNo) {
        this.networkNo = networkNo;
    }

    public int getPcNo() {
        return pcNo;
    }

    public void setPcNo(int pcNo) {
        this.pcNo = pcNo;
    }

    public int getRequestDestinationModuleIoNo() {
        return requestDestinationModuleIoNo;
    }

    public void setRequestDestinationModuleIoNo(int requestDestinationModuleIoNo) {
        this.requestDestinationModuleIoNo = requestDestinationModuleIoNo;
    }

    public int getRequestDestinationModuleStationNo() {
        return requestDestinationModuleStationNo;
    }

    public void setRequestDestinationModuleStationNo(int requestDestinationModuleStationNo) {
        this.requestDestinationModuleStationNo = requestDestinationModuleStationNo;
    }
}
