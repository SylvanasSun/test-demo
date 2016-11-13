package com.sun.sylvanas.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于Google Zxing的二维码生成工具类
 * <p>
 * Created by sylvanasp on 2016/11/13.
 */
public class QRCodeUtils {

    private static final MultiFormatWriter writer = new MultiFormatWriter();

    private static final MultiFormatReader reader = new MultiFormatReader();

    /**
     * 生成二维码
     *
     * @param width    宽度
     * @param height   高度
     * @param format   格式
     * @param content  内容
     * @param filePath 二维码文件存储的路径
     * @return 返回文件名
     */
    public static String createQRCode(int width, int height, String format, String content, String filePath) {

        //设置参数
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 2);

        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            //生成文件名
            String fileName = "QRCode_" + new Date().getTime() + ".png";
            //设置输出路径
            Path outPath = new File(filePath + fileName).toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, format, outPath);

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成二维码,使用默认的宽高
     *
     * @param format   格式
     * @param content  内容
     * @param filePath 二维码文件存储的路径
     * @return 返回文件名
     */
    public static String createQRCode(String format, String content, String filePath) {
        int width = 344;
        int height = 344;
        return createQRCode(width, height, format, content, filePath);
    }

    /**
     * 解析二维码
     *
     * @param filePath 二维码文件路径
     * @return 二维码结果集
     */
    public static Result readQRCode(String filePath) {
        File file = new File(filePath);
        try {
            BufferedImage image = ImageIO.read(file);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));

            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            return reader.decode(binaryBitmap, hints);
        } catch (IOException e) {
            e.printStackTrace();

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
