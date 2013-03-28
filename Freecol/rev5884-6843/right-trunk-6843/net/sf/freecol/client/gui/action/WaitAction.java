

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class WaitAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(WaitAction.class.getName());

    public static final String id = "waitAction";


    
    WaitAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.0", null, KeyStroke.getKeyStroke('W', 0));
        addImageIcons("wait");
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().nextActiveUnit();
    }
}
