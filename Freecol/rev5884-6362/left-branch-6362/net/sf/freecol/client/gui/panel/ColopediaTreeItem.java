

package net.sf.freecol.client.gui.panel;

import javax.swing.ImageIcon;

import net.sf.freecol.client.gui.panel.ColopediaPanel.PanelType;
import net.sf.freecol.common.model.FreeColGameObjectType;


class ColopediaTreeItem {

    private PanelType panelType;
    private FreeColGameObjectType objectType;
    private String text;
    private ImageIcon icon;

    
    ColopediaTreeItem(PanelType panelType, String text) {
        this.panelType = panelType;
        this.text = text;
    }

    
    ColopediaTreeItem(FreeColGameObjectType objectType, String text, ImageIcon icon) {
        this.objectType = objectType;
        this.text = text;
        this.icon = icon;
    }

    
    public FreeColGameObjectType getFreeColGameObjectType() {
        return objectType;
    }

    
    public PanelType getPanelType() {
        return panelType;
    }

    
    @Override
    public String toString() {
        return text;
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
}
