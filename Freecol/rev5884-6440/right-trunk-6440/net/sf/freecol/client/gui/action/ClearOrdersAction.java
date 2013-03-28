

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class ClearOrdersAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ClearOrdersAction.class.getName());

    public static final String id = "clearOrdersAction";


    
    ClearOrdersAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.orders.clearOrders", null, KeyStroke.getKeyStroke('Z', 0));
    }

    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled() && getFreeColClient().getGUI().getActiveUnit() != null;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().clearOrders(getFreeColClient().getGUI().getActiveUnit());
    }
}
