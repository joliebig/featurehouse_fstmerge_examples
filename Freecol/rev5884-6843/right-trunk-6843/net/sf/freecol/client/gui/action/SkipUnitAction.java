

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.model.Unit.UnitState;


public class SkipUnitAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SkipUnitAction.class.getName());

    public static final String id = "skipUnitAction";


    
    SkipUnitAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.1", null, KeyStroke.getKeyStroke(' ', 0));
        addImageIcons("done");
    }

    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled() && getFreeColClient().getGUI().getActiveUnit() != null;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        
        getFreeColClient().getInGameController().changeState(getFreeColClient().getGUI().getActiveUnit(), 
                                                             UnitState.SKIPPED);
    }
}
