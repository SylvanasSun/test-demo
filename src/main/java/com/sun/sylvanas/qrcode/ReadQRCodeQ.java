package com.sun.sylvanas.qrcode;

import jp.sourceforge.qrcode.QRCodeDecoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 使用QRCode读取二维码
 * <p>
 * Created by sylvanasp on 2016/9/29.
 */
public class ReadQRCodeQ {

    public static void main(String[] args) throws Exception {

        File file = new File("D:/test/blog.png");
        BufferedImage bufferedImage = ImageIO.read(file);
        QRCodeDecoder codeDecoder = new QRCodeDecoder();

        String result = new String(codeDecoder.decode(new MyQRCodeImage(bufferedImage)), "utf8");
        System.out.println(result);

    }

}
