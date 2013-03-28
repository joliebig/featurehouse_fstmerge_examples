

package net.sf.freecol.client.gui.action;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;


public class GotoTileAction extends MapboardAction {

    public static final String id = "gotoTileAction";
    
    
    @Override
    public String getId() {
        return id;
    }

        
    public void actionPerformed(ActionEvent e) {
        GUI gui = getFreeColClient().getCanvas().getGUI();

        
        if (gui.getActiveUnit() == null) {
            return;
        }
        
        
        if (!gui.isGotoStarted()) {
            gui.startGoto();

            
            Point pt = getFreeColClient().getCanvas().getMousePosition();
            if (pt != null) {
                Map map = getFreeColClient().getGame().getMap();
                Map.Position p = gui.convertToMapCoordinates(pt.x, pt.y);

                if (p != null && map.isValid(p)) {
                    Tile tile = map.getTile(p);
                    if (tile != null) {
                        if (gui.getActiveUnit().getTile() != tile) {
                            PathNode dragPath = gui.getActiveUnit().findPath(tile);
                            gui.setGotoPath(dragPath);
                        }
                    }
                }
            }
        } else {
            gui.stopGoto();
        }


    }
    
    
    GotoTileAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.11", null, KeyStroke.getKeyStroke('G', 0));
    }

    
    protected boolean shouldBeEnabled() {
        if (!super.shouldBeEnabled()) {
            return false;
        }
        Unit activeUnit = getFreeColClient().getGUI().getActiveUnit();
        return (activeUnit != null && activeUnit.getTile() != null);
    }
}
