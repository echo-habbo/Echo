package net.h4bbo.echo.plugin.handshake.encryption;

import java.security.SecureRandom;
import java.util.Random;

public class KeyGenerator {
    private static final Random random;

    static {
        random = new SecureRandom();
    }
    /**
     * Generates a totally random public key with a random length for RC4 ciphering,
     * containing a-z and 0-9 and returns it as a string.
     */
    public static String generateKey() {
        int keyLength = 60 + random.nextInt(6); // random between 60 and 65 (inclusive)
        StringBuilder sb = new StringBuilder(keyLength);

        for (int i = 0; i < keyLength; i++) {
            int j;
            if (random.nextInt(2) == 1) {
                j = 97 + random.nextInt(26); // a-z
            } else {
                j = 48 + random.nextInt(10); // 0-9
            }
            sb.append((char) j);
        }

        return sb.toString();
    }
}
