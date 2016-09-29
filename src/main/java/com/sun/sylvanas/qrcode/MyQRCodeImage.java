package com.sun.sylvanas.qrcode;

import jp.sourceforge.qrcode.data.QRCodeImage;

import java.awt.image.BufferedImage;

/**
 * Created by sylvanasp on 2016/9/29.
 */
public class MyQRCodeImage implements QRCodeImage {

    private BufferedImage bufferedImage;

    public MyQRCodeImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public int getWidth() {
        return bufferedImage.getWidth();
    }

    public int getHeight() {
        return bufferedImage.getHeight();
    }

    public int getPixel(int i, int i1) {
        return bufferedImage.getRGB(i, i1);
    }
}
