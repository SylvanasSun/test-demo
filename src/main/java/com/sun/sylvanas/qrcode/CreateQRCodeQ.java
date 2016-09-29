package com.sun.sylvanas.qrcode;

import com.swetake.util.Qrcode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 使用QRCode生成二维码
 * <p>
 * Created by sylvanasp on 2016/9/29.
 */
public class CreateQRCodeQ {

    public static void main(String[] args) throws IOException {

        Qrcode qrcode = new Qrcode();
        qrcode.setQrcodeErrorCorrect('M');//纠错等级
        qrcode.setQrcodeEncodeMode('B');//代表数字: A代表a-Z B代表其他字符
        qrcode.setQrcodeVersion(7);//版本号(1-40)

        String qrData = "http://sylvanassun.github.io/";
        int width = 67 + 12 * (7 - 1); // 67+12*(版本号-1)
        int height = 67 + 12 * (7 - 1);

        //依托javaGUI画图工具实现的
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setBackground(Color.WHITE);
        graphics.setColor(Color.BLACK);
        graphics.clearRect(0, 0, width, height);

        int offset = 2;//偏移量

        byte[] bytes = qrData.getBytes("utf8");
        if (bytes.length > 0 && bytes.length < 120) {
            boolean[][] b = qrcode.calQrcode(bytes);
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < b.length; j++) {
                    if (b[i][j]) {
                        graphics.fillRect(j * 3 + offset, i * 3 + offset, 3, 3);
                    }
                }
            }
        }

        graphics.dispose();
        image.flush();
        ImageIO.write(image, "png", new File("D:/test/blog.png"));

    }

}
