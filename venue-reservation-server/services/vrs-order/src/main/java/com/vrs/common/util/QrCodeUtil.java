package com.vrs.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.resource.ClassPathResource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 * 工具类来源：https://blog.csdn.net/qq_38322527/article/details/103307636
 * @Author dam
 * @create 2024/12/29 19:55
 */
public class QrCodeUtil {
    public static String createQRCode(String content) {
        return createQRCode(content,200,200);
    }

    /**
     * 生成加密后的二维码字符串 Base64字符串二维码
     * @param content 二维码正文
     * @param width 二维码宽
     * @param height 二维码高
     * @return base64 编码后的字符串
     * @throws IOException
     */
    public static String createQRCode(String content, int width, int height) {
        String resultImage = "";
        if (!StringUtils.isEmpty(content)) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Map<EncodeHintType, Comparable> hints = new HashMap<>();
            // 指定字符编码为“utf-8”
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 指定二维码的纠错等级为中级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            // 设置图片的边距
            hints.put(EncodeHintType.MARGIN, 2);

            try {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                ImageIO.write(bufferedImage, "png", os);
                /*
                 * 原生转码前面没有 data:image/png;base64 这些字段，返回给前端是无法被解析，可以让前端加，也可以在下面加上
                 */
                resultImage = new String("data:image/jpeg;base64," + Base64.encode(os.toByteArray()));

                return resultImage;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String createQRCodeWithImg(String content) {
        return createQRCodeWithImg(content,200,200);
    }

    public static String createQRCodeWithImg(String content, int width, int height) {
        String resultImage = "";
        if (!StringUtils.isEmpty(content)) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Map<EncodeHintType, Comparable> hints = new HashMap<>();
            // 指定字符编码为“utf-8”
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 指定二维码的纠错等级为中级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            // 设置图片的边距
            hints.put(EncodeHintType.MARGIN, 2);

            try {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

                BufferedImage qrImage = toBufferedImage(bitMatrix);
                //读取Logo图片
                ClassPathResource aliPayPng = new ClassPathResource("static/alipay.png");
                File logoPic = new File(aliPayPng.getAbsolutePath());
                BufferedImage logImage = ImageIO.read(logoPic);

                Graphics2D graphics = qrImage.createGraphics();
                // 缩放尺寸
                int widthLogo = Math.min(logImage.getWidth(null), qrImage.getWidth() * 2 / 10);
                int heightLogo = Math.min(logImage.getHeight(null), qrImage.getHeight() * 2 / 10);
                int x = (qrImage.getWidth() - widthLogo) / 2;
                int y = (qrImage.getHeight() - heightLogo) / 2;
                graphics.drawImage(logImage, x, y, widthLogo, heightLogo, null);
                graphics.dispose();


                ImageIO.write(qrImage, "png", os);
                /*
                 * 原生转码前面没有 data:image/png;base64 这些字段，返回给前端是无法被解析，可以让前端加，也可以在下面加上
                 */
                resultImage = new String("data:image/jpeg;base64," + Base64.encode(os.toByteArray()));

                return resultImage;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y)? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }

}
