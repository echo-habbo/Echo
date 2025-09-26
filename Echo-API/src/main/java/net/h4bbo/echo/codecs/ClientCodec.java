package net.h4bbo.echo.codecs;

import io.netty.buffer.ByteBuf;
import net.h4bbo.echo.common.util.specialised.WireEncoding;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.network.codecs.ProtocolCodec;
import net.h4bbo.echo.common.util.specialised.Base64Encoding;

public class ClientCodec implements IClientCodec, AutoCloseable {
    private String header;
    private int headerId;

    private ByteBuf bufferReader;
    private final ByteBuf buffer;

    public String getMessageBody() {
        String consoleText = bufferReader.toString(ProtocolCodec.getEncoding());
        for (int i = 0; i < 13; i++) {
            consoleText = consoleText.replace(String.valueOf((char)i), "[" + i + "]");
        }
        return consoleText;
    }

    public byte[] getReadableBytes() {
        bufferReader.markReaderIndex();
        byte[] bytes = new byte[bufferReader.readableBytes()];
        bufferReader.readBytes(bytes);
        bufferReader.resetReaderIndex();
        return bytes;
    }

    public String getContent() {
        return new String(getReadableBytes(), ProtocolCodec.getEncoding());
    }

    public ClientCodec(ByteBuf buffer) {
        this.bufferReader = buffer;
        this.buffer = buffer.copy();
        this.header = new String(new byte[]{buffer.readByte(), buffer.readByte()}, ProtocolCodec.getEncoding());
        this.headerId = Base64Encoding.decodeInt32(this.header.getBytes());
    }

    @Override
    public Object get(DataCodec codec) {
        switch (codec) {
            case DataCodec.VL64_INT:
                return getInt();
            case DataCodec.BASE64_INT:
                return getBase64Int();
            case DataCodec.STRING:
                return getString();
            case DataCodec.BOOL:
                return getBool();
            case DataCodec.BYTES:
                return getReadableBytes();
            default:
                throw new IllegalArgumentException("Unsupported codec: " + codec);
        }
    }

    @Override
    public <T> T pop(DataCodec codec, Class<T> type) {
        Object value = get(codec);
        if (type == String.class) {
            return type.cast(String.valueOf(value));
        } else if (type == Integer.class) {
            if (value instanceof Number) {
                return type.cast(((Number) value).intValue());
            } else {
                return type.cast(Integer.parseInt(value.toString()));
            }
        } else if (type == Double.class) {
            if (value instanceof Number) {
                return type.cast(((Number) value).doubleValue());
            } else {
                return type.cast(Double.parseDouble(value.toString()));
            }
        } else if (type == Boolean.class) {
            if (value instanceof Boolean) {
                return type.cast(value);
            } else {
                return type.cast(Boolean.parseBoolean(value.toString()));
            }
        } else if (type == Long.class) {
            if (value instanceof Number) {
                return type.cast(((Number) value).longValue());
            } else {
                return type.cast(Long.parseLong(value.toString()));
            }
        } else if (type == Float.class) {
            if (value instanceof Number) {
                return type.cast(((Number) value).floatValue());
            } else {
                return type.cast(Float.parseFloat(value.toString()));
            }
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    private int getInt() {
        try {
            if (getReadableBytes().length == 0)
                return 0;
            byte[] bzData = this.getReadableBytes();
            int totalBytes = WireEncoding.decodeInt32(bzData);
            readBytesInternal(totalBytes);
            return Integer.parseInt(new String(bzData));
        } catch (Exception e) {
            return 0;
        }
    }

    private int getBase64Int() {
        try {
            if (getReadableBytes().length < 2)
                return 0;
            byte[] data = readBytesInternal(2);
            String base64String = new String(data);
            return Base64Encoding.decodeInt32(base64String.getBytes());
        } catch (Exception e) {
            return 0;
        }
    }

    private String getString() {
        try {
            if (getReadableBytes().length < 2)
                return "";
            int totalBytes = Base64Encoding.decodeInt32(readBytesInternal(2));
            if (getReadableBytes().length < totalBytes)
                return "";
            byte[] data = readBytesInternal(totalBytes);
            String value = new String(data, ProtocolCodec.getEncoding());
            return value;
        } catch (Exception e) {
            return "";
        }
    }

    private boolean getBool() {
        try {
            return bufferReader.readByte() == 'I';
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] getBytes(int len) {
        try {
            byte[] payload = new byte[len];
            bufferReader.readBytes(payload);
            return payload;
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private byte[] readBytesInternal(int len) {
        try {
            byte[] payload = new byte[len];
            bufferReader.readBytes(payload);
            return payload;
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public String getHeader() {
        return header;
    }

    public int getHeaderId() {
        return headerId;
    }

    public ByteBuf getBufferReader() {
        return bufferReader;
    }

    public void close() {
        bufferReader.release();
        buffer.release();
    }

    @Override
    public IClientCodec copy() {
        return new ClientCodec(this.buffer.copy());
    }
}
