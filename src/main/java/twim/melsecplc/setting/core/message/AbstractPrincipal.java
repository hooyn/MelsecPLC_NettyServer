package twim.melsecplc.setting.core.message;

import io.netty.buffer.ByteBuf;

/**
 * @author liumin
 */
public abstract class AbstractPrincipal implements Principal {

    private String address;

    private int points;

    private Device device;

    private Function function;

    private int subcommand;

    private ByteBuf data;

    private String realAddress;

    public AbstractPrincipal() {
    }

    public AbstractPrincipal(Function function, String address, int points) {
        this.function = function;
        this.address = address;
        this.points = points;
    }

    public AbstractPrincipal(Function function, String address, int points, ByteBuf data) {
        this.function = function;
        this.address = address;
        this.points = points;
        this.data = data;
    }

    @Override
    public void encode(ByteBuf buf) {
        String deviceName = address.substring(0, 1);
        device = Device.from(deviceName);
        if (device == null) {
            throw new IllegalArgumentException("The device `" + deviceName + "` dose not exist");
        }
        // 根据软元件的的单位类型来直接指定子指令
        if (getDevice().getType() == UnitType.BIT) {
            subcommand = 0x0001;
        } else {
            subcommand = 0x0000;
        }
        realAddress = address.substring(1);
        encode0(buf);
    }

    /**
     * 编码函数
     *
     * @param buf 编码到ByteBuf
     */
    protected abstract void encode0(ByteBuf buf);

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public void setFunction(Function function) {
        this.function = function;
    }

    @Override
    public void setData(ByteBuf data) {
        this.data = data;
    }

    @Override
    public Function getFunction() {
        return function;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public Device getDevice() {
        return device;
    }

    @Override
    public ByteBuf getData() {
        return data;
    }

    @Override
    public String getRealAddress() {
        return realAddress;
    }

    @Override
    public void setRealAddress(String realAddress) {
        this.realAddress = realAddress;
    }

    @Override
    public int getSubcommand() {
        return subcommand;
    }

    @Override
    public void setSubcommand(int subcommand) {
        this.subcommand = subcommand;
    }
}
