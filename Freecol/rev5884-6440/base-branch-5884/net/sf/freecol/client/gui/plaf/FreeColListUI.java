

package net.sf.freecol.client.gui.plaf;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

import net.sf.freecol.common.resources.ResourceManager;


public class FreeColListUI extends BasicListUI {

    public static ComponentUI createUI(JComponent c) {
        return new FreeColListUI();
    }


    public void installUI(JComponent c) {
        super.installUI(c);
        ((JList) c).setCellRenderer(createRenderer());
    }

    public void paint(Graphics g, JComponent c) {
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
         
        LAFUtilities.setProperties(g, c);
        super.paint(g, c);
    }

    protected ListCellRenderer createRenderer() {
        return new FreeColComboBoxRenderer.UIResource();
    }    
}
