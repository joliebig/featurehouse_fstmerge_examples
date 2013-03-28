

package net.sf.freecol.client.gui.plaf;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;


public class FreeColMenuItemUI extends BasicMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new FreeColMenuItemUI();
    }

    
    public void installUI(JComponent c) {
        super.installUI(c);

        c.setOpaque(false);
    }
    
    @Override
    public void paint(Graphics g, JComponent c) { 
        LAFUtilities.setProperties(g, c);
        super.paint(g, c);
    }
}
