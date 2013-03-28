

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.resources.ResourceManager;


public class ColopediaTreeCellRenderer extends DefaultTreeCellRenderer {
    
    public ImageIcon icon;
    
    
    public ColopediaTreeCellRenderer() {
        setBackgroundNonSelectionColor(new Color(0,0,0,1));
    }
    
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        ColopediaTreeItem nodeItem = (ColopediaTreeItem)node.getUserObject();
        
        if (leaf || nodeItem.getFreeColGameObjectType() instanceof BuildingType) {
            ImageIcon icon = nodeItem.getIcon();
            setIcon(icon);
        } else if (expanded) {
            Image openImage = ResourceManager.getImage("Colopedia.openSection.image");
            ImageIcon openIcon = new ImageIcon((openImage != null) ? openImage : null);
            setIcon(openIcon);
        } else {
            Image closedImage = ResourceManager.getImage("Colopedia.closedSection.image");
            ImageIcon closedIcon = new ImageIcon((closedImage != null) ? closedImage : null);
            setIcon(closedIcon);
        }
        return this;
    }
}
