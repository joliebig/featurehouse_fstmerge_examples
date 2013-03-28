

package net.sf.freecol.client.gui.plaf;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxUI;



public class FreeColCheckBoxUI extends BasicCheckBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new FreeColCheckBoxUI();
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
