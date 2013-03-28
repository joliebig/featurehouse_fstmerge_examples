

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;


public class ImprovementAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ImprovementAction.class.getName());




    public ImprovementActionType iaType;
    
    int actionID;
    
    public ImprovementAction(FreeColClient freeColClient, ImprovementActionType iaType) {
        super(freeColClient, iaType.getNames().get(0), null, KeyStroke.getKeyStroke(iaType.getAccelerator(), 0));
        this.iaType = iaType;
        actionID = -1;
        updateValues(0);
    }

    
    private void updateValues(int newActionID) {
        if (actionID == newActionID) {
            return;
        }
        actionID = newActionID;

        putValue(BUTTON_IMAGE, getFreeColClient().getImageLibrary().getUnitButtonImageIcon(
                 iaType.getImageIDs().get(actionID), 0));
        putValue(BUTTON_ROLLOVER_IMAGE, getFreeColClient().getImageLibrary().getUnitButtonImageIcon(
                 iaType.getImageIDs().get(actionID), 1));
        putValue(BUTTON_PRESSED_IMAGE, getFreeColClient().getImageLibrary().getUnitButtonImageIcon(
                 iaType.getImageIDs().get(actionID), 2));
        putValue(BUTTON_DISABLED_IMAGE, getFreeColClient().getImageLibrary().getUnitButtonImageIcon(
                 iaType.getImageIDs().get(actionID), 3));
        putValue(NAME, Messages.message(iaType.getNames().get(actionID)));

    }

    
    public void update() {
        super.update();

        GUI gui = getFreeColClient().getGUI();
        if (gui != null) {
            Unit selectedOne = getFreeColClient().getGUI().getActiveUnit();
            if (enabled && selectedOne != null && selectedOne.getTile() != null) {
                Tile tile = selectedOne.getTile();
                int newActionID = 0;
                for (TileImprovementType impType : iaType.getImpTypes()) {
                	
                	
                	if (!impType.isTileAllowed(tile) || !impType.isWorkerAllowed(selectedOne)) {
                        continue;
                    }
                    newActionID = iaType.getImpTypes().indexOf(impType);
                    break;
                }
                updateValues(newActionID);
            } else {
                updateValues(0);
            }
        }
    }

    
    protected boolean shouldBeEnabled() {
        if (!super.shouldBeEnabled()) {
            return false;
        }

        GUI gui = getFreeColClient().getGUI();
        if (gui == null)
            return false;

        Unit selectedOne = getFreeColClient().getGUI().getActiveUnit();
        if (selectedOne == null || !selectedOne.checkSetState(UnitState.IMPROVING))
            return false;

        Tile tile = selectedOne.getTile();
        if (tile == null)
             return false;
        
        
        for (TileImprovementType impType : iaType.getImpTypes()) {
        	
        	
            if (!impType.isTileAllowed(tile) || !impType.isWorkerAllowed(selectedOne)) {
                continue;
            }
            return true;
        }
        
        return false;
    }

    
    public String getId() {
        return iaType.getId();
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getInGameController().changeWorkImprovementType(getFreeColClient().getGUI().getActiveUnit(),
                                                                iaType.getImpTypes().get(actionID));
        getFreeColClient().getInGameController().nextActiveUnit();
    }
}
