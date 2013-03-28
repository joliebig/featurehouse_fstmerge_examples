

package net.sf.freecol.client.gui.plaf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;



public class FreeColTransparentPanelUI extends BasicPanelUI {
    
    private static FreeColTransparentPanelUI sharedInstance = new FreeColTransparentPanelUI();
    
    

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

    public void paint(java.awt.Graphics g, javax.swing.JComponent c) {
        if (c.isOpaque()) {
            throw new IllegalStateException("FreeColTransparentPanelUI can only be used on components which are !isOpaque()");
        }
        
        int width = c.getWidth();
        int height = c.getHeight();
        
        Composite oldComposite = ((Graphics2D) g).getComposite();
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        ((Graphics2D) g).setComposite(oldComposite);
    }

}
