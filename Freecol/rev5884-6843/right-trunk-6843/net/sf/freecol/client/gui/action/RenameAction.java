

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class RenameAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(RenameAction.class.getName());

    public static final String id = "renameAction";


    
    RenameAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.orders.rename", null, KeyStroke.getKeyStroke('N', 0));
    }

    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled() && getFreeColClient().getGUI().getActiveUnit() != null;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().rename(getFreeColClient().getGUI().getActiveUnit());
    }
}
