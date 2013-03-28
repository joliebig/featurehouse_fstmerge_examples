

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.common.model.Unit;


public class AssignTradeRouteAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(AssignTradeRouteAction.class.getName());




    public static final String id = "assignTradeRouteAction";


    
    AssignTradeRouteAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.orders.assignTradeRoute", null, KeyStroke.getKeyStroke('A', 0));
    }

    
    public String getId() {
        return id;
    }

    
    protected boolean shouldBeEnabled() {
        if (super.shouldBeEnabled()) {
            GUI gui = getFreeColClient().getGUI();
            if (gui != null) {
                Unit unit = getFreeColClient().getGUI().getActiveUnit();
                return (unit != null && unit.isCarrier());
            }
        }
        return false;
    }

    
    public void actionPerformed(ActionEvent e) {
        Unit unit = getFreeColClient().getGUI().getActiveUnit();
        if (unit != null) {
            getFreeColClient().getInGameController().assignTradeRoute(unit);
        }
    }
}
