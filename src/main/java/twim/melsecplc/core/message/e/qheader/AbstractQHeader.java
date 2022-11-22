package twim.melsecplc.core.message.e.qheader;

import twim.melsecplc.core.message.e.QHeader;

/**
 * @author liumin
 */
public abstract class AbstractQHeader implements QHeader {

    private int networkNo = 0x00;

    private int pcNo = 0xFF;

    private int requestDestinationModuleIoNo = 0x03FF;

    private int requestDestinationModuleStationNo = 0x00;

    public AbstractQHeader() {
    }

    public AbstractQHeader(int networkNo, int pcNo, int requestDestinationModuleIoNo,
                           int requestDestinationModuleStationNo) {
        this.networkNo = networkNo;
        this.pcNo = pcNo;
        this.requestDestinationModuleIoNo = requestDestinationModuleIoNo;
        this.requestDestinationModuleStationNo = requestDestinationModuleStationNo;
    }

    @Override
    public int getNetworkNo() {
        return networkNo;
    }

    @Override
    public void setNetworkNo(int networkNo) {
        this.networkNo = networkNo;
    }

    @Override
    public int getPcNo() {
        return pcNo;
    }

    @Override
    public void setPcNo(int pcNo) {
        this.pcNo = pcNo;
    }

    @Override
    public int getRequestDestinationModuleIoNo() {
        return requestDestinationModuleIoNo;
    }

    @Override
    public void setRequestDestinationModuleIoNo(int requestDestinationModuleIoNo) {
        this.requestDestinationModuleIoNo = requestDestinationModuleIoNo;
    }

    @Override
    public int getRequestDestinationModuleStationNo() {
        return requestDestinationModuleStationNo;
    }

    @Override
    public void setRequestDestinationModuleStationNo(int requestDestinationModuleStationNo) {
        this.requestDestinationModuleStationNo = requestDestinationModuleStationNo;
    }

    @Override
    public void copy(QHeader other) {
        this.networkNo = other.getNetworkNo();
        this.pcNo = other.getPcNo();
        this.requestDestinationModuleIoNo = other.getRequestDestinationModuleIoNo();
        this.requestDestinationModuleStationNo = other.getRequestDestinationModuleStationNo();
    }
}
