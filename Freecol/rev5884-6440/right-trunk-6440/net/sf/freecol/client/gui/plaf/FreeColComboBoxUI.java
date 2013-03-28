

package net.sf.freecol.client.gui.plaf;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalComboBoxUI;



public class FreeColComboBoxUI extends MetalComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new FreeColComboBoxUI();
    }


    public void installUI(JComponent c) {
        super.installUI(c);

        c.setOpaque(false);
    }


    
    
    protected ListCellRenderer createRenderer() {
        return new FreeColComboBoxRenderer.UIResource();
    }
}
