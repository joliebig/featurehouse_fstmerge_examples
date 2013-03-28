



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;



public class ChangeAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ChangeAction.class.getName());


    public static final String id = "changeAction";


    
    ChangeAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.orders.nextUnitOnTile", null, KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
    }
    
    
    
    public void update() {
        super.update();
        
        GUI gui = getFreeColClient().getGUI();
        if (gui != null) {
        if (getFreeColClient().getGUI().getActiveUnit() != null) {
            Unit unit = getFreeColClient().getGUI().getActiveUnit();
            if (unit.getTile() != null) {
                if (unit.getColony() != null) {
                    putValue(NAME, Messages.message("menuBar.orders.enterColony"));
                } else if (unit.isOnCarrier()) {
                    putValue(NAME, Messages.message("menuBar.orders.selectCarrier"));
                } else {
                    putValue(NAME, Messages.message("menuBar.orders.nextUnitOnTile"));
                }
            }
        }
        }
    }
    
    
    protected boolean shouldBeEnabled() {    
        if (!super.shouldBeEnabled()) {
            return false;
        }        
        GUI gui = getFreeColClient().getGUI();
        if (gui == null) return false;
        
        Unit unit = getFreeColClient().getGUI().getActiveUnit();
        if (unit == null) {
            return false;
        } else {
            return unit.getTile() != null;
        }
    }
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Unit unit = getFreeColClient().getGUI().getActiveUnit();
        Tile tile = unit.getTile();

        if (tile.getColony() != null) {
            getFreeColClient().getCanvas().showColonyPanel(tile.getColony());
        } else if (unit.isOnCarrier()) {
            getFreeColClient().getGUI().setActiveUnit(((Unit) unit.getLocation()));
        } else {
            Iterator<Unit> unitIterator = tile.getUnitIterator();
            boolean activeUnitFound = false;
            while (unitIterator.hasNext()) {
                Unit u = unitIterator.next();
                if (u == unit) {
                    activeUnitFound = true;
                } else if (activeUnitFound && u.getState() == UnitState.ACTIVE && u.getMovesLeft() > 0) {
                    getFreeColClient().getGUI().setActiveUnit(u);
                    return;
                }
            }
            unitIterator = tile.getUnitIterator();
            while (unitIterator.hasNext()) {
                Unit u = unitIterator.next();
                if (u == unit) {
                    return;
                } else if (u.getState() == UnitState.ACTIVE && u.getMovesLeft() > 0) {
                    getFreeColClient().getGUI().setActiveUnit(u);
                    return;
                }
            }
        }
    }
}
