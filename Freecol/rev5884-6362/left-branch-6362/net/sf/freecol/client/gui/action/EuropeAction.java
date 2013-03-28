

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;


public class EuropeAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(EuropeAction.class.getName());




    public static final String id = "europeAction";


    
    EuropeAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.europe", null, KeyStroke.getKeyStroke('E', 0));
    }

    
    public String getId() {
        return id;
    }

    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled() && getFreeColClient().getMyPlayer() != null
                && getFreeColClient().getMyPlayer().getEurope() != null;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getCanvas().showEuropePanel();
    }
}
