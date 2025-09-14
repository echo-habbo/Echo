package net.h4bbo.echo.api.network.codecs;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ProtocolCodec {
    private static final Charset CHARSET = StandardCharsets.ISO_8859_1;

    public static Charset getEncoding() {
        return CHARSET;
    }
}
