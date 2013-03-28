



package net.sf.freecol.client.gui.action;

import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;



public abstract class MapboardAction extends FreeColAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MapboardAction.class.getName());


    
    protected MapboardAction(FreeColClient freeColClient, String id) {
        super(freeColClient, id);
    }

    protected MapboardAction(FreeColClient freeColClient, String name, String shortDescription, int mnemonic, KeyStroke accelerator) {
        super(freeColClient, name, shortDescription, mnemonic, accelerator);
    }
    
    protected MapboardAction(FreeColClient freeColClient, String name, String shortDescription, int mnemonic) {
        super(freeColClient, name, shortDescription, mnemonic);
    }

    protected MapboardAction(FreeColClient freeColClient, String name, String shortDescription, KeyStroke accelerator) {
        super(freeColClient, name, shortDescription, accelerator);
    }
    
    protected MapboardAction(FreeColClient freeColClient, String name, String shortDescription) {
        super(freeColClient, name, shortDescription);
    }
    
    
    protected boolean shouldBeEnabled() { 
        return super.shouldBeEnabled()  
            && getFreeColClient().getCanvas() != null
            && getFreeColClient().getCanvas().isMapboardActionsEnabled()
            && (getFreeColClient().getGame() == null
                || getFreeColClient().getGame().getCurrentPlayer() == getFreeColClient().getMyPlayer());
    }
}
