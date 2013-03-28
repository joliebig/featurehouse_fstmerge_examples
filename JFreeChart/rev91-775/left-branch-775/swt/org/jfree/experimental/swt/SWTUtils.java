

package org.jfree.experimental.swt;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;


public class SWTUtils {
    
    private final static String Az = "ABCpqr";

    
    protected static final JPanel DUMMY_PANEL = new JPanel();

    
    public static FontData toSwtFontData(Device device, java.awt.Font font, 
            boolean ensureSameSize) {
        FontData fontData = new FontData();
        fontData.setName(font.getFamily());
        int style = SWT.NORMAL;
        switch (font.getStyle()) {
            case java.awt.Font.PLAIN:
                style |= SWT.NORMAL;
                break;
            case java.awt.Font.BOLD:
                style |= SWT.BOLD;
                break;
            case java.awt.Font.ITALIC:
                style |= SWT.ITALIC;
                break;
            case (java.awt.Font.ITALIC + java.awt.Font.BOLD):
                style |= SWT.ITALIC | SWT.BOLD;
                break;
        }
        fontData.setStyle(style);
        
        int height = (int) Math.round(font.getSize() * 72.0 
                / device.getDPI().y);
        fontData.setHeight(height);
        
        
        if (ensureSameSize) {            
            GC tmpGC = new GC(device);
            Font tmpFont = new Font(device, fontData);
            tmpGC.setFont(tmpFont);
            if (tmpGC.textExtent(Az).x 
                    > DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                while (tmpGC.textExtent(Az).x 
                        > DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                    tmpFont.dispose();
                    height--;
                    fontData.setHeight(height);
                    tmpFont = new Font(device, fontData);
                    tmpGC.setFont(tmpFont);
                }
            }
            else if (tmpGC.textExtent(Az).x 
                    < DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                while (tmpGC.textExtent(Az).x 
                        < DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                    tmpFont.dispose();
                    height++;
                    fontData.setHeight(height);
                    tmpFont = new Font(device, fontData);
                    tmpGC.setFont(tmpFont);
                }
            }
            tmpFont.dispose();
            tmpGC.dispose();
        }
        return fontData;
    }
    
    
    public static java.awt.Font toAwtFont(Device device, FontData fontData, 
            boolean ensureSameSize) {
        int style;
        switch (fontData.getStyle()) {
            case SWT.NORMAL:
                style = java.awt.Font.PLAIN;
                break;
            case SWT.ITALIC:
                style = java.awt.Font.ITALIC;
                break;
            case SWT.BOLD:
                style = java.awt.Font.BOLD;
                break;
            default:
                style = java.awt.Font.PLAIN;
                break;
        }
        int height = (int) Math.round(fontData.getHeight() * device.getDPI().y 
                / 72.0);
        
        
        if (ensureSameSize) {
            GC tmpGC = new GC(device);
            Font tmpFont = new Font(device, fontData);
            tmpGC.setFont(tmpFont);
            JPanel DUMMY_PANEL = new JPanel();
            java.awt.Font tmpAwtFont = new java.awt.Font(fontData.getName(), 
                    style, height);
            if (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) 
                    > tmpGC.textExtent(Az).x) {
                while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) 
                        > tmpGC.textExtent(Az).x) {
                    height--;                
                    tmpAwtFont = new java.awt.Font(fontData.getName(), style, 
                            height);
                }
            }
            else if (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) 
                    < tmpGC.textExtent(Az).x) {
                while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) 
                        < tmpGC.textExtent(Az).x) {
                    height++;
                    tmpAwtFont = new java.awt.Font(fontData.getName(), style, 
                            height);
                }
            }
            tmpFont.dispose();
            tmpGC.dispose();
        }
        return new java.awt.Font(fontData.getName(), style, height);
    }

    
    public static java.awt.Font toAwtFont(Device device, Font font) {
        FontData fontData = font.getFontData()[0]; 
        return toAwtFont(device, fontData, true);
    }

    
    public static java.awt.Color toAwtColor(Color color) {
        return new java.awt.Color(color.getRed(), color.getGreen(), 
                color.getBlue());
    }
    
    
    public static Color toSwtColor(Device device, java.awt.Paint paint) {
        java.awt.Color color;
        if (paint instanceof java.awt.Color) {
            color = (java.awt.Color) paint;
        }
        else {
            try {
                throw new Exception("only color is supported at present... " 
                        + "setting paint to uniform black color" );
            } 
            catch (Exception e) {
                e.printStackTrace();
                color = new java.awt.Color(0, 0, 0);
            }
        }
        return new org.eclipse.swt.graphics.Color(device,
                color.getRed(), color.getGreen(), color.getBlue());
    }

    
    public static Color toSwtColor(Device device, java.awt.Color color) {
        return new org.eclipse.swt.graphics.Color(device,
                color.getRed(), color.getGreen(), color.getBlue());
    }
    
    
    public static Rectangle toSwtRectangle(Rectangle2D rect2d) {
        return new Rectangle(
                (int) Math.round(rect2d.getMinX()),
                (int) Math.round(rect2d.getMinY()),
                (int) Math.round(rect2d.getWidth()),
                (int) Math.round(rect2d.getHeight())
                );
    }

    
    public static Rectangle2D toAwtRectangle(Rectangle rect) {
        Rectangle2D rect2d = new Rectangle2D.Double();
        rect2d.setRect(rect.x, rect.y, rect.width, rect.height);
        return rect2d;
    }
    
    
    public static Point2D toAwtPoint(Point p) {
        return new java.awt.Point(p.x, p.y);
    }

    
    public static Point toSwtPoint(java.awt.Point p) {
        return new Point(p.x, p.y);
    }
    
    
    public static Point toSwtPoint(java.awt.geom.Point2D p) {
        return new Point((int) Math.round(p.getX()), 
                (int) Math.round(p.getY()));
    }
    
    
    public static MouseEvent toAwtMouseEvent(org.eclipse.swt.events.MouseEvent event) {
        int button = MouseEvent.NOBUTTON;
        switch (event.button) {
        case 1: button = MouseEvent.BUTTON1; break;
        case 2: button = MouseEvent.BUTTON2; break;
        case 3: button = MouseEvent.BUTTON3; break;
        }
        int modifiers = 0;
        if ((event.stateMask & SWT.CTRL) != 0) {
            modifiers |= InputEvent.CTRL_DOWN_MASK;
        }
        if ((event.stateMask & SWT.SHIFT) != 0) {
            modifiers |= InputEvent.SHIFT_DOWN_MASK;
        }
        if ((event.stateMask & SWT.ALT) != 0) {
            modifiers |= InputEvent.ALT_DOWN_MASK;
        }
        MouseEvent awtMouseEvent = new MouseEvent(DUMMY_PANEL, event.hashCode(), 
                event.time, modifiers, event.x, event.y, 1, false, button);
        return awtMouseEvent;
    }
    
    
    public static ImageData convertAWTImageToSWT(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        if (w == -1 || h == -1) {
            return null;
        }
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return convertToSWT(bi);
    }
    
    
    public static ImageData convertToSWT(BufferedImage bufferedImage) {
        if (bufferedImage.getColorModel() instanceof DirectColorModel) {
            DirectColorModel colorModel 
                    = (DirectColorModel) bufferedImage.getColorModel();
            PaletteData palette = new PaletteData(colorModel.getRedMask(),
                    colorModel.getGreenMask(), colorModel.getBlueMask());
            ImageData data = new ImageData(bufferedImage.getWidth(), 
                    bufferedImage.getHeight(), colorModel.getPixelSize(),
                    palette);
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[3];
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    raster.getPixel(x, y, pixelArray);
                    int pixel = palette.getPixel(new RGB(pixelArray[0], 
                            pixelArray[1], pixelArray[2]));
                    data.setPixel(x, y, pixel);
                }
            }
            return data;
        } 
        else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
            IndexColorModel colorModel = (IndexColorModel) 
                    bufferedImage.getColorModel();
            int size = colorModel.getMapSize();
            byte[] reds = new byte[size];
            byte[] greens = new byte[size];
            byte[] blues = new byte[size];
            colorModel.getReds(reds);
            colorModel.getGreens(greens);
            colorModel.getBlues(blues);
            RGB[] rgbs = new RGB[size];
            for (int i = 0; i < rgbs.length; i++) {
                rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, 
                        blues[i] & 0xFF);
            }
            PaletteData palette = new PaletteData(rgbs);
            ImageData data = new ImageData(bufferedImage.getWidth(),
                    bufferedImage.getHeight(), colorModel.getPixelSize(),
                    palette);
            data.transparentPixel = colorModel.getTransparentPixel();
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    raster.getPixel(x, y, pixelArray);
                    data.setPixel(x, y, pixelArray[0]);
                }
            }
            return data;
        }
        return null;
    }

}
