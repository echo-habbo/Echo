package net.h4bbo.echo.plugin.handshake.encryption;

import net.h4bbo.echo.api.network.session.IConnectionSession;

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

    public boolean isEncryptionReady() {
        return encryptionReady;
    }

    public void setEncryptionReady(boolean encryptionReady) {
        this.encryptionReady = encryptionReady;
    }
}
