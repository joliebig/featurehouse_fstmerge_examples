

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.common.model.Unit.UnitState;


public class FortifyAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(FortifyAction.class.getName());

    public static final String id = "fortifyAction";


    
    FortifyAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.2", null, KeyStroke.getKeyStroke('F', 0));
        addImageIcons("fortify");
    }

    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled() && getFreeColClient().getGUI().getActiveUnit() != null
                && getFreeColClient().getGUI().getActiveUnit().checkSetState(UnitState.FORTIFYING);
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().changeState(getFreeColClient().getGUI().getActiveUnit(),
                UnitState.FORTIFYING);
    }
}
