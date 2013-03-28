

package net.sf.freecol.client.gui.plaf;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;



public class FreeColTableHeaderUI extends BasicTableHeaderUI {

    public static ComponentUI createUI(JComponent c) {
        return new FreeColTableHeaderUI();
    }

    
    public void installUI(JComponent c) {
        super.installUI(c);

        JTableHeader j = (JTableHeader) c;
        j.setOpaque(false);

        DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) j.getDefaultRenderer();
        dtcr.setOpaque(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) { 
        LAFUtilities.setProperties(g, c);
        super.paint(g, c);
    }
}
