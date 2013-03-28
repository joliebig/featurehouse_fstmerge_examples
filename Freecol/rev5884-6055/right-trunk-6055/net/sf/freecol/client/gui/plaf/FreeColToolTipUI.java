

package net.sf.freecol.client.gui.plaf;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.View;

import net.sf.freecol.common.resources.ResourceManager;





public class FreeColToolTipUI extends BasicToolTipUI {
    
    private static FreeColToolTipUI sharedInstance = new FreeColToolTipUI();
    
    

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

    public void paint(Graphics g, JComponent c) {


        if (c.isOpaque()) {
            int width = c.getWidth();
            int height = c.getHeight();

            Image tempImage = ResourceManager.getImage("BackgroundImage");

            if (tempImage != null) {
                for (int x=0; x<width; x+=tempImage.getWidth(null)) {
                    for (int y=0; y<height; y+=tempImage.getHeight(null)) {
                        g.drawImage(tempImage, x, y, null);
                    }
                }
            } else {
                g.setColor(c.getBackground());
                g.fillRect(0, 0, width, height);
            }
        }
         
        LAFUtilities.setProperties(g, c);

        
        Font font = c.getFont();
        FontMetrics metrics = g.getFontMetrics(font);
        Dimension size = c.getSize();

        g.setColor(c.getForeground());
        g.setFont(font);

        String tipText = ((JToolTip)c).getTipText();
        if (tipText == null) {
            tipText = "";
        }

        Insets insets = c.getInsets();
        Rectangle paintTextR = new Rectangle(
            insets.left,
            insets.top,
            size.width - (insets.left + insets.right),
            size.height - (insets.top + insets.bottom));

        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            v.paint(g, paintTextR);
        } else {
            g.drawString(tipText, paintTextR.x + 3,
                                  paintTextR.y + metrics.getAscent());
        }
    }

}
