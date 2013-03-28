

package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.border.AbstractBorder;


public class FreeColImageBorder extends AbstractBorder {
	
    private BufferedImage topImage;
    private BufferedImage leftImage;
    private BufferedImage bottomImage;    
    private BufferedImage rightImage;
    private BufferedImage topLeftCornerImage;
    private BufferedImage topRightCornerImage;
    private BufferedImage bottomLeftCornerImage;
    private BufferedImage bottomRightCornerImage;
    
    
    public FreeColImageBorder(Image topImage, Image leftImage, Image bottomImage, Image rightImage, 
                Image topLeftCornerImage, Image topRightCornerImage, Image bottomLeftCornerImage, Image bottomRightCornerImage) {
    	
        this.topImage = createBufferedImage(topImage);
        this.leftImage = createBufferedImage(leftImage);
        this.bottomImage = createBufferedImage(bottomImage);
        this.rightImage = createBufferedImage(rightImage);
        this.topLeftCornerImage = createBufferedImage(topLeftCornerImage);
        this.topRightCornerImage = createBufferedImage(topRightCornerImage);
        this.bottomLeftCornerImage = createBufferedImage(bottomLeftCornerImage);
        this.bottomRightCornerImage = createBufferedImage(bottomRightCornerImage);
    }
    
    
    private BufferedImage createBufferedImage(Image img) {
    	if(img != null) {
	    	BufferedImage buff = new BufferedImage(getWidth(img), getHeight(img), BufferedImage.TYPE_INT_ARGB);
	    	Graphics gfx = buff.createGraphics();
	    	gfx.drawImage(img, 0, 0, null);
	    	gfx.dispose();
	    	return buff;
    	} else {
    		return null;
    	}
    }
    
        
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, null);
    }
    
    
    public Insets getBorderInsets(Component c, Insets insets) {        
        int top = Math.max(Math.max(getHeight(topImage), getHeight(topLeftCornerImage)), getHeight(topRightCornerImage));
        int left = Math.max(Math.max(getWidth(leftImage), getWidth(topLeftCornerImage)), getWidth(bottomLeftCornerImage));
        int bottom = Math.max(Math.max(getHeight(bottomImage), getHeight(bottomLeftCornerImage)), getHeight(bottomRightCornerImage));
        int right = Math.max(Math.max(getWidth(rightImage), getWidth(topRightCornerImage)), getWidth(bottomRightCornerImage));
    
        if (leftImage == null) {
            left = 0;
        }
        if (rightImage == null) {
            right = 0;
        }
        if (topImage == null) {
            top = 0;
        }
        if (bottomImage == null) {
            bottom = 0;
        }
        
        if (insets == null) {
            return new Insets(top, left, bottom, right);
        } else {
            insets.top = top;
            insets.left = left;
            insets.bottom = bottom;
            insets.right = right;
            return insets;
        }
    }
    
    
    private int getHeight(Image im) {
        return (im != null) ? im.getHeight(null) : 0;
    }
    
    
    private int getWidth(Image im) {
        return (im != null) ? im.getWidth(null) : 0;
    }        

    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    	Insets insets = getBorderInsets(c);
    	Graphics2D g2 = (Graphics2D) g;
    	
    	
    	int topHeight = getHeight(topImage);
    	int leftWidth = getWidth(leftImage);
    	int bottomHeight = getHeight(bottomImage);
    	int rightWidth = getWidth(rightImage);	
		int topLeftCornerWidth = getWidth(topLeftCornerImage);
    	int topLeftCornerHeight = getHeight(topLeftCornerImage);
		int topRightCornerWidth = getWidth(topRightCornerImage);
    	int topRightCornerHeight = getHeight(topRightCornerImage);
		int bottomLeftCornerWidth = getWidth(bottomLeftCornerImage);
    	int bottomLeftCornerHeight = getHeight(bottomLeftCornerImage);
		int bottomRightCornerWidth = getWidth(bottomRightCornerImage);
    	int bottomRightCornerHeight = getHeight(bottomRightCornerImage);
    	
    	
    	if(topImage != null) {
    		fillTexture(g2, topImage, x + topLeftCornerWidth, y + insets.top - topHeight, width - topLeftCornerWidth - topRightCornerWidth, topHeight);
    	}
    	if(leftImage != null) {
    		fillTexture(g2, leftImage, x + insets.left - leftWidth, y + topLeftCornerHeight, leftWidth, height - topLeftCornerHeight - bottomLeftCornerHeight);
    	}
    	if(bottomImage != null) {
    		fillTexture(g2, bottomImage, x + bottomLeftCornerWidth, y + height - insets.bottom, width - bottomLeftCornerWidth - bottomRightCornerWidth, bottomHeight);
    	}
    	if(rightImage != null) {
    		fillTexture(g2, rightImage, x + width - insets.right, y + topRightCornerHeight, rightWidth, height - topRightCornerHeight - bottomRightCornerHeight);
    	}
    	if(topLeftCornerImage != null) {
    		fillTexture(g2, topLeftCornerImage, x + Math.max(insets.left, topLeftCornerWidth) - topLeftCornerWidth, y + Math.max(insets.top, topLeftCornerHeight) - topLeftCornerHeight, topLeftCornerWidth, topLeftCornerHeight);
    	}
    	if(topRightCornerImage != null) {
    		fillTexture(g2, topRightCornerImage, x + width - Math.max(insets.right, topRightCornerWidth), y + Math.max(insets.top, topRightCornerHeight) - topRightCornerHeight, topRightCornerWidth, topRightCornerHeight);
    	}
    	if(bottomLeftCornerImage != null) {
    		fillTexture(g2, bottomLeftCornerImage, x + Math.max(insets.left, bottomLeftCornerWidth) - bottomLeftCornerWidth, y + height - Math.max(insets.bottom, bottomLeftCornerHeight), bottomLeftCornerWidth, bottomLeftCornerHeight);
    	}
    	if(bottomRightCornerImage != null) {
    		fillTexture(g2, bottomRightCornerImage, x + width - Math.max(insets.right, bottomRightCornerWidth), y + height - Math.max(insets.bottom, bottomRightCornerHeight), bottomRightCornerWidth, bottomRightCornerHeight);
    	}
    }
    
    
    public void fillTexture(Graphics2D g2, BufferedImage img, int x, int y, int width, int height) {
    	Rectangle anchor = new Rectangle(x, y, getWidth(img), getHeight(img));
    	TexturePaint paint = new TexturePaint(img, anchor);
    	g2.setPaint(paint);
    	g2.fillRect(x, y, width, height);
    }
}