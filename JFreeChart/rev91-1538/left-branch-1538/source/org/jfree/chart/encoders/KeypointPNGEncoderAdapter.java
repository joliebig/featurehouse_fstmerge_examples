

package org.jfree.chart.encoders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import com.keypoint.PngEncoder;


public class KeypointPNGEncoderAdapter implements ImageEncoder {

    
    private int quality = 9;

    
    private boolean encodingAlpha = false;

    
    public float getQuality() {
        return this.quality;
    }

    
    public void setQuality(float quality) {
        this.quality = (int) quality;
    }

    
    public boolean isEncodingAlpha() {
        return this.encodingAlpha;
    }

    
    public void setEncodingAlpha(boolean encodingAlpha) {
        this.encodingAlpha = encodingAlpha;
    }

    
    public byte[] encode(BufferedImage bufferedImage) throws IOException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        PngEncoder encoder = new PngEncoder(bufferedImage, this.encodingAlpha,
                0, this.quality);
        return encoder.pngEncode();
    }

    
    public void encode(BufferedImage bufferedImage, OutputStream outputStream)
        throws IOException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        if (outputStream == null) {
            throw new IllegalArgumentException("Null 'outputStream' argument.");
        }
        PngEncoder encoder = new PngEncoder(bufferedImage, this.encodingAlpha,
                0, this.quality);
        outputStream.write(encoder.pngEncode());
    }

}
