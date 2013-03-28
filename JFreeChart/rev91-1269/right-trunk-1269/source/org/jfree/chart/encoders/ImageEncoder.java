

package org.jfree.chart.encoders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;


public interface ImageEncoder {

    
    public byte[] encode(BufferedImage bufferedImage) throws IOException;


    
    public void encode(BufferedImage bufferedImage, OutputStream outputStream) 
        throws IOException;

    
    public float getQuality();

    
    public void setQuality(float quality);

    
    public boolean isEncodingAlpha();

    
    public void setEncodingAlpha(boolean encodingAlpha);

}
