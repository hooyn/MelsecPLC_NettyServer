package twim.melsecplc.setting.core.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import twim.melsecplc.setting.core.UnknownDeviceException;
import twim.melsecplc.setting.core.utils.BinaryConverters;
import twim.melsecplc.setting.core.utils.ByteBufUtilities;

/**
 * @author liumin
 */
public class BinaryPrincipal extends AbstractPrincipal {

    public BinaryPrincipal() {
    }

    public BinaryPrincipal(Function function, String address, int points) {
        super(function, address, points);
    }

    public BinaryPrincipal(Function function, String address, int points, ByteBuf data) {
        super(function, address, points, data);
    }

    @Override
    public void encode0(ByteBuf buf) {
        buf.writeShortLE(getFunction().getCommand());
        buf.writeShortLE(getSubcommand());
        int addr = Integer.parseUnsignedInt(getRealAddress(), getDevice().getRadix().getValue());
        buf.writeMediumLE(addr);
        buf.writeByte(getDevice().getBinaryCode());
        buf.writeShortLE(getPoints());
        if (getData() != null) {
            if (getDevice().getType() == UnitType.BIT) {
                byte[] bytes = ByteBufUtilities.readAllBytes(getData());
                if (bytes != null) {
                    buf.writeBytes(BinaryConverters.convertBoolArrayToBinaryOnBit(bytes));
                }
            } else {
                ByteBufUtilities.swapBEToLE(buf, getData());
            }
        }
    }

    @Override
    public boolean decode(ByteBuf buf) throws Exception {
        setFunction(Function.from(buf.readUnsignedShortLE()));
        setSubcommand(buf.readUnsignedShortLE());
        int addr = buf.readUnsignedMediumLE();
        int binaryCode = buf.readUnsignedByte();
        Device device = Device.fromBinaryCode(binaryCode);
        if (device == null) {
            throw new UnknownDeviceException(binaryCode);
        }
        setDevice(device);
        if (getDevice().getRadix() == Radix.HEXADECIMAL) {
            setRealAddress(Integer.toHexString(addr));
        } else {
            setRealAddress("" + addr);
        }
        int points = buf.readUnsignedShortLE();
        setPoints(points);

        int remaining = buf.readableBytes();
        if (remaining > 0) {
            if (device.getType() == UnitType.BIT) {
                byte[] bytes = ByteBufUtilities.readAllBytes(buf);
                setData(Unpooled.wrappedBuffer(BinaryConverters.convertBinaryOnBitToBoolArray(bytes, points)));
            } else {
                ByteBuf data = Unpooled.buffer(remaining);
                ByteBufUtilities.swapLEToBE(data, buf);
                setData(data);
            }
        }

        setAddress(device.toString() + addr);
        return true;
    }

    @Override
    public String toString() {
        return "AsciiPrincipal{" +
            "address='" + getAddress() + '\'' +
            ", points=" + getPoints() +
            ", function=" + getFunction() +
            ", subcommand=" + getSubcommand() +
            ", data=" + (getData() != null ? ByteBufUtil.hexDump(getData()) : null) +
            '}';
    }
}
