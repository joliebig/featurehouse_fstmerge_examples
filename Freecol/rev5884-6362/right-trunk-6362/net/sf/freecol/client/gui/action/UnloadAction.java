

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.panel.MapControls;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Unit;


public class UnloadAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(UnloadAction.class.getName());

    public static final String id = "unloadAction";

    
    public UnloadAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.orders.unload", null, KeyStroke.getKeyStroke('U', 0));
    }

    
    public String getId() {
        return id;
    }

    
    protected boolean shouldBeEnabled() {
        if (super.shouldBeEnabled()) {
            GUI gui = getFreeColClient().getGUI();
            if (gui != null) {
                Unit unit = getFreeColClient().getGUI().getActiveUnit();
                return (unit != null && unit.isCarrier() && unit.getGoodsCount() > 0);
            }
        }
        return false;
    }    
    
        
    public void actionPerformed(ActionEvent e) {
        Unit unit = getFreeColClient().getGUI().getActiveUnit();
        if (unit != null) {
            if (!unit.isInEurope() && unit.getColony() == null) {
                if (getFreeColClient().getCanvas().showConfirmDialog("dumpAllCargo", "yes", "no")) {
                    unloadAllCargo(unit);
                    MapControls controls = ((MapControlsAction) getFreeColClient().getActionManager().getFreeColAction(MapControlsAction.id)).getMapControls();
                    if (controls != null) {
                        controls.update();
                    }
                }
            } else {
                unloadAllCargo(unit);
                unloadAllUnits(unit);
                MapControls controls = ((MapControlsAction) getFreeColClient().getActionManager().getFreeColAction(MapControlsAction.id)).getMapControls();
                if (controls != null) {
                    controls.update();
                }
            }
        }
    }

    
    private void unloadAllUnits(Unit carrier) {
        for (Unit unit : new ArrayList<Unit>(carrier.getUnitList())) {
            getFreeColClient().getInGameController().leaveShip(unit);
        }
    }

    
    private void unloadAllCargo(Unit carrier) {
        Boolean dump = carrier.getColony() == null;
        for (Goods goods : new ArrayList<Goods>(carrier.getGoodsList())) {
            getFreeColClient().getInGameController().unloadCargo(goods, dump);
        }
    }

}
