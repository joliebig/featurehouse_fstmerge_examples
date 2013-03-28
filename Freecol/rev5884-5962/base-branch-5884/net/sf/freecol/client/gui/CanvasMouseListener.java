

package net.sf.freecol.client.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.control.InGameController;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;


public final class CanvasMouseListener implements MouseListener {

    private static final Logger logger = Logger.getLogger(CanvasMouseListener.class.getName());

    private final Canvas canvas;

    private final GUI gui;

    
    public CanvasMouseListener(Canvas canvas, GUI g) {
        this.canvas = canvas;
        gui = g;
    }

    
    public void mouseClicked(MouseEvent e) {
        try {
            if (e.getClickCount() > 1) {
                Map.Position position = gui.convertToMapCoordinates(e.getX(), e.getY());
                if (FreeCol.isInDebugMode()) {
                    Tile tile = canvas.getClient().getGame().getMap().getTile(position);
                    if (tile.getSettlement() != null) {
                        canvas.getClient().getInGameController().debugForeignColony(tile);
                    }
                } else {
                    gui.showColonyPanel(position);
                }
            } else {
                canvas.requestFocus();
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Error in mouseClicked!", ex);
        }
    }

    
    public void mouseEntered(MouseEvent e) {
        
    }

    
    public void mouseExited(MouseEvent e) {
        
    }

    
    public void mousePressed(MouseEvent e) {
        if (!e.getComponent().isEnabled()) {
            return;
        }
        try {
            if (e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger()) {
                
                if (gui.isGotoStarted()) {
                    gui.stopGoto();
                }
                
                canvas.showTilePopup(gui.convertToMapCoordinates(e.getX(), e.getY()), e.getX(), e.getY());
            } else if (e.getButton() == MouseEvent.BUTTON2) {
                Map.Position p = gui.convertToMapCoordinates(e.getX(), e.getY());
                if (p == null || !canvas.getClient().getGame().getMap().isValid(p)) {
                    return;
                }

                Tile tile = canvas.getClient().getGame().getMap().getTile(p);
                if (tile != null) {
                    Unit unit = gui.getActiveUnit();
                    if (unit != null && unit.getTile() != tile) {
                        PathNode dragPath = unit.findPath(tile);
                        gui.startGoto();
                        gui.setGotoPath(dragPath);
                    }
                }
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                if (gui.isGotoStarted()) {
                    PathNode path = gui.getGotoPath();
                    if (path != null) {
                        gui.stopGoto();
                        
                        Unit unit = gui.getActiveUnit();
                        canvas.getClient().getInGameController().setDestination(unit, path.getLastNode().getTile());
                        if (canvas.getClient().getGame().getCurrentPlayer() == canvas.getClient().getMyPlayer()) {
                            canvas.getClient().getInGameController().moveToDestination(unit);
                        }
                    }
                } else {
                    gui.setSelectedTile(gui.convertToMapCoordinates(e.getX(), e.getY()), true);
                }
                canvas.requestFocus();
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Error in mousePressed!", ex);
        }
    }

    
    public void mouseReleased(MouseEvent e) {
        try {
            if (gui.getGotoPath() != null) {
                

                PathNode temp = gui.getGotoPath();

                gui.stopGoto();

                
                Unit unit = gui.getActiveUnit();
                InGameController ctlr = canvas.getClient().getInGameController();
                ctlr.setDestination(unit, temp.getLastNode().getTile());
                if (canvas.getClient().getGame().getCurrentPlayer() == canvas.getClient().getMyPlayer()) {
                    ctlr.moveToDestination(unit);
                    ctlr.nextActiveUnit();
                }
                
            } else if (gui.isGotoStarted()) {
                gui.stopGoto();
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Error in mouseReleased!", ex);
        }
    }
}
