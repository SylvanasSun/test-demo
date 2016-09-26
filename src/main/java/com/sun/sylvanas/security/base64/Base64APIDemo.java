package com.sun.sylvanas.security.base64;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

/**
 * Base64加密API案例
 * <p>
 * Created by sylvanasp on 2016/9/26.
 */
public class Base64APIDemo {

    private static final String JDK_STR = "Base64 JDK";

    private static final String CC_STR = "Base64 Commons Codec";

    private static final String BC_STR = "Base64 Bouncy Castle";

    public static void main(String[] args) {
        jdkBase64();
        commonsCodecBase64();
        bouncyCastleBase64();
    }

    /**
     * 使用JDK API实现Base64加密解密
     */
    public static void jdkBase64() {
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(JDK_STR.getBytes());
        System.out.println("encode: " + encode);

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] decodeBuffer = decoder.decodeBuffer(encode);
            System.out.println("decode: " + new String(decodeBuffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 使用Commons Codec实现Base64加密解密
     */
    public static void commonsCodecBase64() {
        byte[] encode = Base64.encodeBase64(CC_STR.getBytes());
        System.out.println("encode: " + new String(encode));

        byte[] decode = Base64.decodeBase64(encode);
        System.out.println("decode: " + new String(decode));
    }

    /**
     * 使用Bouncy Castle实现Base64加密解密
     */
    public static void bouncyCastleBase64() {
        byte[] encode = org.bouncycastle.util.encoders.Base64.encode(BC_STR.getBytes());
        System.out.println("encode: " + new String(encode));

        byte[] decode = org.bouncycastle.util.encoders.Base64.decode(encode);
        System.out.println("decode: " + new String(decode));
    }

}
