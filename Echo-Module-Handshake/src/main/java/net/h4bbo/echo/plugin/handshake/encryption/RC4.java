package net.h4bbo.echo.plugin.handshake.encryption;

import net.h4bbo.echo.api.network.codecs.ProtocolCodec;

import java.util.Objects;

/**
 * Provides data ciphering with RC4.
 *
 * Assumes RC4Core supplies:
 * - protected int[] keyWindow;
 * - protected String premixString;
 * - protected String decodeKey(String publicKey);
 * (and any other helpers you originally had in the C# base class).
 */
public class RC4 {
    protected final String[] DI = {
            "0","1","2","3","4","5","6","7","8","9",
            "A","B","C","D","E","F"
    };

    // The original large integer table from the C# rc4Core
    protected final int[] keyWindow = {
            204, 53, 74, 109, 63, 4, 163, 182, 210, 186, 19, 162, 160, 115, 139, 83, 235, 177, 14, 15, 11, 127, 4, 210, 222, 138, 10, 138, 151, 236, 158, 186, 67, 1, 168, 69, 139, 214, 243, 32, 157, 161, 211, 155, 20, 192, 214, 155, 12, 153, 192, 112, 98, 146, 33, 30, 22, 131, 81, 161, 105, 142, 103, 204, 112, 9, 167, 185, 176, 51, 27, 166, 249, 228, 24, 165, 197, 25, 166, 216, 74, 14, 104, 15, 77, 49, 6, 50, 65, 126, 10, 187, 15, 17, 189, 155, 246, 221, 92, 104, 79, 87, 186, 88, 80, 50, 223, 126, 148, 217, 81, 223, 91, 70, 165, 237, 150, 95, 195, 205, 199, 176, 156, 122, 187, 232, 252, 230, 169, 94, 157, 194, 44, 164, 208, 22, 141, 139, 167, 236, 201, 42, 130, 14, 44, 57, 253, 224, 130, 118, 242, 226, 146, 202, 154, 40, 201, 171, 160, 91, 143, 144, 150, 197, 169, 204, 121, 131, 139, 112, 214, 196, 74, 123, 159, 220, 77, 176, 151, 73, 125, 135, 166, 26, 176, 31, 255, 234, 91, 30, 218, 41, 121, 17, 45, 3, 234, 35, 185, 52, 112, 108, 65, 72, 184, 93, 225, 113, 62, 0, 110, 38, 43, 15, 44, 114, 162, 167, 69, 40, 103, 144, 114, 215, 228, 47, 112, 235, 179, 211, 116, 237, 70, 167, 36, 224, 183, 11, 0, 74, 145, 241, 153, 40, 151, 211, 231, 199, 235, 176, 109, 95, 160, 141, 137, 236, 39, 17, 246, 97, 120, 227, 12, 1, 195, 239, 150, 169, 85, 226, 23, 58, 145, 157, 37, 218, 132, 168, 94, 15, 240, 24, 152, 230, 249, 80, 145, 208, 209, 144, 154, 228, 197, 40, 6, 248, 90, 15, 1, 82, 145, 77, 220, 27, 167, 0, 149, 0, 103, 53, 226, 242, 175, 9, 177, 130, 65, 216, 107, 4, 194, 71, 135, 231, 151, 178, 188, 220, 33, 152, 120, 165, 73, 124, 32, 215, 127, 130, 29, 40, 20, 3, 212, 254, 106, 42, 98, 7, 8, 129, 195, 30, 74, 118, 169, 81, 88, 235, 149, 232, 181, 182, 206, 82, 163, 26, 116, 37, 41, 50, 63, 185, 165, 2, 81, 10, 149, 103, 211, 168, 34, 55, 32, 233, 16, 238, 219, 235, 170, 255, 244, 12, 89, 211, 88, 33, 24, 38, 190, 75, 70, 86, 89, 2, 189, 134, 207, 65, 6, 148, 124, 22, 57, 21, 118, 227, 173, 21, 236, 236, 139, 189, 230, 153, 153, 182, 230, 216, 26, 0, 9, 50, 32, 189, 97, 3, 208, 201, 103, 163, 96, 0, 42, 11, 173, 98, 102, 76, 31, 243, 59, 71, 223, 252, 186, 157, 231, 90, 212, 83, 10, 69, 69, 165, 209, 112, 157, 237, 24, 90, 4, 44, 247, 32, 159, 126, 171, 99, 216, 196, 228, 217, 157, 143, 32, 16, 111, 67, 106, 231, 10, 167, 13, 240, 182, 105, 52, 12, 84, 91, 243, 205, 180, 180, 35, 58, 238, 240, 0, 209, 48, 249, 243, 209, 93, 10, 22, 183, 5, 177, 110, 16, 188, 201, 240, 194, 11, 76, 219, 67, 254, 176, 139, 66, 81, 138, 109, 178, 71, 143, 74, 217, 52, 0, 127, 190, 12, 214, 231, 84, 239, 165, 155, 89, 95, 106, 62, 30, 182, 137, 85, 39, 221, 51, 188, 149, 104, 167, 71, 11, 220, 212, 246, 114, 10, 4, 216, 127, 233, 231, 178, 174, 181, 29, 49, 118, 177, 108, 156, 174, 118, 196, 216, 106, 203, 96, 65, 12, 140, 248, 152, 35, 152, 17, 89, 136, 138, 94, 5, 190, 92, 189, 16, 216, 61, 70, 165, 36, 238, 167, 16, 61, 206, 140, 226, 251, 37, 225, 211, 111, 42, 195, 36, 248, 233, 67, 146, 100, 244, 23, 154, 103, 48, 4, 15, 33, 169, 151, 13, 151, 115, 173, 37, 103, 172, 23, 182, 29, 22, 25, 54, 46, 188, 14, 24, 12, 182, 241, 163, 90, 121, 172, 29, 73, 191, 91, 232, 229, 197, 200, 32, 7, 67, 214, 141, 248, 10, 135, 168, 4, 144, 17, 94, 228, 76, 202, 130, 174, 251, 170, 100, 173, 232, 183, 132, 130, 35, 163, 1, 154, 134, 56, 202, 13, 190, 224, 56, 107, 107, 244, 16, 12, 149, 220, 120, 245, 179, 103, 85, 255, 195, 187, 191, 82, 225, 13, 206, 106, 60, 212, 12, 211, 247, 112, 185, 5, 56, 226, 236, 179, 181, 208, 204, 16, 159, 158, 36, 65, 101, 148, 23, 89, 125, 27, 61, 117, 255, 142, 32, 138, 105, 166, 203, 253, 113, 138, 30, 247, 250, 198, 21, 244, 113, 40, 161, 229, 179, 100, 76, 30, 177, 69, 87, 90, 9, 135, 254, 108, 99, 145, 195, 145, 138, 223, 237, 52, 126, 244, 109, 171, 44, 0, 187, 129, 127, 49, 220, 100, 253, 0, 116, 93, 87, 39, 245, 5, 54, 203, 241, 155, 255, 125, 80, 253, 75, 71, 242, 147, 153, 148, 214, 91, 33, 181, 78, 10, 82, 171, 89, 179, 221, 144, 224, 138, 112, 254, 152, 186, 190, 224, 44, 251, 60, 133, 65, 70, 72, 203, 126, 123, 212, 108, 68, 185, 42, 208, 51, 11, 177, 3, 24, 207, 14, 148, 113, 55, 1, 19, 179, 31, 133, 11, 227, 72, 145, 242, 157, 244, 239, 129, 124, 109, 56, 134, 56, 95, 110, 161, 73, 151, 136, 67, 176, 201, 193, 70, 53, 31, 238, 84, 81, 65, 50, 182, 20, 17, 247, 179, 217, 14, 34, 182, 97, 55, 117, 176, 108, 234, 147, 89, 168, 7, 251, 212, 22, 107, 63, 248, 179, 222, 167, 214, 136, 74, 53, 47, 120, 233, 131, 41, 167, 220, 56, 12, 51, 125, 207, 112, 179, 211, 47, 134, 223, 112, 223, 46, 249, 24, 64, 58, 36, 187, 77, 132, 116, 116, 111, 36, 127, 217, 177, 24, 58, 102, 166, 105, 119, 234, 187, 198, 77, 153, 23, 157, 103, 92, 33, 136, 182, 131, 154, 141, 149, 4, 117, 213, 226, 64, 116, 55, 6, 159, 126, 225
    };

    protected final String premixString = "eb11nmhdwbn733c2xjv1qln3ukpe0hvce0ylr02s12sv96rus2ohexr9cp8rufbmb1mdb732j1l3kehc0l0s2v6u2hx9prfmu";

    private static final int SIZE = 256;

    // Work indices
    private int i;
    private int j;

    private final int[] key = new int[SIZE];
    private final int[] table = new int[SIZE];

    /**
     * Creates a new RC4 cipher instance from a public key.
     * The constructor decodes the provided key, initializes the S-box,
     * and runs a premixing phase to further randomize the state.
     *
     * @param publicKey the encoded public key string
     */
    public RC4(String publicKey) {
        this.i = 0;
        this.j = 0;

        String decodedKey = decodeKey(publicKey);
        initialize(decodedKey);
        premixTable(premixString);
    }

    /**
     * Converts an encoded public key into a numeric checksum string.
     * The algorithm mirrors the C# implementation, combining characters
     * from both halves of the input and applying XOR/shift transformations.
     *
     * @param key composite key string
     * @return numeric checksum as a decimal string
     */
    protected String decodeKey(String key) {
        Objects.requireNonNull(key, "Key must not be null");

        String table = key.substring(0, key.length() / 2);
        String remainder = key.substring(key.length() / 2);
        long checkSum = 0L;

        for (int i = 0; i < table.length(); i++) {
            String single = remainder.substring(i, i + 1);
            int offset = table.indexOf(single);
            if (offset % 2 == 0) offset *= 2;
            if (i % 3 == 0) offset *= 3;
            if (offset < 0) offset = table.length() % 2;
            checkSum += offset;
            checkSum ^= (offset << ((i % 3) * 8));
        }

        return Long.toString(checkSum);
    }

    /**
     * Initializes the key stream and S-box using the decoded key string.
     * Performs RC4 key scheduling (KSA) to prepare the cipher state.
     *
     * @param keyString decoded key string (decimal format)
     */
    private void initialize(String keyString) {
        Objects.requireNonNull(keyString, "Key string must not be null");
        int keyValue = Integer.parseInt(keyString);
        int keyLength = ((keyValue & 0xF8) / 8);
        if (keyLength < 20) keyLength += 20;

        int keyOffset = keyValue % keyWindow.length;
        int tGiven = keyValue;
        int tOwn;

        int[] w = new int[keyLength];

        // Expand key material using keyWindow and bitwise operations
        for (int a = 0; a < keyLength; a++) {
            tOwn = keyWindow[Math.abs((keyOffset + a) % keyWindow.length)];
            w[a] = Math.abs(tGiven ^ tOwn);
            tGiven = (a == 31) ? keyValue : (tGiven / 2);
        }

        // Fill key array and initialize S-box
        for (int b = 0; b < SIZE; b++) {
            key[b] = w[b % w.length];
            table[b] = b;
        }

        // Key-scheduling algorithm (RC4 KSA)
        int t;
        int u = 0;
        for (int a = 0; a < SIZE; a++) {
            u = (u + table[a] + key[a]) % SIZE;
            t = table[a];
            table[a] = table[u];
            table[u] = t;
        }
    }

    /**
     * Performs a premixing phase by repeatedly encrypting a fixed string.
     * This scrambles the S-box before the cipher is used.
     *
     * @param s premix string
     */
    private void premixTable(String s) {
        for (int a = 0; a < 17; a++) {
            encipher(s);
        }
    }

    /**
     * Encrypts a byte array and returns the result as a hex-encoded byte array.
     *
     * @param input plaintext bytes
     * @return encrypted bytes (hex characters)
     */
    public byte[] encipher(byte[] input) {
        var val = encipher(new String(input, ProtocolCodec.getEncoding()));
        return val.getBytes(ProtocolCodec.getEncoding());
    }

    /**
     * Encrypts a string and returns a hex-encoded ciphertext.
     * Each plaintext character is XORed with the RC4 keystream byte.
     *
     * @param input plaintext string
     * @return hex-encoded ciphertext
     */
    public String encipher(String input) {
        if (input == null || input.isEmpty()) return "";

        StringBuilder ret = new StringBuilder(input.length() * 2);

        for (int a = 0; a < input.length(); a++) {
            // RC4 PRGA step
            i = (i + 1) % SIZE;
            j = (j + table[i]) % SIZE;
            int t = table[i];
            table[i] = table[j];
            table[j] = t;

            int k = table[(table[i] + table[j]) % SIZE];
            int c = input.charAt(a) ^ k;
            int byteVal = c & 0xFF;

            // Encode as two uppercase hex digits
            if (byteVal == 0) {
                ret.append("00");
            } else {
                String hex = Integer.toHexString(byteVal).toUpperCase();
                if (hex.length() == 1) ret.append('0');
                ret.append(hex);
            }
        }
        return ret.toString();
    }

    /**
     * Decrypts a hex-encoded RC4 ciphertext back to a byte array.
     * Returns an empty array if the input is invalid.
     *
     * @param input hex-encoded ciphertext
     * @return plaintext bytes
     */
    public byte[] decipher(byte[] input) {
        var val = decipher(new String(input, ProtocolCodec.getEncoding()));
        return val.getBytes(ProtocolCodec.getEncoding());
    }

    /**
     * Decrypts a hex-encoded RC4 ciphertext back to its plaintext string.
     * Returns an empty string if the input is malformed.
     *
     * @param input hex-encoded ciphertext
     * @return plaintext string
     */
    public String decipher(String input) {
        if (input == null || input.length() % 2 != 0) return "";

        try {
            StringBuilder ret = new StringBuilder(input.length() / 2);

            for (int a = 0; a < input.length(); a += 2) {
                // RC4 PRGA step
                i = (i + 1) % SIZE;
                j = (j + table[i]) % SIZE;
                int t = table[i];
                table[i] = table[j];
                table[j] = t;

                int k = table[(table[i] + table[j]) % SIZE];
                int parsed = Integer.parseInt(input.substring(a, a + 2), 16);
                ret.append((char) (parsed ^ k));
            }
            return ret.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
