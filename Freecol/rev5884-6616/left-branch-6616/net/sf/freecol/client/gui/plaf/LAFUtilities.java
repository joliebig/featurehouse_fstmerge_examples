

package net.sf.freecol.client.gui.plaf;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;


public final class LAFUtilities {
    
    private static final int AA_TEXT_SIZE = 16;
    
    
    public static void setProperties(Graphics g, JComponent c) {
        if (c.getFont().getSize() >= AA_TEXT_SIZE) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        Object textAA = c.getClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING);
        if (textAA != null) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, textAA);
        }
    }
}
