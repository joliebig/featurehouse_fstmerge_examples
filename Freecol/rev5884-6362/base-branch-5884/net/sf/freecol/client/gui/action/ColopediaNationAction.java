



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.ColopediaPanel;



public class ColopediaNationAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ColopediaNationAction.class.getName());


    public static final String id = "colopediaNationAction";

    
    ColopediaNationAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.colopedia.nation", null, KeyEvent.VK_N);
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showColopediaPanel(ColopediaPanel.PanelType.NATIONS);
    }
}
