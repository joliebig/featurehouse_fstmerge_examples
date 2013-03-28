

package org.jfree.experimental.swt;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
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
    
    
    public static MouseEvent toAwtMouseEvent(org.eclipse.swt.widgets.Event event) {
        MouseEvent awtMouseEvent = new MouseEvent(DUMMY_PANEL, event.hashCode(), 
                (long) event.time, SWT.NONE, event.x, event.y, 1, false);
        return awtMouseEvent;
    }
}
