

package org.jfree.chart.encoders;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;


public class SunJPEGEncoderAdapter implements ImageEncoder {
    
    
    private float quality = 0.95f;

    
    public SunJPEGEncoderAdapter() {
    }

    
    public float getQuality() {
        return this.quality;
    }

    
    public void setQuality(float quality) {
        if (quality < 0.0f || quality > 1.0f) {
            throw new IllegalArgumentException(
                    "The 'quality' must be in the range 0.0f to 1.0f");
        }
        this.quality = quality;
    }

    
    public boolean isEncodingAlpha() {
        return false;
    }

    
    public void setEncodingAlpha(boolean encodingAlpha) {
        
    }

    
    public byte[] encode(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encode(bufferedImage, outputStream);
        return outputStream.toByteArray();
    }

    
    public void encode(BufferedImage bufferedImage, OutputStream outputStream) 
            throws IOException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        if (outputStream == null) {
            throw new IllegalArgumentException("Null 'outputStream' argument.");
        }
        Iterator iterator = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = (ImageWriter) iterator.next();
        ImageWriteParam p = writer.getDefaultWriteParam();
        p.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        p.setCompressionQuality(this.quality);
        ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(bufferedImage, null, null), p);
        ios.flush();
        writer.dispose();
        ios.close();
    }

}
