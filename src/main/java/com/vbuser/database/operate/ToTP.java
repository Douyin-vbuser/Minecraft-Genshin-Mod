//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:DeepSeek
//AI注释服务提供商:DeepSeek

package com.vbuser.database.operate;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 基于时间的一次性密码(TOTP)实现<br>
 * 提供网页端操作确权功能
 */
public class ToTP {
    private static final String ALGORITHM = "HmacSHA1"; // HMAC算法
    private static final int TIME_STEP = 30; // 时间步长(秒)
    private static final int CODE_DIGITS = 6; // 验证码位数
    private static final int SECRET_SIZE = 20; // 密钥大小

    /**
     * 生成随机密钥
     * @return Base32编码的密钥
     */
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SECRET_SIZE];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeAsString(bytes);
    }

    /**
     * 生成当前时间的一次性密码
     * @param secretKey 密钥
     * @return 6位数字验证码
     */
    public static String generateTOTP(String secretKey) {
        long currentTime = System.currentTimeMillis() / 1000L;
        long timeStep = currentTime / TIME_STEP;
        return generateTOTP(secretKey, timeStep);
    }

    /**
     * 生成指定时间步长的一次性密码
     * @param secretKey 密钥
     * @param timeStep 时间步长
     * @return 6位数字验证码
     */
    private static String generateTOTP(String secretKey, long timeStep) {
        try {
            // 解码Base32密钥
            Base32 base32 = new Base32();
            byte[] key = base32.decode(secretKey);
            byte[] data = longToByteArray(timeStep);

            // 计算HMAC-SHA1
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(key, ALGORITHM));
            byte[] hash = mac.doFinal(data);

            // 动态截取偏移量
            int offset = hash[hash.length - 1] & 0xf;
            int binary =
                    ((hash[offset] & 0x7f) << 24) |
                            ((hash[offset + 1] & 0xff) << 16) |
                            ((hash[offset + 2] & 0xff) << 8) |
                            (hash[offset + 3] & 0xff);

            // 生成6位数字验证码
            int otp = binary % (int) Math.pow(10, CODE_DIGITS);
            return String.format("%0" + CODE_DIGITS + "d", otp);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating TOTP", e);
        }
    }

    /**
     * 验证TOTP码
     * @param secretKey 密钥
     * @param code 待验证的代码
     * @return 验证是否成功
     */
    public static boolean validateTOTP(String secretKey, String code) {
        String currentCode = generateTOTP(secretKey);
        return currentCode.equals(code);
    }

    /**
     * 长整型转字节数组
     * @param l 长整型值
     * @return 8字节数组
     */
    private static byte[] longToByteArray(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }
}