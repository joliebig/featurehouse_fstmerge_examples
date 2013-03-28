

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.resources.ResourceManager;


public class FreeColProgressBar extends JPanel {

    private static final Color PRIMARY_1 = new Color(122, 109, 82);

    
    private int min = 0;

    
    private int max = 100;

    
    private int value = 0;

    
    private int step = 0;

    private int iconWidth;

    private int iconHeight = 16;

    
    private GoodsType goodsType = null;

    @SuppressWarnings("unused")
    private final Canvas parent;

    private Image image;


    
    public FreeColProgressBar(Canvas parent, GoodsType goodsType) {
        this(parent, goodsType, 0, 100, 0, 0);
    }

    
    public FreeColProgressBar(Canvas parent, GoodsType goodsType, int min, int max) {
        this(parent, goodsType, min, max, 0, 0);
    }

    
    public FreeColProgressBar(Canvas parent, GoodsType goodsType, int min, int max, int value, int step) {
        this.parent = parent;
        this.goodsType = goodsType;
        this.min = min;
        this.max = max;
        this.value = value;
        this.step = step;

        setBorder(BorderFactory.createLineBorder(PRIMARY_1));
        ImageIcon icon = parent.getImageLibrary().getGoodsImageIcon(goodsType);
        
        image = icon.getImage().getScaledInstance(-1, iconHeight, Image.SCALE_SMOOTH);
        iconWidth = image.getWidth(this);
        setPreferredSize(new Dimension(200, 20));
    }

    
    public void update(int value, int step) {
        update(min, max, value, step);
    }

    
    public void update(int min, int max, int value, int step) {
        this.min = min;
        this.max = max;
        this.value = value;
        this.step = step;
        repaint();
    }

    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();
        int width = getWidth() - getInsets().left - getInsets().right;
        int height = getHeight() - getInsets().top - getInsets().bottom;

        if (iconWidth < 0) {
            iconWidth = image.getWidth(this);
        }

        if (isOpaque()) {
            Image tempImage = ResourceManager.getImage("BackgroundImage");

            if (tempImage != null) {
                for (int x = getInsets().left; x < width + getInsets().left; x += tempImage.getWidth(null)) {
                    for (int y = getInsets().top; y < height + getInsets().top; y += tempImage.getHeight(null)) {
                        g2d.drawImage(tempImage, x, y, null);
                    }
                }
            } else {
                g2d.setColor(getBackground());
                g2d.fillRect(getInsets().left, getInsets().top, width, height);
            }
        }

        int dvalue = 0;
        if (value >= max) {
            dvalue = width;
        } else if (max > 0) {
            dvalue = width * value / max;
        }
        if (dvalue > 0) {
            if (dvalue > width) {
                dvalue = width;
            }
            g2d.setColor(new Color(0, 0, 0, 70));
            g2d.fillRect(getInsets().left, getInsets().top, dvalue, height);
        }

        int dstep = 0;
        if (max > 0) {
            dstep = width * step / max;
            if (dstep > 0) {
                if (dstep + dvalue > width) {
                    dstep = width - dvalue;
                }
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillRect(getInsets().left + dvalue, getInsets().top, dstep, height);
            }
        }

        String stepSignal = (step < 0) ? "-" : "+"; 
        String progressString = String.valueOf(value) + stepSignal + Math.abs(step) + "/" + max;
        String turnsString = Messages.message("notApplicable.short");
        if (max <= value) {
            turnsString = "0";
        } else if (step > 0) {
            
            int turns = (max - value) / step;
            if ((max - value) % step > 0) {
                turns++;
            }
            turnsString = Integer.toString(turns);
        }
        progressString += " " + Messages.message("turnsToComplete.short", "%number%", turnsString);
        
        int stringWidth = g2d.getFontMetrics().stringWidth(progressString);
        int stringHeight = g2d.getFontMetrics().getAscent() + g2d.getFontMetrics().getDescent();
        int restWidth = getWidth() - stringWidth;

        if (goodsType != null) {
            restWidth -= iconWidth;
            g2d.drawImage(image, restWidth / 2, (getHeight() - iconHeight) / 2, null);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString(progressString, restWidth / 2 + iconWidth, getHeight() / 2 + stringHeight / 4);

        g2d.dispose();
    }

}
