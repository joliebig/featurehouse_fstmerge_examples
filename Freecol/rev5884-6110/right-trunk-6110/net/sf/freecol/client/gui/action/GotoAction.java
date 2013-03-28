

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.model.Unit;


public class GotoAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(GotoAction.class.getName());




    public static final String id = "gotoAction";


    
    GotoAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.orders.goto", null, KeyStroke.getKeyStroke('H', 0));
    }

    
    protected boolean shouldBeEnabled() {
        
        return getFreeColClient().getCanvas() != null && !getFreeColClient().getCanvas().isShowingSubPanel();
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Unit unit = getFreeColClient().getGUI().getActiveUnit();
        if (unit != null) {
            getFreeColClient().getInGameController().selectDestination(unit);
        }
    }
}
