package com.vbuser.database.operate;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ToTP {
    private static final String ALGORITHM = "HmacSHA1";
    private static final int TIME_STEP = 30;
    private static final int CODE_DIGITS = 6;
    private static final int SECRET_SIZE = 20;

    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SECRET_SIZE];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeAsString(bytes);
    }

    public static String generateTOTP(String secretKey) {
        long currentTime = System.currentTimeMillis() / 1000L;
        long timeStep = currentTime / TIME_STEP;
        return generateTOTP(secretKey, timeStep);
    }

    private static String generateTOTP(String secretKey, long timeStep) {
        try {
            Base32 base32 = new Base32();
            byte[] key = base32.decode(secretKey);
            byte[] data = longToByteArray(timeStep);

            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(key, ALGORITHM));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xf;
            int binary =
                    ((hash[offset] & 0x7f) << 24) |
                            ((hash[offset + 1] & 0xff) << 16) |
                            ((hash[offset + 2] & 0xff) << 8) |
                            (hash[offset + 3] & 0xff);

            int otp = binary % (int) Math.pow(10, CODE_DIGITS);
            return String.format("%0" + CODE_DIGITS + "d", otp);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating TOTP", e);
        }
    }

    public static boolean validateTOTP(String secretKey, String code) {
        String currentCode = generateTOTP(secretKey);
        return currentCode.equals(code);
    }

    private static byte[] longToByteArray(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }
}
