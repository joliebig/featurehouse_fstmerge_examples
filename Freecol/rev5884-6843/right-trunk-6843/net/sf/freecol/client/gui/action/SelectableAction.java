



package net.sf.freecol.client.gui.action;

import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.MapControls;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;


public abstract class SelectableAction extends MapboardAction {

    public static final String id = "selectableAction";

    protected boolean selected = false;

    
    protected SelectableAction(FreeColClient freeColClient, String id) {
        super(freeColClient, id);
    }

    protected SelectableAction(FreeColClient freeColClient, String name, String shortDescription, int mnemonic, KeyStroke accelerator) {
        super(freeColClient, name, shortDescription, mnemonic, accelerator);
    }
    
    protected SelectableAction(FreeColClient freeColClient, String name, String shortDescription, int mnemonic) {
        super(freeColClient, name, shortDescription, mnemonic);
    }

    protected SelectableAction(FreeColClient freeColClient, String name, String shortDescription, KeyStroke accelerator) {
        super(freeColClient, name, shortDescription, accelerator);
    }
    
    protected SelectableAction(FreeColClient freeColClient, String name, String shortDescription) {
        super(freeColClient, name, shortDescription);
    }

    
    public void update() {
        super.update();
        
        final Game game = getFreeColClient().getGame();
        final Player player = getFreeColClient().getMyPlayer();
        if (game != null && player != null && !player.getNewModelMessages().isEmpty()) {
            enabled = false;
        }
    }

    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean b) {
        this.selected = b;
    }
}
