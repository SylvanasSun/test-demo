package com.sun.sylvanas.security.mac;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Message Authentication Code API案例.
 * MAC也可以称作为HMAC,即含有密钥的散列函数算法.
 * <p>
 * Created by sylvanasp on 2016/9/26.
 */
public class MACAPIDemo {

    private static final String MAC_STR = "Hello Message Authentication Code";

    public static void main(String[] args) {
        jdkHmacMD5();
        bcHmacMD5();
    }

    /**
     * 使用JDK实现HmacMD5
     */
    public static void jdkHmacMD5() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5"); //初始化KeyGenerator
            SecretKey secretKey = keyGenerator.generateKey(); //生成密钥
//            byte[] key = secretKey.getEncoded(); //获得密钥

            byte[] key =
                    Hex.decodeHex(new char[]{'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a'}); // 自定义密钥

            SecretKey restoreSecretKey = new SecretKeySpec(key, "HmacMD5");//还原密钥
            Mac mac = Mac.getInstance(restoreSecretKey.getAlgorithm());// 实例化MAC
            mac.init(restoreSecretKey); // 初始化MAC
            byte[] hmacMD5Bytes = mac.doFinal(MAC_STR.getBytes());//执行摘要
            System.out.println("JDK HmacMD5: " + Hex.encodeHexString(hmacMD5Bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用BouncyCastle API实现HmacMD5
     */
    public static void bcHmacMD5() {
        HMac hMac = new HMac(new MD5Digest());
        hMac.init(new KeyParameter(org.bouncycastle.util.encoders.Hex.decode("aaaaaaaaaa")));
        hMac.update(MAC_STR.getBytes(), 0, MAC_STR.length());

        byte[] hmacMD5Bytes = new byte[hMac.getMacSize()];
        hMac.doFinal(hmacMD5Bytes, 0);
        System.out.println("BC HmacMD5: " + org.bouncycastle.util.encoders.Hex.toHexString(hmacMD5Bytes));
    }

}
