package net.h4bbo.echo.server.plugin.example;

import net.h4bbo.echo.api.network.session.IConnectionSession;
import net.h4bbo.echo.server.network.session.ConnectionSession;

import java.security.SecureRandom;
import java.util.Random;

public class RC4Holder {
    public final RC4 rc4;
    public final IConnectionSession connectionSession;
    public final String key;
    private boolean encryptionReady;

    public RC4Holder(IConnectionSession connectionSession) {
        this.key = KeyGenerator.generateKey();
        this.rc4 = new RC4(this.key);
        this.connectionSession = connectionSession;
        this.encryptionReady = false;
    }

    public RC4 getRc4() {
        return rc4;
    }

    public IConnectionSession getConnectionSession() {
        return connectionSession;
    }

    public boolean isEncryptionReady() {
        return encryptionReady;
    }

    public void setEncryptionReady(boolean encryptionReady) {
        this.encryptionReady = encryptionReady;
    }
}
