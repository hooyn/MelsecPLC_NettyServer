package twim.melsecplc.setting.core.message.e;

import io.netty.buffer.ByteBuf;
import twim.melsecplc.setting.core.MelsecClientOptions;
import twim.melsecplc.setting.core.message.Function;
import twim.melsecplc.setting.core.message.Principal;

/**
 * @author liumin
 */
public abstract class AbstractFrameECommand implements FrameECommand {

    private Function function;

    private String address;

    private int points;

    private ByteBuf data;

    private Principal principal;

    private Subheader subheader;

    private QHeader qHeader;

    public AbstractFrameECommand() {
    }

    public AbstractFrameECommand(Function function, String address, int points) {
        this.function = function;
        this.address = address;
        this.points = points;
    }

    public AbstractFrameECommand(Function function, String address, int points, MelsecClientOptions options) {
        this.function = function;
        this.address = address;
        this.points = points;
        initQHeader(options);
    }

    public AbstractFrameECommand(Function function, String address, int points, ByteBuf data) {
        this.function = function;
        this.address = address;
        this.points = points;
        this.data = data;
    }

    public AbstractFrameECommand(Function function, String address, int points, ByteBuf data, MelsecClientOptions options) {
        this.function = function;
        this.address = address;
        this.points = points;
        this.data = data;
        initQHeader(options);
    }

    private void initQHeader(MelsecClientOptions options) {
        this.getQHeader().setNetworkNo(options.getNetworkNo());
        this.getQHeader().setPcNo(options.getPcNo());
        this.getQHeader().setRequestDestinationModuleIoNo(options.getRequestDestinationModuleIoNo());
        this.getQHeader().setRequestDestinationModuleStationNo(options.getRequestDestinationModuleStationNo());
    }

    @Override
    public Principal getPrincipal() {
        if (principal == null) {
            principal = newPrincipal();
        }
        return principal;
    }

    @Override
    public Subheader getSubheader() {
        if (subheader == null) {
            subheader = newSubheader();
        }
        return subheader;
    }

    @Override
    public QHeader getQHeader() {
        if (qHeader == null) {
            qHeader = newQHeader();
        }
        return qHeader;
    }

    /**
     * 新建Command
     *
     * @return Principal
     */
    protected abstract Principal newPrincipal();

    /**
     * 新建Subheader
     *
     * @return Subheader
     */
    protected abstract Subheader newSubheader();

    /**
     * 新建QHeader
     *
     * @return QHeader
     */
    protected abstract QHeader newQHeader();

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
