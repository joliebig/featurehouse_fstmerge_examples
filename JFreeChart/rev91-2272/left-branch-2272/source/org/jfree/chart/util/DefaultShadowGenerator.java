

package org.jfree.chart.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;


public class DefaultShadowGenerator implements ShadowGenerator, Serializable {

    
    private int shadowSize;

    
    private Color shadowColor;

    
    private float shadowOpacity;

    
    private double angle;

    
    private int distance;

    
    public DefaultShadowGenerator() {
        this(5, Color.black, 0.5f, 5, -Math.PI / 4);
    }

    
    public DefaultShadowGenerator(int size, Color color, float opacity,
            int distance, double angle) {
        if (color == null) {
            throw new IllegalArgumentException("Null 'color' argument.");
        }
        this.shadowSize = size;
        this.shadowColor = color;
        this.shadowOpacity = opacity;
        this.distance = distance;
        this.angle = angle;
    }

    
    public int getShadowSize() {
        return this.shadowSize;
    }

    
    public Color getShadowColor() {
        return this.shadowColor;
    }

    
    public float getShadowOpacity() {
        return this.shadowOpacity;
    }

    
    public int getDistance() {
        return this.distance;
    }

    
    public double getAngle() {
        return this.angle;
    }

    
    public int calculateOffsetX() {
        return (int) (Math.cos(this.angle) * this.distance) - this.shadowSize;
    }

    
    public int calculateOffsetY() {
        return -(int) (Math.sin(this.angle) * this.distance) - this.shadowSize;
    }

    
    public BufferedImage createDropShadow(BufferedImage source) {
        BufferedImage subject = new BufferedImage(
                source.getWidth() + this.shadowSize * 2,
                source.getHeight() + this.shadowSize * 2,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = subject.createGraphics();
        g2.drawImage(source, null, shadowSize, shadowSize);
        g2.dispose();
        applyShadow(subject);
        return subject;
    }

    
    protected void applyShadow(BufferedImage image) {
        int dstWidth = image.getWidth();
        int dstHeight = image.getHeight();

        int left = (shadowSize - 1) >> 1;
        int right = shadowSize - left;
        int xStart = left;
        int xStop = dstWidth - right;
        int yStart = left;
        int yStop = dstHeight - right;

        int shadowRgb = shadowColor.getRGB() & 0x00FFFFFF;

        int[] aHistory = new int[shadowSize];
        int historyIdx = 0;

        int aSum;

        int[] dataBuffer = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        int lastPixelOffset = right * dstWidth;
        float sumDivider = shadowOpacity / shadowSize;

        

        for (int y = 0, bufferOffset = 0; y < dstHeight; y++, bufferOffset = y * dstWidth) {
            aSum = 0;
            historyIdx = 0;
            for (int x = 0; x < shadowSize; x++, bufferOffset++) {
                int a = dataBuffer[bufferOffset] >>> 24;
                aHistory[x] = a;
                aSum += a;
            }

            bufferOffset -= right;

            for (int x = xStart; x < xStop; x++, bufferOffset++) {
                int a = (int) (aSum * sumDivider);
                dataBuffer[bufferOffset] = a << 24 | shadowRgb;

                
                aSum -= aHistory[historyIdx];

                
                a = dataBuffer[bufferOffset + right] >>> 24;
                aHistory[historyIdx] = a;
                aSum += a;

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }

        
        for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {
            aSum = 0;
            historyIdx = 0;
            for (int y = 0; y < shadowSize; y++, bufferOffset += dstWidth) {
                int a = dataBuffer[bufferOffset] >>> 24;
                aHistory[y] = a;
                aSum += a;
            }

            bufferOffset -= lastPixelOffset;

            for (int y = yStart; y < yStop; y++, bufferOffset += dstWidth) {
                int a = (int) (aSum * sumDivider);
                dataBuffer[bufferOffset] = a << 24 | shadowRgb;

                
                aSum -= aHistory[historyIdx];

                
                a = dataBuffer[bufferOffset + lastPixelOffset] >>> 24;
                aHistory[historyIdx] = a;
                aSum += a;

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultShadowGenerator)) {
            return false;
        }
        DefaultShadowGenerator that = (DefaultShadowGenerator) obj;
        if (this.shadowSize != that.shadowSize) {
            return false;
        }
        if (!this.shadowColor.equals(that.shadowColor)) {
            return false;
        }
        if (this.shadowOpacity != that.shadowOpacity) {
            return false;
        }
        if (this.distance != that.distance) {
            return false;
        }
        if (this.angle != that.angle) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int hash = HashUtilities.hashCode(17, this.shadowSize);
        hash = HashUtilities.hashCode(hash, this.shadowColor);
        hash = HashUtilities.hashCode(hash, this.shadowOpacity);
        hash = HashUtilities.hashCode(hash, this.distance);
        hash = HashUtilities.hashCode(hash, this.angle);
        return hash;
    }

}
