

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.common.model.Unit;


public class BuildColonyAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(BuildColonyAction.class.getName());

    public static final String id = "buildColonyAction";


    
    BuildColonyAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.7", null, KeyStroke.getKeyStroke('B', 0));
        addImageIcons("build");
    }

    
    protected boolean shouldBeEnabled() {
        if (!super.shouldBeEnabled()) {
            return false;
        }
        Unit selectedOne = getFreeColClient().getGUI().getActiveUnit();
        return selectedOne != null && selectedOne.getTile() != null
            && (selectedOne.canBuildColony()
                || (selectedOne.getTile().getColony() != null
                    
                    && selectedOne.getType().hasSkill()));
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().buildColony();
    }
}
