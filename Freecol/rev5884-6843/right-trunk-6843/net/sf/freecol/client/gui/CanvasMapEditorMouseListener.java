

package net.sf.freecol.client.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.control.MapEditorController;
import net.sf.freecol.client.gui.panel.EditSettlementDialog;
import net.sf.freecol.client.gui.panel.MapEditorTransformPanel.TileTypeTransform;
import net.sf.freecol.client.gui.panel.RiverStylePanel;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.server.generator.TerrainGenerator;


public final class CanvasMapEditorMouseListener implements MouseListener, MouseMotionListener {

    private static final Logger logger = Logger.getLogger(CanvasMapEditorMouseListener.class.getName());

    private final Canvas canvas;

    private final GUI gui;

    private ScrollThread scrollThread;

    private static final int DRAG_SCROLLSPACE = 100;

    private static final int AUTO_SCROLLSPACE = 1;

    private Point oldPoint;
    private Point startPoint;

    
    public CanvasMapEditorMouseListener(Canvas canvas, GUI g) {
        this.canvas = canvas;
        gui = g;
        scrollThread = null;
    }
    
    
    
    private Map getMap() {
        Map map = null;
        if (canvas.getClient().getGame() != null)
            map = canvas.getClient().getGame().getMap();
        return map;
    }

    
    public void mouseClicked(MouseEvent e) {
        if (getMap() == null) {
            return;
        }
        try {
            if (e.getClickCount() > 1) {
                Position p = gui.convertToMapCoordinates(e.getX(), e.getY());
                gui.showColonyPanel(p);
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
        if (getMap() == null) {
            return;
        }
        try {
            if (e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger()) {
                Position p = gui.convertToMapCoordinates(e.getX(), e.getY());
                Tile tile = getMap().getTile(p);
                if (tile != null) {
                    if (tile.hasRiver()) {
                        TileImprovement river = tile.getRiver();
                        int style = canvas.showFreeColDialog(new RiverStylePanel(canvas));
                        if (style == -1) {
                            
                        } else if (style == 0) {
                            tile.getTileItemContainer().removeTileItem(river);
                        } else if (0 < style && style < ResourceManager.RIVER_STYLES) {
                            river.setStyle(style);
                        } else {
                            logger.warning("Unknown river style: " + style);
                        }
                    }
                    if (tile.getSettlement() instanceof IndianSettlement) {
                        IndianSettlement settlement = (IndianSettlement) tile.getSettlement();
                        canvas.showFreeColDialog(new EditSettlementDialog(canvas, settlement));
                    }
                } else {
                    gui.setSelectedTile(p, true);
                }
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                startPoint = e.getPoint();
                oldPoint = e.getPoint();
                JComponent component = (JComponent)e.getSource();
                drawBox(component, startPoint, oldPoint);
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Error in mousePressed!", ex);
        }
    }

    
    public void mouseReleased(MouseEvent e) {
        if (getMap() == null) {
            return;
        }
        JComponent component = (JComponent)e.getSource();
        
        MapEditorController controller = canvas.getClient().getMapEditorController();
        boolean isTransformActive = controller.getMapTransform() != null; 
        
        if(startPoint == null){
        	startPoint = e.getPoint();
        }
        if(oldPoint == null){
        	oldPoint = e.getPoint();
        }
        drawBox(component, startPoint, oldPoint);
        if (gui.getFocus() != null) {	
            Position start = gui.convertToMapCoordinates(startPoint.x, startPoint.y);
            Position end = start;
            
            if(startPoint.x != oldPoint.x || startPoint.y != oldPoint.y){
            	end = gui.convertToMapCoordinates(oldPoint.x, oldPoint.y);
            }
            
            
            if(!isTransformActive){
            	gui.setFocus(end);
            	return;
            }
            
            
            int min_x, max_x, min_y, max_y;
            if (start.x < end.x) {
                min_x = start.x;
                max_x = end.x;
            } else {
                min_x = end.x;
                max_x = start.x;
            }
            if (start.y < end.y) {
                min_y = start.y;
                max_y = end.y;
            } else {
                min_y = end.y;
                max_y = start.y;
            }
            
            
            Tile t = null;
            for (int x = min_x; x <= max_x; x++) {
                for (int y = min_y; y <= max_y; y++) {
                    t = getMap().getTile(x, y);
                    if (t != null) {
                        controller.transform(t);
                    }
                }
            }
            if (controller.getMapTransform() instanceof TileTypeTransform) {
                for (int x = min_x - 2; x <= max_x + 2; x++) {
                    for (int y = min_y - 2; y <= max_y + 2; y++) {
                        t = getMap().getTile(x, y);
                        if (t != null && t.getType().isWater()) {
                            TerrainGenerator.encodeStyle(t);
                        }
                    }
                }
            }
            canvas.refresh();
            canvas.requestFocus();
        }
    }

    
    public void mouseMoved(MouseEvent e) {
        if (getMap() == null) {
            return;
        }
		
        if (e.getComponent().isEnabled() && 
            canvas.getClient().getClientOptions().getBoolean(ClientOptions.AUTO_SCROLL)) {
            auto_scroll(e.getX(), e.getY());
        } else if (scrollThread != null) {
            scrollThread.stopScrolling();
            scrollThread = null;
        }
    }
	
    
    public void mouseDragged(MouseEvent e) {
        if (getMap() == null) {
            return;
        }
		
        JComponent component = (JComponent)e.getSource();
        drawBox(component, startPoint, oldPoint);
        oldPoint = e.getPoint();
        drawBox(component, startPoint, oldPoint);
        Map.Position p = gui.convertToMapCoordinates(e.getX(), e.getY());

        if (e.getComponent().isEnabled() &&
            canvas.getClient().getClientOptions().getBoolean(ClientOptions.MAP_SCROLL_ON_DRAG)) {
            drag_scroll(e.getX(), e.getY());
        } else if (scrollThread != null) {
            scrollThread.stopScrolling();
            scrollThread = null;
        }
        canvas.refresh();
    }
	
    private void drawBox(JComponent component, Point startPoint, Point endPoint) {
        if(startPoint == null || endPoint == null){
        	return;
        }
        if(startPoint.distance(endPoint) == 0){
        	return;
        }
        
        
        MapEditorController controller = canvas.getClient().getMapEditorController();
        if(controller.getMapTransform() == null){ 
        	return;
        }
    	
    	Graphics2D graphics = (Graphics2D) component.getGraphics ();
        graphics.setColor(Color.WHITE);
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(startPoint.x - endPoint.x);
        int height = Math.abs(startPoint.y - endPoint.y);
        graphics.drawRect(x, y, width, height);
    }

    private void auto_scroll(int x, int y){
        scroll(x, y, AUTO_SCROLLSPACE);
    }
	
    private void drag_scroll(int x, int y){
        scroll(x, y, DRAG_SCROLLSPACE);
    }

    private void scroll(int x, int y, int scrollspace) {
        if (getMap() == null) {
            return;
        }
		
        
		 
        Direction direction;
        if ((x < scrollspace) && (y < scrollspace)) {
            
            direction = Direction.NW;
        } else if ((x >= gui.getWidth() - scrollspace) && (y < scrollspace)) {
            
            direction = Direction.NE;
        } else if ((x >= gui.getWidth() - scrollspace) && (y >= gui.getHeight() - scrollspace)) {
            
            direction = Direction.SE;
        } else if ((x < scrollspace) && (y >= gui.getHeight() - scrollspace)) {
            
            direction = Direction.SW;
        } else if (y < scrollspace) {
            
            direction = Direction.N;
        } else if (x >= gui.getWidth() - scrollspace) {
            
            direction = Direction.E;
        } else if (y >= gui.getHeight() - scrollspace) {
            
            direction = Direction.S;
        } else if (x < scrollspace) {
            
            direction = Direction.W;
        } else {
            
            if (scrollThread != null) {
                scrollThread.stopScrolling();
                scrollThread = null;
            }
            return;
        }

        if (scrollThread != null) {
            
            scrollThread.setDirection(direction);
        } else {
            
            scrollThread = new ScrollThread(getMap(), gui);
            scrollThread.setDirection(direction);
            scrollThread.start();
        }
    }


    
    private class ScrollThread extends Thread {

        private final Map map;

        private final GUI gui;

        private Direction direction;

        private boolean cont;


        
        public ScrollThread(Map m, GUI g) {
            super(FreeCol.CLIENT_THREAD+"Mouse scroller");
            map = m;
            gui = g;
            cont = true;
        }

        
        public void setDirection(Direction d) {
            direction = d;
        }

        
        public void stopScrolling() {
            cont = false;
        }

        
        public void run() {
            do {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                }

                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                try {
                                    int x, y;
                                    Tile t = map.getTile(gui.getFocus().getX(), gui.getFocus().getY());
                                    if (t == null) {
                                        return;
                                    }

                                    t = map.getNeighbourOrNull(direction, t);
                                    if (t == null) {
                                        return;
                                    }

                                    if (gui.isMapNearTop(t.getY()) && gui.isMapNearTop(gui.getFocus().getY())) {
                                        if (t.getY() > gui.getFocus().getY()) {
                                            y = t.getY();
                                            do {
                                                y += 2;
                                            } while (gui.isMapNearTop(y));
                                        } else {
                                            y = gui.getFocus().getY();
                                        }
                                    } else if (gui.isMapNearBottom(t.getY()) && gui.isMapNearBottom(gui.getFocus().getY())) {
                                        if (t.getY() < gui.getFocus().getY()) {
                                            y = t.getY();
                                            do {
                                                y -= 2;
                                            } while (gui.isMapNearBottom(y));
                                        } else {
                                            y = gui.getFocus().getY();
                                        }
                                    } else {
                                        y = t.getY();
                                    }

                                    if (gui.isMapNearLeft(t.getX(), t.getY())
                                        && gui.isMapNearLeft(gui.getFocus().getX(), gui.getFocus().getY())) {
                                        if (t.getX() > gui.getFocus().getX()) {
                                            x = t.getX();
                                            do {
                                                x++;
                                            } while (gui.isMapNearLeft(x, y));
                                        } else {
                                            x = gui.getFocus().getX();
                                        }
                                    } else if (gui.isMapNearRight(t.getX(), t.getY())
                                               && gui.isMapNearRight(gui.getFocus().getX(), gui.getFocus().getY())) {
                                        if (t.getX() < gui.getFocus().getX()) {
                                            x = t.getX();
                                            do {
                                                x--;
                                            } while (gui.isMapNearRight(x, y));
                                        } else {
                                            x = gui.getFocus().getX();
                                        }
                                    } else {
                                        x = t.getX();
                                    }

                                    gui.setFocus(x, y);
                                } catch (Exception e) {
                                    logger.log(Level.WARNING, "Exception while scrolling!", e);
                                }
                            }
                        });
                } catch (InvocationTargetException e) {
                    logger.log(Level.WARNING, "Scroll thread caught error", e);
                    cont = false;
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Scroll thread interrupted", e);
                    cont = false;
                }
            } while (cont);
        }
    }
}
