package com.sun.sylvanas.security.md;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * Message Digest API案例.
 * <p>
 * Created by sylvanasp on 2016/9/26.
 */
public class MDAPIDemo {

    private static final String MD_STR = "Hello Message Digest";

    public static void main(String[] args) {
        jdkMD2();
        jdkMD5();
        providerMD4();
        bcMD5();
        ccMD5();
    }

    /**
     * 使用JDK API实现MD2加密
     */
    public static void jdkMD2() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD2");
            byte[] MD2Bytes = messageDigest.digest(MD_STR.getBytes());
            // 转换为16进制并输出
            System.out.println("JDK MD2: " + Hex.encodeHexString(MD2Bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用JDK API实现MD5加密
     */
    public static void jdkMD5() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] MD5Bytes = messageDigest.digest(MD_STR.getBytes());
            // 转换为16进制并输出
            System.out.println("JDK MD5: " + Hex.encodeHexString(MD5Bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过支持第三方包来实现MD4,JDK本身实现中没有MD4
     */
    public static void providerMD4() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            MessageDigest messageDigest = MessageDigest.getInstance("MD4");
            byte[] MD4Bytes = messageDigest.digest(MD_STR.getBytes());
            // 转换为16进制并输出
            System.out.println("BC MD4: " + org.bouncycastle.util.encoders.Hex.toHexString(MD4Bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用BouncyCastle API实现MD5
     */
    public static void bcMD5() {
        Digest digest = new MD5Digest();
        digest.update(MD_STR.getBytes(), 0, MD_STR.length());
        byte[] MD5Bytes = new byte[digest.getDigestSize()];
        digest.doFinal(MD5Bytes, 0);
        // 转换为16进制并输出
        System.out.println("BC MD5: " + org.bouncycastle.util.encoders.Hex.toHexString(MD5Bytes));
    }

    /**
     * 使用Commons Codes API实现MD5
     */
    public static void ccMD5() {
        String md5Str = DigestUtils.md5Hex(MD_STR);
        System.out.println("CC MD5: " + md5Str);
    }

}
