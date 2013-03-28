

package net.sf.freecol.client.gui.plaf;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

import net.sf.freecol.common.resources.ResourceManager;



public class FreeColTextFieldUI extends BasicTextFieldUI {

    private JComponent c;

    public FreeColTextFieldUI(JComponent c) {
        this.c = c;
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new FreeColTextFieldUI(c);
    }
    
    @Override
    public void paintSafely(Graphics g) { 
        LAFUtilities.setProperties(g, c);
        super.paintSafely(g);
    }

    public void paintBackground(java.awt.Graphics g) {
        JComponent c = getComponent();

        if (c.isOpaque()) {
            int width = c.getWidth();
            int height = c.getHeight();

            Image tempImage = ResourceManager.getImage("BackgroundImage2");

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

    }

}
