package com.sun.sylvanas.security.sha;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * Secure Hash Algorithm API案例.
 * <p>
 * Created by sylvanasp on 2016/9/26.
 */
public class SHAAPIDemo {

    private static final String SHA_STR = "Hello Secure Hash Algorithm";

    public static void main(String[] args) {
        jdkSHA1();
        bcSHA1();
        bcSHA224();
        providerSHA224();
        ccSHA1();
    }

    /**
     * 使用JDK API实现SHA1加密
     */
    public static void jdkSHA1() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(SHA_STR.getBytes());
            // 转换为16进制并输出
            System.out.println("JDK SHA-1: " + Hex.encodeHexString(messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用BouncyCastle API实现SHA1加密
     */
    public static void bcSHA1() {
        Digest digest = new SHA1Digest();
        digest.update(SHA_STR.getBytes(), 0, SHA_STR.length());
        byte[] SHA1Bytes = new byte[digest.getDigestSize()];
        digest.doFinal(SHA1Bytes, 0);
        // 转换为16进制并输出
        System.out.println("BC SHA-1: " + org.bouncycastle.util.encoders.Hex.toHexString(SHA1Bytes));
    }

    /**
     * 使用BouncyCastle API实现SHA224加密,JDK本身没有实现SHA224
     */
    public static void bcSHA224() {
        Digest digest = new SHA224Digest();
        digest.update(SHA_STR.getBytes(), 0, SHA_STR.length());
        byte[] SHA224Bytes = new byte[digest.getDigestSize()];
        digest.doFinal(SHA224Bytes, 0);
        // 转换为16进制并输出
        System.out.println("BC SHA-224: " + org.bouncycastle.util.encoders.Hex.toHexString(SHA224Bytes));
    }

    /**
     * 通过支持第三方包来实现SHA224
     */
    public static void providerSHA224() {
        Security.addProvider(new BouncyCastleProvider());

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA224");
            messageDigest.update(SHA_STR.getBytes());
            // 转换为16进制并输出
            System.out.println("PROVIDER SHA-224: "
                    + org.bouncycastle.util.encoders.Hex.toHexString(messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    /**
     * 使用Commons Codec API实现SHA1加密
     */
    public static void ccSHA1() {
        System.out.println("CC SHA-1: " + DigestUtils.sha1Hex(SHA_STR));
    }

}
