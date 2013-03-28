


package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sf.freecol.common.resources.ResourceManager;


public class ColorCellRenderer extends JLabel implements TableCellRenderer {

    
    public ColorCellRenderer(boolean useBorder) {
        if (useBorder) {
            ImageIcon background = new ImageIcon(ResourceManager.getImage("BackgroundImage"));
            setBorder(BorderFactory
                      .createCompoundBorder(BorderFactory
                                            .createMatteBorder(5, 10, 5, 10, background),
                                            BorderFactory
                                            .createLineBorder(Color.BLACK)));
        }
        
        setOpaque(true);
    }

    
    public Component getTableCellRendererComponent(JTable table, Object color,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        Color myColor = (Color)color;

        setBackground(myColor);

        setToolTipText("RGB value: " + myColor.getRed() + ", " + myColor.getGreen() + ", "
                       + myColor.getBlue());

        return this;
    }
}
