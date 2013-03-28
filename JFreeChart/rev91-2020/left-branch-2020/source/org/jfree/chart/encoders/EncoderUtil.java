

package org.jfree.chart.encoders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;


public class EncoderUtil {

    
    public static byte[] encode(BufferedImage image, String format)
        throws IOException {
        ImageEncoder imageEncoder = ImageEncoderFactory.newInstance(format);
        return imageEncoder.encode(image);
    }

    
    public static byte[] encode(BufferedImage image, String format,
                                boolean encodeAlpha) throws IOException {
        ImageEncoder imageEncoder
            = ImageEncoderFactory.newInstance(format, encodeAlpha);
        return imageEncoder.encode(image);
    }

    
    public static byte[] encode(BufferedImage image, String format,
                                float quality) throws IOException {
        ImageEncoder imageEncoder
            = ImageEncoderFactory.newInstance(format, quality);
        return imageEncoder.encode(image);
    }

    
    public static byte[] encode(BufferedImage image, String format,
                                float quality, boolean encodeAlpha)
        throws IOException {
        ImageEncoder imageEncoder
            = ImageEncoderFactory.newInstance(format, quality, encodeAlpha);
        return imageEncoder.encode(image);
    }

    
    public static void writeBufferedImage(BufferedImage image, String format,
        OutputStream outputStream) throws IOException {
        ImageEncoder imageEncoder = ImageEncoderFactory.newInstance(format);
        imageEncoder.encode(image, outputStream);
    }

    
    public static void writeBufferedImage(BufferedImage image, String format,
        OutputStream outputStream, float quality) throws IOException {
        ImageEncoder imageEncoder
            = ImageEncoderFactory.newInstance(format, quality);
        imageEncoder.encode(image, outputStream);
    }

    
    public static void writeBufferedImage(BufferedImage image, String format,
        OutputStream outputStream, boolean encodeAlpha) throws IOException {
        ImageEncoder imageEncoder
            = ImageEncoderFactory.newInstance(format, encodeAlpha);
        imageEncoder.encode(image, outputStream);
    }

    
    public static void writeBufferedImage(BufferedImage image, String format,
        OutputStream outputStream, float quality, boolean encodeAlpha)
        throws IOException {
        ImageEncoder imageEncoder
            = ImageEncoderFactory.newInstance(format, quality, encodeAlpha);
        imageEncoder.encode(image, outputStream);
    }

}
