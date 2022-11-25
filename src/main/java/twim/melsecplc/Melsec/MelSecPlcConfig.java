package twim.melsecplc.melsec;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MelSecPlcConfig {

    private String address;
    @Builder.Default
    private int port = 5002;

    @Builder.Default
    private int networkNo = 0x01;
    @Builder.Default
    private int pcNo = 0x01;
    @Builder.Default
    private int requestDestinationModuleIoNo = 0x03FF;
    @Builder.Default
    private int requestDestinationModuleStationNo = 0x00;

    public MelSecPlcConfig(String address, int port, int networkNo, int pcNo, int requestDestinationModuleIoNo,
                           int requestDestinationModuleStationNo){
        this.address = address;
        this.port = port;
        this.networkNo = networkNo;
        this.pcNo = pcNo;
        this.requestDestinationModuleIoNo = requestDestinationModuleIoNo;
        this.requestDestinationModuleStationNo = requestDestinationModuleStationNo;
    }
}