package net.h4bbo.echo.common.network.codecs;
import net.h4bbo.echo.common.util.specialised.WireEncoding;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IPacketCodec;
import net.h4bbo.echo.api.network.session.IConnectionSend;
import net.h4bbo.echo.common.util.specialised.Base64Encoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

public class PacketCodec implements IPacketCodec {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private boolean composed = false;
    private final int headerId;

    public PacketCodec(int headerId) {
        this.headerId = headerId;
    }

    public int getHeaderId() {
        return headerId;
    }

    public byte[] getBuffer() {
        return buffer.toByteArray();
    }

    public int getLength() {
        return buffer.size();
    }

    public boolean isComposed() {
        return composed;
    }

    public static IPacketCodec create(int headerId) {
        return new PacketCodec(headerId).append(DataCodec.BASE64_INT, headerId);
    }

    @Override
    public IPacketCodec append(DataCodec codec, Object value) {
        if (composed) {
            throw new IllegalStateException("Cannot modify a composed packet");
        }

        try {
            switch (codec) {
                case INT:
                    appendInteger(value);
                    break;
                case BASE64_INT:
                    appendBase64Integer(value);
                    break;
                case VL64_INT:
                    appendVL64Integer(value);
                    break;
                case STRING:
                    appendString(value);
                    break;
                case BOOL:
                    appendBoolean(value);
                    break;
                case BYTES:
                    appendBytes(value);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported codec: " + codec);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to packet buffer", e);
        }

        return this;
    }

    @Override
    public void send(IConnectionSend target) {
        Objects.requireNonNull(target);

        composed = true;

        target.send(this);
    }

    @Override
    public void sendAll(IConnectionSend... targets) {
        Objects.requireNonNull(targets);

        composed = true;

        for (var target : targets) {
            if (target == null) {
                continue;
            }
            try {
                target.send(this);
            } catch (Exception e) {
                throw new RuntimeException("Error sending packet to target", e);
            }
        }
    }

    private void write(byte[] bytes) throws IOException {
        buffer.write(bytes);
    }

    private void appendInteger(Object value) throws IOException {
        int intValue;
        if (value instanceof Number) {
            intValue = ((Number) value).intValue();
        } else {
            intValue = Integer.parseInt(value.toString());
        }
        String s = Integer.toString(intValue);
        write(s.getBytes(getProtocolEncoding()));
    }

    private void appendBase64Integer(Object value) throws IOException {
        int intValue = (Integer) value;
        byte[] encodedBytes = Base64Encoding.encodeInt32(intValue, 2);
        write(encodedBytes);
    }

    private void appendVL64Integer(Object value) throws IOException {
        int intValue = (Integer) value;
        byte[] encodedBytes = WireEncoding.encodeInt32(intValue);
        write(encodedBytes);
    }

    private void appendString(Object value) throws IOException {
        String s = value != null ? value.toString() : "";
        write(s.getBytes(getProtocolEncoding()));
        write(new byte[] { (byte) 2 });
    }

    private void appendBoolean(Object value) throws IOException {
        boolean boolValue = (Boolean) value;
        write(new byte[] { boolValue ? (byte) 'I' : (byte) 'H' });
    }

    private void appendBytes(Object value) throws IOException {
        if (value instanceof String) {
            String str = (String) value;
            write(str.getBytes(getProtocolEncoding()));
        } else if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            write(bytes);
        } else {
            throw new IllegalArgumentException("Value must be either String or byte array for BYTES codec");
        }
    }

    public static Charset getProtocolEncoding() {
        return java.nio.charset.StandardCharsets.UTF_8;
    }
}
