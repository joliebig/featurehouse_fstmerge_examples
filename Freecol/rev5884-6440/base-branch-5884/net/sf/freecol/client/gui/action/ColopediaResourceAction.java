

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.ColopediaPanel;



public class ColopediaResourceAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ColopediaResourceAction.class.getName());


    public static final String id = "colopediaResourceAction";
    
    
    ColopediaResourceAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.colopedia.resource", null, KeyEvent.VK_R);
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showColopediaPanel(ColopediaPanel.PanelType.RESOURCES);
    }
}
