

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class ExecuteGotoOrdersAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ExecuteGotoOrdersAction.class.getName());




    public static final String id = "executeGotoOrdersAction";


    
    ExecuteGotoOrdersAction(FreeColClient freeColClient) {
        super(freeColClient, "executeGotoOrders", null, KeyStroke.getKeyStroke('O', 0));
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().executeGotoOrders();
    }
}
