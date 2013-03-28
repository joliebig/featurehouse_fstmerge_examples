

package net.sf.freecol.client.gui.plaf;

import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

import net.sf.freecol.common.resources.ResourceManager;



public class FreeColPanelUI extends BasicPanelUI {
    
    private static FreeColPanelUI sharedInstance = new FreeColPanelUI();
    
    private FreeColPanelUI() {
    }

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

    public void paint(java.awt.Graphics g, javax.swing.JComponent c) {
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
    }
}
