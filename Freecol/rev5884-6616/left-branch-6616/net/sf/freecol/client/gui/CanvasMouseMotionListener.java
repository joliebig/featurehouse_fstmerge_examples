

package net.sf.freecol.client.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;


public final class CanvasMouseMotionListener implements MouseMotionListener {

    private static final Logger logger = Logger.getLogger(CanvasMouseMotionListener.class.getName());

    
    
    private Tile lastTile;

    private final Canvas canvas;

    private final GUI gui;

    private final Map map;

    private ScrollThread scrollThread;

    
    private static final int DRAG_SCROLLSPACE = 100;
	
	private static final int AUTO_SCROLLSPACE = 1;


    
    public CanvasMouseMotionListener(Canvas canvas, GUI g, Map m) {
        this.canvas = canvas;
        gui = g;
        map = m;
        scrollThread = null;
    }

    
    public void mouseMoved(MouseEvent e) {
      
    	if (e.getComponent().isEnabled() && 
   			canvas.getClient().getClientOptions().getBoolean(ClientOptions.AUTO_SCROLL)) {
				auto_scroll(e.getX(), e.getY());
        } else if (scrollThread != null) {
            scrollThread.stopScrolling();
            scrollThread = null;
        }

        if (gui.isGotoStarted()) {
            if (gui.getActiveUnit() == null) {
                gui.stopGoto();
            }
            
            Map.Position p = gui.convertToMapCoordinates(e.getX(), e.getY());

            if (p == null || !map.isValid(p)) {
                return;
            }
        
            Tile tile = map.getTile(p);
            if (tile != null) {
                if (lastTile != tile) {
                    lastTile = tile;
                    if (gui.getActiveUnit().getTile() != tile) {
                        PathNode dragPath = gui.getActiveUnit().findPath(tile);
                        gui.setGotoPath(dragPath);
                    } else {
                        gui.setGotoPath(null);
                    }
                } 
            }
        }
    }
	
	
    public void mouseDragged(MouseEvent e) {
        Map.Position p = gui.convertToMapCoordinates(e.getX(), e.getY());

        if (e.getComponent().isEnabled() &&
			 canvas.getClient().getClientOptions().getBoolean(ClientOptions.MAP_SCROLL_ON_DRAG)) {
				drag_scroll(e.getX(), e.getY());
        } else if (scrollThread != null) {
            scrollThread.stopScrolling();
            scrollThread = null;
        }

        if (p == null || !map.isValid(p)) {
            return;
        }

        Tile tile = map.getTile(p);
        if (tile != null && 
            (e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) {
            
            if (gui.isGotoStarted()) {
                if (gui.getActiveUnit() == null) {
                    gui.stopGoto();
                } else { 
                    if (lastTile != tile) {
                        lastTile = tile;
                        if (gui.getActiveUnit().getTile() != tile) {
                            PathNode dragPath = gui.getActiveUnit().findPath(tile);
                            
                            
                            
                            
                            gui.setGotoPath(dragPath);
                        } else {
                            gui.setGotoPath(null);
                        }
                    }
                }
            } else {
                gui.startGoto();
            }
        }
    }
	
	private void auto_scroll(int x, int y){
		scroll(x, y, AUTO_SCROLLSPACE);
	}
	
	private void drag_scroll(int x, int y){
		scroll(x, y, DRAG_SCROLLSPACE);
	}

	private void scroll(int x, int y, int scrollspace) {
		
		
		
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
            
            scrollThread = new ScrollThread(map, gui);
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
