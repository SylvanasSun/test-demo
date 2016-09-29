package com.sun.sylvanas.qrcode.zxing;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

/**
 * 使用Zxing读取QR Code二维码
 * <p>
 * Created by sylvanasp on 2016/9/29.
 */
public class ReadQRCode {

    public static void main(String[] args) {

        try {
            MultiFormatReader multiFormatReader = new MultiFormatReader();
            File file = new File("D:/test/blog.png");
            BufferedImage image = ImageIO.read(file);
            BinaryBitmap binaryBitmap =
                    new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));

            HashMap hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 2);

            Result result = multiFormatReader.decode(binaryBitmap, hints);

            System.out.println("解析结果: " + result.toString());
            System.out.println("二维码格式: " + result.getBarcodeFormat());
            System.out.println("二维码内容: " + result.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}