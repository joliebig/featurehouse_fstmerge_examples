

package net.sf.freecol.client.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.UIManager;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.panel.IndianSettlementPanel;
import net.sf.freecol.client.gui.panel.MapControls;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.LostCityRumour;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Resource;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileItem;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.resources.ResourceManager;




public final class GUI {

    private static final Logger logger = Logger.getLogger(GUI.class.getName());
    
    
    @SuppressWarnings("serial")
    static class GrayLayer extends Component {

        
        private static final Color MASK_COLOR = new Color(0f, 0f, 0f, .6f);
        
        private static final int DEFAULT_FONT_SIZE = 18;
        
        private static final int FONT_SIZE_DECREMENT = 2;
        
        private static final int MAX_TEXT_WIDTH = 640;

        
        private ImageLibrary imageLibrary;
        
        private Player player;

        public GrayLayer(ImageLibrary imageLibrary) {
            this.imageLibrary = imageLibrary;
        }

        
        public void paint(Graphics g) {
            Rectangle clipArea = g.getClipBounds();
            if (clipArea == null) {
                clipArea = getBounds();
                clipArea.x = clipArea.y = 0;
            }
            if (clipArea.isEmpty()) {
                
                return;
            }
            g.setColor(MASK_COLOR);
            g.fillRect(clipArea.x, clipArea.y, clipArea.width, clipArea.height);

            if (player == null) {
                
                return;
            }

            ImageIcon coatOfArmsIcon = imageLibrary
                    .getCoatOfArmsImageIcon(player.getNation());

            Rectangle iconBounds = new Rectangle();
            if (coatOfArmsIcon != null) {
                iconBounds.width = coatOfArmsIcon.getIconWidth();
                iconBounds.height = coatOfArmsIcon.getIconHeight();
            }

            Font nameFont = getFont();
            FontMetrics nameFontMetrics = getFontMetrics(nameFont);
            String message = Messages.message("waitingFor", "%nation%", player
                    .getNationAsString());

            Rectangle textBounds;
            int fontSize = DEFAULT_FONT_SIZE;
            int maxWidth = Math.min(MAX_TEXT_WIDTH, getSize().width);
            do {
                nameFont = nameFont.deriveFont(Font.BOLD, fontSize);
                nameFontMetrics = getFontMetrics(nameFont);
                textBounds = nameFontMetrics.getStringBounds(message, g)
                        .getBounds();
                fontSize -= FONT_SIZE_DECREMENT;
            } while (textBounds.width > maxWidth);

            Dimension size = getSize();
            textBounds.x = (size.width - textBounds.width) / 2;
            textBounds.y = (size.height - textBounds.height - iconBounds.height) / 2;

            iconBounds.x = (size.width - iconBounds.width) / 2;
            iconBounds.y = (size.height - iconBounds.height) / 2
                    + textBounds.height;

            if (textBounds.intersects(clipArea)) {
                
                g.setFont(nameFont);
                g.setColor(player.getColor());
                g.drawString(message, textBounds.x, textBounds.y
                        + textBounds.height);
            }
            if (coatOfArmsIcon != null && iconBounds.intersects(clipArea)) {
                
                coatOfArmsIcon.paintIcon(this, g, iconBounds.x, iconBounds.y);
            }
        }

        
        public void setPlayer(Player player) {
            if (this.player == player) {
                return;
            }
            this.player = player;
            repaint();
        }
    }

    private final FreeColClient freeColClient;
    private Dimension size;
    private ImageLibrary lib;
    private TerrainCursor cursor;
    private ViewMode viewMode;

    
    private boolean inGame;

    private final Vector<GUIMessage> messages;

    private Map.Position selectedTile;
    private Map.Position focus = null;
    private Unit activeUnit;

    
    private PathNode currentPath;
    private PathNode gotoPath = null;
    private boolean gotoStarted = false;

    
    private int tileHeight,
    tileWidth,
    topSpace,
    topRows,
    
    bottomRows,
    leftSpace,
    rightSpace;

    
    private int bottomRow = -1;
    
    private int topRow;
    
    
    private int bottomRowY;
    
    
    private int topRowY;

    
    private int leftColumn;
    
    private int rightColumn;
    
    
    private int leftColumnX;

    
    public static final int UNIT_OFFSET = 20,
    TEXT_OFFSET_X = 2, 
    TEXT_OFFSET_Y = 13, 
    STATE_OFFSET_X = 25,
    STATE_OFFSET_Y = 10,
    ALARM_OFFSET_X = 37,
    ALARM_OFFSET_Y = 10,
    RUMOUR_OFFSET_X = 40,
    RUMOUR_OFFSET_Y = 5,
    MISSION_OFFSET_X = 49,
    MISSION_OFFSET_Y = 10,
    OTHER_UNITS_OFFSET_X = -5, 
    OTHER_UNITS_OFFSET_Y = 1,
    OTHER_UNITS_WIDTH = 3,
    MAX_OTHER_UNITS = 10,
    MESSAGE_COUNT = 3,
    MESSAGE_AGE = 30000; 

    public static final int OVERLAY_INDEX = 100;
    public static final int FOREST_INDEX = 200;

    private int displayTileText = 0;
    private GeneralPath gridPath = null;

    
    public boolean displayCoordinates = false;
    public boolean displayColonyValue = false;
    public Player displayColonyValuePlayer = null;

    public boolean debugShowMission = false;
    public boolean debugShowMissionInfo = false;
    
    private volatile boolean blinkingMarqueeEnabled;
    
    private Image cursorImage;
    
    private GrayLayer greyLayer;
    
    private java.util.Map<Unit, Integer> unitsOutForAnimation;
    private java.util.Map<Unit, JLabel> unitsOutForAnimationLabels;

    
    private static final Direction[] borderDirections =
        new Direction[] { Direction.NW, Direction.NE, Direction.SE, Direction.SW };
    private EnumMap<Direction, Dimension> borderPoints =
        new EnumMap<Direction, Dimension>(Direction.class);
    private EnumMap<Direction, Dimension> controlPoints =
        new EnumMap<Direction, Dimension>(Direction.class);
    private Stroke borderStroke = new BasicStroke(4);


    
    public GUI(FreeColClient freeColClient, Dimension size, ImageLibrary lib) {
        this.freeColClient = freeColClient;
        this.size = size;

        setImageLibrary(lib);
        
        unitsOutForAnimation = new HashMap<Unit, Integer>();
        unitsOutForAnimationLabels = new HashMap<Unit, JLabel>();

        inGame = false;
        logger.info("GUI created.");
        messages = new Vector<GUIMessage>(MESSAGE_COUNT);
        viewMode = new ViewMode(this);
        logger.info("Starting in Move Units View Mode");
        displayTileText = freeColClient.getClientOptions().getDisplayTileText();
        blinkingMarqueeEnabled = true;

        cursor = new net.sf.freecol.client.gui.TerrainCursor();

    }
    
    public void setImageLibrary(ImageLibrary lib) {
        this.lib = lib;
        cursorImage = lib.getMiscImage(ImageLibrary.UNIT_SELECT);
        
        TileType tileType = FreeCol.getSpecification().getTileTypeList().get(0);
        tileHeight = lib.getTerrainImageHeight(tileType);
        tileWidth = lib.getTerrainImageWidth(tileType);

        int dx = tileWidth/16;
        int dy = tileHeight/16;
        int ddx = dx + dx/2;
        int ddy = dy + dy/2;

        
        controlPoints.put(Direction.N, new Dimension(tileWidth/2, dy));
        controlPoints.put(Direction.E, new Dimension(tileWidth - dx, tileHeight/2));
        controlPoints.put(Direction.S, new Dimension(tileWidth/2, tileHeight - dy));
        controlPoints.put(Direction.W, new Dimension(dx, tileHeight/2));
        
        controlPoints.put(Direction.SE, new Dimension(tileWidth/2, tileHeight));
        controlPoints.put(Direction.NE, new Dimension(tileWidth, tileHeight/2));
        controlPoints.put(Direction.SW, new Dimension(0, tileHeight/2));
        controlPoints.put(Direction.NW, new Dimension(tileWidth/2, 0));
        
        borderPoints.put(Direction.NW, new Dimension(dx + ddx, tileHeight/2 - ddy));
        borderPoints.put(Direction.N, new Dimension(tileWidth/2 - ddx, dy + ddy));
        borderPoints.put(Direction.NE, new Dimension(tileWidth/2 + ddx, dy + ddy));
        borderPoints.put(Direction.E, new Dimension(tileWidth - dx - ddx, tileHeight/2 - ddy));
        borderPoints.put(Direction.SE, new Dimension(tileWidth - dx - ddx, tileHeight/2 + ddy));
        borderPoints.put(Direction.S, new Dimension(tileWidth/2 + ddx, tileHeight - dy - ddy));
        borderPoints.put(Direction.SW, new Dimension(tileWidth/2 - ddx, tileHeight - dy - ddy));
        borderPoints.put(Direction.W, new Dimension(dx + ddx, tileHeight/2 + ddy));

        borderStroke = new BasicStroke(dy);

        updateMapDisplayVariables();
    }
    
    private boolean isOutForAnimation(final Unit unit) {
        return unitsOutForAnimation.containsKey(unit);
    }
    
    
    public void executeWithUnitOutForAnimation(final Unit unit,
                                               final Tile sourceTile,
                                               final OutForAnimationCallback r) {
        final JLabel unitLabel = enterUnitOutForAnimation(unit, sourceTile);
        try {
            r.executeWithUnitOutForAnimation(unitLabel);
        } finally {
            releaseUnitOutForAnimation(unit);
        }
    }
    
    private JLabel enterUnitOutForAnimation(final Unit unit, final Tile sourceTile) {
        Integer i = unitsOutForAnimation.get(unit);
        if (i == null) {
            final JLabel unitLabel = getUnitLabel(unit);
            final Integer UNIT_LABEL_LAYER = JLayeredPane.DEFAULT_LAYER;

            i = 1;
            unitLabel.setLocation(getUnitLabelPositionInTile(unitLabel,
                    getTilePosition(sourceTile)));
            unitsOutForAnimationLabels.put(unit, unitLabel);
            freeColClient.getCanvas().add(unitLabel, UNIT_LABEL_LAYER, false);
        } else {
            i++;
        }
        unitsOutForAnimation.put(unit, i);
        return unitsOutForAnimationLabels.get(unit);
    }
    
    private void releaseUnitOutForAnimation(final Unit unit) {
        Integer i = unitsOutForAnimation.get(unit);
        if (i == null) {
            throw new IllegalStateException("Tried to release unit that was not out for animation"); 
        }
        if (i == 1) {
            unitsOutForAnimation.remove(unit);
            freeColClient.getCanvas().remove(unitsOutForAnimationLabels.remove(unit), false);
        } else {
            i--;
            unitsOutForAnimation.put(unit, i); 
        }
    }

    
    private JLabel getUnitLabel(Unit unit) {
        final Image unitImg = lib.getUnitImageIcon(unit).getImage();
        

        final int width = tileWidth/2 + unitImg.getWidth(null)/2;
        final int height = unitImg.getHeight(null);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();

        final int unitX = (width - unitImg.getWidth(null)) / 2;
        g.drawImage(unitImg, unitX, 0, null);

        
        
        

        final JLabel label = new JLabel(new ImageIcon(img));
        label.setSize(width, height);
        return label;
    }

    private void updateMapDisplayVariables() {
        
        topSpace = (size.height - tileHeight) / 2;
        if ((topSpace % (tileHeight / 2)) != 0) {
            topRows = topSpace / (tileHeight / 2) + 2;
        } else {
            topRows = topSpace / (tileHeight / 2) + 1;
        }
        bottomRows = topRows;
        leftSpace = (size.width - tileWidth) / 2;
        rightSpace = leftSpace;
    }
    
    
    public ViewMode getViewMode(){
        return viewMode;
    }
    
    
    public void startCursorBlinking() {
        
        final FreeColClient theFreeColClient = freeColClient; 
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (!blinkingMarqueeEnabled) return;
                if (getActiveUnit() != null && getActiveUnit().getTile() != null) {
                    
                    theFreeColClient.getCanvas().refreshTile(getActiveUnit().getTile());            
                }               
            }
        };
        
        cursor.addActionListener(taskPerformer);
        
        cursor.startBlinking();
    }
    
    public TerrainCursor getCursor(){
        return cursor;
    }
    
    public void setSize(Dimension size) {
        this.size = size;
        updateMapDisplayVariables();
    }
    
    public void moveTileCursor(Direction direction){
        Tile selectedTile = freeColClient.getGame().getMap().getTile(getSelectedTile());
        if(selectedTile != null){   
            Tile newTile = freeColClient.getGame().getMap().getNeighbourOrNull(direction, selectedTile);
            if(newTile != null)
                setSelectedTile(newTile.getPosition());
        }
        else{
            logger.warning("selectedTile is null");
        }
    }

    
    public void setSelectedTile(Position selectedTile) {
        setSelectedTile(selectedTile, false);
    }

    
    public void setSelectedTile(Position selectedTile, boolean clearGoToOrders) {
        Game gameData = freeColClient.getGame();

        if (selectedTile != null && !gameData.getMap().isValid(selectedTile)) {
            return;
        }

        Position oldPosition = this.selectedTile;

        this.selectedTile = selectedTile;

        if (viewMode.getView() == ViewMode.MOVE_UNITS_MODE) {
            if (activeUnit == null ||
                (activeUnit.getTile() != null &&
                 !activeUnit.getTile().getPosition().equals(selectedTile))) {
                Tile t = gameData.getMap().getTile(selectedTile);
                if (t != null && t.getSettlement() != null) {
                    Settlement s = t.getSettlement();
                    if (s instanceof Colony) {
                        if (s.getOwner().equals(freeColClient.getMyPlayer())) {
                            
                            setFocus(selectedTile);
                            freeColClient.getCanvas().showColonyPanel((Colony) s);
                        } else if (FreeCol.isInDebugMode()) {
                            freeColClient.getInGameController().debugForeignColony(t);
                        }
                        return;
                    } else if (s instanceof IndianSettlement) {
                        
                        setFocus(selectedTile);
                        Canvas canvas =freeColClient.getCanvas();
                        canvas.showPanel(new IndianSettlementPanel(canvas, (IndianSettlement) s));
                        return;
                    }
                }

                
                Unit unitInFront = getUnitInFront(gameData.getMap().getTile(selectedTile));
                if (unitInFront != null) {
                    setActiveUnit(unitInFront);
                    updateGotoPathForActiveUnit();
                } else {
                    setFocus(selectedTile);
                }
            } else if (activeUnit.getTile() != null &&
                    activeUnit.getTile().getPosition().equals(selectedTile)) {
                
                if (clearGoToOrders && activeUnit.getDestination() != null) {
                    freeColClient.getInGameController().clearGotoOrders(activeUnit);
                    updateGotoPathForActiveUnit();
                }
            }
        }
        
        freeColClient.getActionManager().update();
        freeColClient.updateMenuBar();

        int x = 0, y = 0;
        MapControls mapControls = freeColClient.getCanvas().getMapControls();
        if (mapControls != null) {
            x = getWidth() - mapControls.getInfoPanelWidth();
            y = getHeight() - mapControls.getInfoPanelHeight();
        }
        freeColClient.getCanvas().repaint(x, y, getWidth(), getHeight());

        
        if ((!onScreen(selectedTile) &&
             freeColClient.getClientOptions().getBoolean(ClientOptions.JUMP_TO_ACTIVE_UNIT)) ||
            freeColClient.getClientOptions().getBoolean(ClientOptions.ALWAYS_CENTER)) {
                setFocus(selectedTile);
        } else {
            if (oldPosition != null) {
                freeColClient.getCanvas().refreshTile(oldPosition);
            }

            if (selectedTile != null) {
                freeColClient.getCanvas().refreshTile(selectedTile);
            }
        }
    }

    public void showColonyPanel(Position selectedTile) {
        Game gameData = freeColClient.getGame();

        if (selectedTile != null && !gameData.getMap().isValid(selectedTile)) {
            return;
        }

        if (viewMode.getView() == ViewMode.MOVE_UNITS_MODE) {
            Tile t = gameData.getMap().getTile(selectedTile);
            if (t != null && t.getSettlement() != null && t.getSettlement() instanceof Colony) {
                if (t.getSettlement().getOwner().equals(freeColClient.getMyPlayer())) {
                    setFocus(selectedTile);
                    stopBlinking();
                    freeColClient.getCanvas().showColonyPanel((Colony) t.getSettlement());
                }
            }
        }
    }
    
    public void restartBlinking() {
        blinkingMarqueeEnabled = true;
    }
    
    public void stopBlinking() {
        blinkingMarqueeEnabled = false;
    }

    
    public Unit getUnitInFront(Tile unitTile) {
        if (unitTile == null || unitTile.getUnitCount() <= 0) {
            return null;
        }

        if (activeUnit != null && activeUnit.getTile() == unitTile) {
            return activeUnit;
        } else {
            if (unitTile.getSettlement() == null) {
                Unit bestDefendingUnit = null;
                if (activeUnit != null) {
                    bestDefendingUnit = unitTile.getDefendingUnit(activeUnit);
                    if (bestDefendingUnit != null) {
                        return bestDefendingUnit;
                    }
                }
                
                Unit movableUnit = unitTile.getMovableUnit();
                if (movableUnit != null && movableUnit.getLocation() == movableUnit.getTile()) {
                    return movableUnit;
                } else {
                    Unit bestPick = null;
                    Iterator<Unit> unitIterator = unitTile.getUnitIterator();
                    while (unitIterator.hasNext()) {
                        Unit u = unitIterator.next();
                        if (bestPick == null || bestPick.getMovesLeft() < u.getMovesLeft()) {
                            bestPick = u;
                        }
                    }
                    
                    return bestPick;
                }
            } else {
                return null;
            }
        }
    }

    
    
    public Position getSelectedTile() {
        return selectedTile;
    }


    
    public Unit getActiveUnit() {
        return activeUnit;
    }


    
    public void setActiveUnit(Unit activeUnit) {
        
        
        
        
        if (activeUnit != null && activeUnit.getOwner() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().repaint(0, 0, getWidth(), getHeight());
            return;
        }

        if (activeUnit != null && activeUnit.getTile() == null) {
            activeUnit = null;
        }

        this.activeUnit = activeUnit;

        if (activeUnit != null) {
            if (freeColClient.getGame().getCurrentPlayer() == freeColClient.getMyPlayer()) {
                if (activeUnit.getState() != UnitState.ACTIVE) {
                    freeColClient.getInGameController().clearOrders(activeUnit);
                }
            } else {
                freeColClient.getInGameController().clearGotoOrders(activeUnit);
            }
        }
        updateGotoPathForActiveUnit();

        
        if(viewMode.getView() == ViewMode.VIEW_TERRAIN_MODE && activeUnit != null)
            viewMode.changeViewMode(ViewMode.MOVE_UNITS_MODE);

        
        if (activeUnit != null) {
            setSelectedTile(activeUnit.getTile().getPosition());
        } else {
            freeColClient.getActionManager().update();
            freeColClient.updateMenuBar();

            int x = 0, y = 0;
            MapControls mapControls = freeColClient.getCanvas().getMapControls();
            if (mapControls != null) {
                x = getWidth() - mapControls.getInfoPanelWidth();
                y = getHeight() - mapControls.getInfoPanelHeight();
            }
            freeColClient.getCanvas().repaint(x, y, getWidth(), getHeight());
        }
    }


    
    public Position getFocus() {
        return focus;
    }


    
    public void setFocus(Position focus) {
        this.focus = focus;

        forceReposition();
        freeColClient.getCanvas().repaint(0, 0, getWidth(), getHeight());
    }

    
    public void setFocusImmediately(Position focus) {
        this.focus = focus;

        forceReposition();
        freeColClient.getCanvas().paintImmediately(0, 0, getWidth(), getHeight());
    }

    
    public void setFocus(int x, int y) {
        setFocus(new Map.Position(x,y));
    }


    
    public int getMessageCount() {
        return messages.size();
    }


    
    public GUIMessage getMessage(int index) {
        return messages.get(index);
    }


    
    public synchronized void addMessage(GUIMessage message) {
        if (getMessageCount() == MESSAGE_COUNT) {
            messages.remove(0);
        }
        messages.add(message);

        freeColClient.getCanvas().repaint(0, 0, getWidth(), getHeight());
    }


    
    public synchronized boolean removeOldMessages() {
        long currentTime = new Date().getTime();
        boolean result = false;

        int i = 0;
        while (i < getMessageCount()) {
            long messageCreationTime = getMessage(i).getCreationTime().getTime();
            if ((currentTime - messageCreationTime) >= MESSAGE_AGE) {
                result = true;
                messages.remove(i);
            } else {
                i++;
            }
        }

        return result;
    }


    
    public int getWidth() {
        return size.width;
    }


    
    public int getHeight() {
        return size.height;
    }


    
    public void display(Graphics2D g) {
        if ((freeColClient.getGame() != null)
                && (freeColClient.getGame().getMap() != null)
                && (focus != null)
                && inGame) {
            removeOldMessages();
            displayMap(g);
        } else {
            if (freeColClient.isMapEditor()) {
                g.setColor(Color.black);
                g.fillRect(0, 0, size.width, size.height);                
            } else {
                Image bgImage = ResourceManager.getImage("CanvasBackgroundImage", size);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, freeColClient.getCanvas());
                    
                    
                    String versionStr = "v. " + FreeCol.getVersion();
                    Font oldFont = g.getFont();
                    Color oldColor = g.getColor();
                    Font newFont = oldFont.deriveFont(Font.BOLD);
                    TextLayout layout = new TextLayout(versionStr, newFont, g.getFontRenderContext());
                   
                    Rectangle2D bounds = layout.getBounds();
                    float x = getWidth() - (float) bounds.getWidth() - 5;
                    float y = getHeight() - (float) bounds.getHeight();
                    g.setColor(Color.white);
                    layout.draw(g, x, y);
                    
                    
                    g.setFont(oldFont);
                    g.setColor(oldColor);
                    
                } else {
                    g.setColor(Color.black);
                    g.fillRect(0, 0, size.width, size.height);
                }
            }
        }
    }


    
    private int getLeftColumns() {
        return getLeftColumns(focus.getY());
    }


    
    private int getLeftColumns(int y) {
        int leftColumns;

        if ((y % 2) == 0) {
            leftColumns = leftSpace / tileWidth + 1;
            if ((leftSpace % tileWidth) > 32) {
                leftColumns++;
            }
        } else {
            leftColumns = leftSpace / tileWidth + 1;
            if ((leftSpace % tileWidth) == 0) {
                leftColumns--;
            }
        }

        return leftColumns;
    }


    
    private int getRightColumns() {
        return getRightColumns(focus.getY());
    }


    
    private int getRightColumns(int y) {
        int rightColumns;

        if ((y % 2) == 0) {
            rightColumns = rightSpace / tileWidth + 1;
            if ((rightSpace % tileWidth) == 0) {
                rightColumns--;
            }
        } else {
            rightColumns = rightSpace / tileWidth + 1;
            if ((rightSpace % tileWidth) > 32) {
                rightColumns++;
            }
        }

        return rightColumns;
    }


    
    private void positionMap() {
        Game gameData = freeColClient.getGame();

        if (focus == null) {
            return;
        }
        
        int x = focus.getX(),
            y = focus.getY();
        int leftColumns = getLeftColumns(),
            rightColumns = getRightColumns();

        

        if (y < topRows) {
            
            bottomRow = (size.height / (tileHeight / 2)) - 1;
            if ((size.height % (tileHeight / 2)) != 0) {
                bottomRow++;
            }
            topRow = 0;
            bottomRowY = bottomRow * (tileHeight / 2);
            topRowY = 0;
        } else if (y >= (gameData.getMap().getHeight() - bottomRows)) {
            
            bottomRow = gameData.getMap().getHeight() - 1;

            topRow = size.height / (tileHeight / 2);
            if ((size.height % (tileHeight / 2)) > 0) {
                topRow++;
            }
            topRow = gameData.getMap().getHeight() - topRow;

            bottomRowY = size.height - tileHeight;
            topRowY = bottomRowY - (bottomRow - topRow) * (tileHeight / 2);
        } else {
            
            bottomRow = y + bottomRows;
            topRow = y - topRows;
            bottomRowY = topSpace + (tileHeight / 2) * bottomRows;
            topRowY = topSpace - topRows * (tileHeight / 2);
        }

        

        if (x < leftColumns) {
            
            leftColumn = 0;

            rightColumn = size.width / tileWidth - 1;
            if ((size.width % tileWidth) > 0) {
                rightColumn++;
            }

            leftColumnX = 0;
        } else if (x >= (gameData.getMap().getWidth() - rightColumns)) {
            
            rightColumn = gameData.getMap().getWidth() - 1;

            leftColumn = size.width / tileWidth;
            if ((size.width % tileWidth) > 0) {
                leftColumn++;
            }

            leftColumnX = size.width - tileWidth - tileWidth / 2 -
                leftColumn * tileWidth;
            leftColumn = rightColumn - leftColumn;
        } else {
            
            leftColumn = x - leftColumns;
            rightColumn = x + rightColumns;
            leftColumnX = (size.width - tileWidth) / 2 - leftColumns * tileWidth;
        }
    }

    
    private void displayGotoPath(Graphics2D g, PathNode gotoPath) {
        if (gotoPath != null) {
            PathNode temp = gotoPath;
            while (temp != null) {
                Point p = getTilePosition(temp.getTile());
                if (p != null) {
                    Tile tile = temp.getTile();
                    Image image;
                    final Color textColor; 
                    if (temp.getTurns() == 0) {
                        g.setColor(Color.GREEN);                        
                        image = getPathImage(activeUnit);
                        if (activeUnit != null 
                                && tile.isExplored()
                                && activeUnit.isNaval()
                                && tile.isLand() 
                                && (tile.getColony() == null || tile.getColony().getOwner() != activeUnit.getOwner())) {
                            image = getPathImage(activeUnit.getFirstUnit());
                        }
                        textColor = Color.BLACK;
                    } else {
                        g.setColor(Color.RED);
                        image = getPathNextTurnImage(activeUnit);
                        if (activeUnit != null
                                && tile.isExplored()
                                && activeUnit.isNaval()
                                && tile.isLand() 
                                && (tile.getColony() == null || tile.getColony().getOwner() != activeUnit.getOwner())) {
                            image = getPathNextTurnImage(activeUnit.getFirstUnit());
                        }
                        textColor = Color.WHITE;
                    }                
                    if (image != null) {
                        g.drawImage(image, p.x + (tileWidth - image.getWidth(null))/2, p.y + (tileHeight - image.getHeight(null))/2, null);
                    } else {
                        g.fillOval(p.x + tileWidth/2, p.y + tileHeight/2, 10, 10);
                        g.setColor(Color.BLACK);
                        g.drawOval(p.x + tileWidth/2, p.y + tileHeight/2, 10, 10);
                    }                
                    if (temp.getTurns() > 0) {
                        BufferedImage stringImage = createStringImage(g, Integer.toString(temp.getTurns()), textColor, tileWidth, 12);
                        g.drawImage(stringImage, p.x + (tileWidth - stringImage.getWidth(null))/2, p.y + (tileHeight - stringImage.getHeight()) / 2, null);
                    }
                }                    
                temp = temp.next;
            }
        }
    }

    private int getXOffset(int clipLeftX, int tileY) {
        int xx = clipLeftX;
        if ((tileY % 2) != 0) {
            xx += tileWidth / 2;
        }
        return xx;
    }



    
    private void displayMap(Graphics2D g) {
        Rectangle clipBounds = g.getClipBounds();
        Map map = freeColClient.getGame().getMap();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        

        if (bottomRow < 0) {
            positionMap();
        }

        
        int clipTopRow = (clipBounds.y - topRowY) / (tileHeight / 2) - 1;
        int clipTopY = topRowY + clipTopRow * (tileHeight / 2);
        clipTopRow = topRow + clipTopRow;

        int clipLeftCol = (clipBounds.x - leftColumnX) / tileWidth - 1;
        int clipLeftX = leftColumnX + clipLeftCol * tileWidth;
        clipLeftCol = leftColumn + clipLeftCol;

        int clipBottomRow = (clipBounds.y + clipBounds.height - topRowY) / (tileHeight / 2);
        clipBottomRow = topRow + clipBottomRow;

        int clipRightCol = (clipBounds.x + clipBounds.width - leftColumnX) / tileWidth;
        clipRightCol = leftColumn + clipRightCol;

        
        if (freeColClient.getClientOptions().getBoolean(ClientOptions.DISPLAY_GRID)) {
            gridPath = new GeneralPath();
            gridPath.moveTo(0, 0);
            int nextX = tileWidth / 2;
            int nextY = - (tileHeight / 2);

            for (int i = 0; i <= ((clipRightCol - clipLeftCol) * 2 + 1); i++) {
                gridPath.lineTo(nextX, nextY);
                nextX += tileWidth / 2;
                if (nextY == - (tileHeight / 2)) {
                    nextY = 0;
                }
                else {
                    nextY = - (tileHeight / 2);
                }
            }
        }

        

        g.setColor(Color.black);
        g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        int xx;

        
        
        int yy = clipTopY;

        
        for (int tileY = clipTopRow; tileY <= clipBottomRow; tileY++) {
            xx = getXOffset(clipLeftX, tileY);

            
            for (int tileX = clipLeftCol; tileX <= clipRightCol; tileX++) {
                Tile tile = map.getTile(tileX, tileY);
                displayBaseTile(g, map, tile, xx, yy, true);
                xx += tileWidth;
            }

            yy += tileHeight / 2;
        }

        

        List<Unit> darkUnits = new ArrayList<Unit>();
        List<Integer> darkUnitsX = new ArrayList<Integer>();
        List<Integer> darkUnitsY = new ArrayList<Integer>();
        
        yy = clipTopY;

        
        for (int tileY = clipTopRow; tileY <= clipBottomRow; tileY++) {
            xx = getXOffset(clipLeftX, tileY);

            if (freeColClient.getClientOptions().getBoolean(ClientOptions.DISPLAY_GRID)) {
                
                g.translate(xx, yy + (tileHeight / 2));
                g.setColor(Color.BLACK);
                g.draw(gridPath);
                g.translate(- xx, - (yy + (tileHeight / 2)));
            }

            
            for (int tileX = clipLeftCol; tileX <= clipRightCol; tileX++) {
                Tile tile = map.getTile(tileX, tileY);
                    
                
                paintBorders(g, tile, xx, yy, true);
                
                displayTileOverlays(g, map, tile, xx, yy, true, true);
                
                paintBorders(g, tile, xx, yy, false);

                if (viewMode.displayTileCursor(tile,xx,yy)) {
                    drawCursor(g, xx, yy);
                }
                xx += tileWidth;
            }

            xx = getXOffset(clipLeftX, tileY);

            
            for (int tileX = clipLeftCol; tileX <= clipRightCol; tileX++) {
                

                Unit unitInFront = getUnitInFront(map.getTile(tileX, tileY));
                if (unitInFront != null && !isOutForAnimation(unitInFront)) {
                    displayUnit(g, unitInFront, xx, yy);
                        
                    if (unitInFront.isUndead()) {
                        darkUnits.add(unitInFront);
                        darkUnitsX.add(xx);
                        darkUnitsY.add(yy);
                    }
                }
                xx += tileWidth;
            }

            yy += tileHeight / 2;
        }

        
        if (darkUnits.size() > 0) {
            g.setColor(Color.BLACK);
            final Image im = getImageLibrary().getMiscImage(ImageLibrary.DARKNESS);
            for (int index=0; index<darkUnits.size(); index++) {
                final Unit u = darkUnits.get(index);
                final int x = darkUnitsX.get(index);
                final int y = darkUnitsY.get(index);            
                g.drawImage(im, x  + tileWidth/2 - im.getWidth(null)/2, y + tileHeight/2 - im.getHeight(null)/2, null);
                displayUnit(g, u, x, y);
            }
        }

        
        
        yy = clipTopY;
        
        for (int tileY = clipTopRow; tileY <= clipBottomRow; tileY++) {
            xx = getXOffset(clipLeftX, tileY);

            
            for (int tileX = clipLeftCol; tileX <= clipRightCol; tileX++) {
                Tile tile = map.getTile(tileX, tileY);
                if (tile != null && tile.getSettlement() != null
                    && tile.getSettlement().getName() != null) {
                    Settlement settlement = tile.getSettlement();

                    if (!(settlement instanceof IndianSettlement)
                        ||(((IndianSettlement)settlement).hasBeenVisited(freeColClient.getMyPlayer()))) {
                        BufferedImage stringImage =
                            createSettlementNameImage(g, settlement,
                                                  lib.getTerrainImageWidth(tile.getType()) * 4/3, 16);
                        g.drawImage(stringImage, 
                                    xx + (lib.getTerrainImageWidth(tile.getType()) - 
                                          stringImage.getWidth())/2 + 1,
                                    yy + (lib.getSettlementImage(settlement).getHeight(null) + 1), null);
                    }
                }
                xx += tileWidth;
            }
            yy += tileHeight / 2;
        }

        

        displayGotoPath(g, currentPath);
        displayGotoPath(g, gotoPath);
        
        
        Canvas canvas = freeColClient.getCanvas();
        
        if (!freeColClient.isMapEditor()
                && freeColClient.getGame() != null
                && freeColClient.getMyPlayer() != freeColClient.getGame().getCurrentPlayer()) {
            
            if (greyLayer == null) {
                greyLayer = new GrayLayer(lib);
            }
            if (greyLayer.getParent() == null) { 
                canvas.add(greyLayer, JLayeredPane.DEFAULT_LAYER, false);
                canvas.moveToFront(greyLayer);
            }
                        
            greyLayer.setBounds(0,0,canvas.getSize().width, canvas.getSize().height);
            greyLayer.setPlayer(freeColClient.getGame().getCurrentPlayer());
            
        }
        else {
            if (greyLayer != null && greyLayer.getParent() != null) {
                canvas.remove(greyLayer, false);
            }
        }

        

        
        synchronized (this) {
            BufferedImage si = createStringImage(g, "getSizes", Color.WHITE, size.width, 12);

            yy = size.height - 300 - getMessageCount() * si.getHeight();
            xx = 40;

            for (int i = 0; i < getMessageCount(); i++) {
                GUIMessage message = getMessage(i);
                g.drawImage(createStringImage(g, message.getMessage(), message.getColor(), size.width, 12), xx, yy, null);
                yy += si.getHeight();
            }
        }

        Image decoration = ResourceManager.getImage("menuborder.shadow.s.image");
        int width = decoration.getWidth(null);
        for (int index = 0; index < size.width; index += width) {
            g.drawImage(decoration, index, 0, null);
        }
        decoration = ResourceManager.getImage("menuborder.shadow.sw.image");
        g.drawImage(decoration, 0, 0, null);
        decoration = ResourceManager.getImage("menuborder.shadow.se.image");
        g.drawImage(decoration, size.width - decoration.getWidth(null), 0, null);
        
    }
    
    
    private Image getPathImage(Unit u) {
        if (u == null) {
            return null;
        } else {
            return ResourceManager.getImage("path." + u.getPathTypeImage() + ".image");
        }
    }
    
    
    
    
    private Image getPathNextTurnImage(Unit u) {
        if (u == null) {
            return null;
        } else {
            return ResourceManager.getImage("path." + u.getPathTypeImage() + ".nextTurn.image");
        }
    }

    
    public BufferedImage createStringImage(Graphics2D g, String nameString, Color color, int maxWidth, int preferredFontSize) {
        return createStringImage(null, g, nameString, color, maxWidth, preferredFontSize);
    }
    
    
    public BufferedImage createStringImage(JComponent c, String nameString, Color color, int maxWidth, int preferredFontSize) {
        return createStringImage(c, null, nameString, color, maxWidth, preferredFontSize);
    }
    
    
    private class StringImageKey {
        public Color color;
        public String name;
        public StringImageKey(Color c, String s) {
            this.color = c;
            this.name = s;
        }
        public int hashCode() {
            return name.hashCode();
        }
        public boolean equals(Object o) {
            if (o==null || !(o instanceof StringImageKey))
                return false;
            StringImageKey other = (StringImageKey) o;
            return (other.color.equals(this.color)) && (other.name.equals(this.name));
        }
    }
    private HashMap<StringImageKey, BufferedImage> stringImageCache = new HashMap<StringImageKey, BufferedImage>();
    
    
    private BufferedImage createStringImage(JComponent c, Graphics g, String nameString, Color color, int maxWidth, int preferredFontSize) {
        if (color == null) {
            logger.warning("createStringImage called with color null");
            color = Color.WHITE;
        }
        BufferedImage bi = null;
        
        StringImageKey key = new StringImageKey(color, nameString);
        bi = stringImageCache.get(key);
        if (bi!=null) {
            return bi;
        }
        
        Font nameFont = (c != null) ? c.getFont() : g.getFont();
        FontMetrics nameFontMetrics = (c != null) ? c.getFontMetrics(nameFont) : g.getFontMetrics(nameFont);

        
        int fontSize = preferredFontSize;
        do {
            nameFont = nameFont.deriveFont(Font.BOLD, fontSize);
            nameFontMetrics = (c != null) ? c.getFontMetrics(nameFont) : g.getFontMetrics(nameFont);
            bi = new BufferedImage(nameFontMetrics.stringWidth(nameString) + 4, 
                                   nameFontMetrics.getMaxAscent() + nameFontMetrics.getMaxDescent(), BufferedImage.TYPE_INT_ARGB);
            fontSize -= 2;
        } while (bi.getWidth() > maxWidth);

        
        Graphics2D big = bi.createGraphics();
        big.setColor(color);
        big.setFont(nameFont);
        big.drawString(nameString, 2, nameFontMetrics.getMaxAscent());

        
        int playerColor = color.getRGB();
        int borderColor = getStringBorderColor(color).getRGB();
        for (int biX=0; biX<bi.getWidth(); biX++) {
            for (int biY=0; biY<bi.getHeight(); biY++) {
                int r = bi.getRGB(biX, biY);

                if (r == playerColor) {
                    continue;
                }

                for (int cX=-1; cX <=1; cX++) {
                    for (int cY=-1; cY <=1; cY++) {
                        if (biX+cX >= 0 && biY+cY >= 0 && biX+cX < bi.getWidth() && biY+cY < bi.getHeight() && bi.getRGB(biX + cX, biY + cY) == playerColor) {
                            bi.setRGB(biX, biY, borderColor);
                            continue;
                        }
                    }
                }
            }
        }
        this.stringImageCache.put(key, bi);
        return bi;
    }

    private BufferedImage createSettlementNameImage(Graphics g, Settlement settlement,
                                                int maxWidth, int preferredFontSize) {        
        return createStringImage((Graphics2D) g,
                settlement.getName(),
                settlement.getOwner().getColor(),
                maxWidth,
                preferredFontSize);
    }

    
    public void drawRoad(Graphics2D g, long seed, int x1, int y1, int x2, int y2) {
        final int MAX_CORR = 4;
        Color oldColor = g.getColor();
        Random roadRandom = new Random(seed);

        int i = Math.max(Math.abs(x2-x1), Math.abs(y2-y1));
        int baseX = x1;
        int baseY = y1;
        double addX = (x2-x1)/((double) i);
        double addY = (y2-y1)/((double) i);
        int corr = 0;
        int xCorr = 0;
        int yCorr = 0;
        int lastDiff = 1;

        g.setColor(new Color(128, 64, 0));
        g.drawLine(baseX, baseY, baseX, baseY);

        for (int j=1; j<=i; j++) {
            int oldCorr = corr;
            
                corr = corr + roadRandom.nextInt(3)-1;
                if (oldCorr != corr) {
                    lastDiff = oldCorr - corr;
                }
            

            if (Math.abs(corr) > MAX_CORR || Math.abs(corr) >= i-j) {
                if (corr > 0) {
                    corr--;
                } else {
                    corr++;
                }
            }

            if (corr != oldCorr) {
                g.setColor(new Color(128, 128, 0));
                g.drawLine(baseX+(int) (j*addX)+xCorr, baseY+(int) (j*addY)+yCorr, baseX+(int) (j*addX)+xCorr, baseY+(int) (j*addY)+yCorr);
            } else {
                int oldXCorr = 0;
                int oldYCorr = 0;

                if (x2-x1 == 0) {
                    oldXCorr = corr+lastDiff;
                    oldYCorr = 0;
                } else if (y2-y1 == 0) {
                    oldXCorr = 0;
                    oldYCorr = corr+lastDiff;
                } else {
                    if (corr > 0) {
                        oldXCorr = corr+lastDiff;
                    } else {
                        oldYCorr = corr+lastDiff;
                    }
                }

                g.setColor(new Color(128, 128, 0));
                g.drawLine(baseX+(int) (j*addX)+oldXCorr, baseY+(int) (j*addY)+oldYCorr, baseX+(int) (j*addX)+oldXCorr, baseY+(int) (j*addY)+oldYCorr);
            }

            if (x2-x1 == 0) {
                xCorr = corr;
                yCorr = 0;
            } else if (y2-y1 == 0) {
                xCorr = 0;
                yCorr = corr;
            } else {
                if (corr > 0) {
                    xCorr = corr;
                } else {
                    yCorr = corr;
                }
            }

            g.setColor(new Color(128, 64, 0));
            g.drawLine(baseX+(int) (j*addX)+xCorr, baseY+(int) (j*addY)+yCorr, baseX+(int) (j*addX)+xCorr, baseY+(int) (j*addY)+yCorr);
        }
        g.setColor(oldColor);
    }


    
    public void displayColonyTile(Graphics2D g, Map map, Tile tile, int x, int y, Colony colony) {
        displayBaseTile(g, map, tile, x, y, false);        

        Unit occupyingUnit = null;
        int price = 0;
        if (colony != null) {
            occupyingUnit = colony.getColonyTile(tile).getOccupyingUnit();
            Settlement settlement = tile.getOwningSettlement();
            price = colony.getOwner().getLandPrice(tile);
            if ((settlement != null &&
                 (settlement instanceof Colony && settlement != colony) ||
                 (settlement instanceof IndianSettlement && price > 0)) ||
                occupyingUnit != null) {
                
                g.drawImage(lib.getMiscImage(ImageLibrary.TILE_TAKEN), x, y, null);
            }
        }
        displayTileOverlays(g, map, tile, x, y, false, false);
        
        if (price > 0 && tile.getSettlement() == null) {
            
            Image image = lib.getMiscImage(ImageLibrary.TILE_OWNED_BY_INDIANS);
            g.drawImage(image, x+tileWidth/2-image.getWidth(null)/2, y+tileHeight/2-image.getHeight(null)/2, null);
        }
        
        if (occupyingUnit != null) {
            ImageIcon image = lib.getScaledImageIcon(lib.getUnitImageIcon(occupyingUnit), 0.5f);
            g.drawImage(image.getImage(), (x + tileWidth / 4) - image.getIconWidth() / 2,
                    (y + tileHeight / 2) - image.getIconHeight() / 2, null);
            
            displayOccupationIndicator(g, occupyingUnit, x + (int) (STATE_OFFSET_X * lib.getScalingFactor()), y);
        }
    }


    
    public void displayTerrain(Graphics2D g, Map map, Tile tile, int x, int y) {
        displayBaseTile(g, map, tile, x, y, true);
        displayTileItems(g, map, tile, x, y);
        
    }

    
    public void displayTile(Graphics2D g, Map map, Tile tile, int x, int y) {
        displayTile(g, map, tile, x, y, true);
    }

    
    public void displayTile(Graphics2D g, Map map, Tile tile, int x, int y, boolean drawUnexploredBorders) {
        displayBaseTile(g, map, tile, x, y, drawUnexploredBorders);
        displayTileOverlays(g, map, tile, x, y, drawUnexploredBorders, true);
    }

    
    private void displayBaseTile(Graphics2D g, Map map, Tile tile, int x, int y, boolean drawUnexploredBorders) {
        if (tile == null) {
            return;
        }
        
        g.drawImage(lib.getTerrainImage(tile.getType(), tile.getX(), tile.getY()), x, y, null);

        Map.Position pos = new Map.Position(tile.getX(), tile.getY());

        if (!tile.isLand() && tile.getStyle() > 0) {
            g.drawImage(lib.getBeachImage(tile.getStyle()), x, y, null);
        }

        for (Direction direction : Direction.values()) {
            Tile borderingTile = map.getAdjacentTile(pos, direction);
            if (borderingTile!=null) {

                if (!drawUnexploredBorders && !borderingTile.isExplored() &&
                    (direction == Direction.SE || direction == Direction.S ||
                     direction == Direction.SW)) {
                    continue;
                }

                if (tile.getType() == borderingTile.getType()) {
                    
                    continue;
                }
                else if (tile.isLand() && !borderingTile.isLand()) {
                    
                    continue;
                }
                else if (!tile.isLand() && borderingTile.isLand() && borderingTile.isExplored()) {
                    
                    
                    
                    g.drawImage(lib.getBorderImage(borderingTile.getType(), direction,
                                                    tile.getX(), tile.getY()),
                                                    x, y, null);
                    if (borderingTile.getRiver() != null &&
                        (direction == Direction.SE || direction == Direction.SW ||
                         direction == Direction.NE || direction == Direction.NW)) {
                        g.drawImage(lib.getRiverMouthImage(direction, borderingTile.getRiver().getMagnitude(),
                                                           tile.getX(), tile.getY()),
                                    x, y, null);
                    }
               } else if (tile.isExplored() && borderingTile.isExplored()) {
                    if (tile.getType().getArtBasic().equals(borderingTile.getType().getArtBasic())) {
                        
                        continue;
                    }
                    else if (borderingTile.getType().getIndex() < tile.getType().getIndex()) {
                        
                        g.drawImage(lib.getBorderImage(borderingTile.getType(), direction,
                                                        tile.getX(), tile.getY()),
                                                        x, y, null);
                    }
                }
            }
        }
    }    


    private void paintBorders(Graphics2D g, Tile tile, int x, int y, boolean opaque) {
        if (tile == null || !freeColClient.getClientOptions().getBoolean(ClientOptions.DISPLAY_BORDERS)) {
            return;
        }
        Player owner = tile.getOwner();
        if (owner != null) {
            Stroke oldStroke = g.getStroke();
            g.setStroke(borderStroke);
            Color oldColor = g.getColor();
            Color newColor = new Color(owner.getColor().getRed(),
                                       owner.getColor().getGreen(),
                                       owner.getColor().getBlue(),
                                       opaque ? 255 : 100);
            g.setColor(newColor);
            GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            path.moveTo(borderPoints.get(Direction.NW).width,
                        borderPoints.get(Direction.NW).height);
            for (Direction d : borderDirections) {
                Tile otherTile = tile.getNeighbourOrNull(d);
                Direction next = d.getNextDirection();
                Direction next2 = next.getNextDirection();
                if (otherTile == null || otherTile.getOwner() != owner) {
                    Tile tile1 = tile.getNeighbourOrNull(next);
                    Tile tile2 = tile.getNeighbourOrNull(next2);
                    if (tile2 == null || tile2.getOwner() != owner) {
                        
                        path.lineTo(borderPoints.get(next).width,
                                    borderPoints.get(next).height);
                        path.quadTo(controlPoints.get(next).width,
                                    controlPoints.get(next).height,
                                    borderPoints.get(next2).width,
                                    borderPoints.get(next2).height);
                    } else {
                        int dx = 0, dy = 0;
                        switch(d) {
                        case NW: dx = 64; dy = -32; break;
                        case NE: dx = 64; dy = 32; break;
                        case SE: dx = -64; dy = 32; break;
                        case SW: dx = -64; dy = -32; break;
                        }
                        if (tile1 != null && tile1.getOwner() == owner) {
                            
                            path.lineTo(borderPoints.get(next).width,
                                        borderPoints.get(next).height);
                            
                            Direction previous = d.getPreviousDirection();
                            Direction previous2 = previous.getPreviousDirection();
                            int ddx = 0, ddy = 0;
                            switch(d) {
                            case NW: ddy = -64; break;
                            case NE: ddx = 128; break;
                            case SE: ddy = 64; break;
                            case SW: ddx = -128; break;
                            }
                            path.quadTo(controlPoints.get(previous).width + dx,
                                        controlPoints.get(previous).height + dy,
                                        borderPoints.get(previous2).width + ddx,
                                        borderPoints.get(previous2).height + ddy);
                        } else {
                            
                            path.lineTo(borderPoints.get(d).width + dx,
                                        borderPoints.get(d).height + dy);
                        }
                    }
                } else {
                    path.moveTo(borderPoints.get(next2).width,
                                borderPoints.get(next2).height);
                }
            }
            path.transform(AffineTransform.getTranslateInstance(x, y));
            g.draw(path);
            g.setColor(oldColor);
            g.setStroke(oldStroke);
        }
    }



    
    private void displayTileOverlays(Graphics2D g, Map map, Tile tile, int x, int y,
                                     boolean drawUnexploredBorders, boolean withNumber) {
        if (tile != null) {
            if (drawUnexploredBorders) {
                displayUnexploredBorders(g, map, tile, x, y);
            }
            displayTileItems(g, map, tile, x, y);
            displaySettlement(g, map, tile, x, y, withNumber);
            displayFogOfWar(g, map, tile, x, y);
            displayOptionalValues(g, map, tile, x, y);
        }
    }

    
    private void displayTileItems(Graphics2D g, Map map, Tile tile, int x, int y) {  
        
        
        if (!tile.isExplored()) {
            g.drawImage(lib.getTerrainImage(null, tile.getX(), tile.getY()), x, y, null);
        } else {
            
            List<TileItem> tileItems = new ArrayList<TileItem>();
            if (tile.getTileItemContainer() != null) {
                tileItems = tile.getTileItemContainer().getTileItems();
            }
            int startIndex = 0;
            for (int index = startIndex; index < tileItems.size(); index++) {
                if (tileItems.get(index).getZIndex() < OVERLAY_INDEX) {
                    drawItem(g, tile, tileItems.get(index), x, y);
                    startIndex = index + 1;
                } else {
                    startIndex = index;
                    break;
                }
            }
            
            if (tile.getType().getArtOverlay() != null) {
                Image overlayImage = lib.getOverlayImage(tile.getType(), tile.getX(), tile.getY());
                g.drawImage(overlayImage, x, y + tileHeight - overlayImage.getHeight(null), null);
            }
            for (int index = startIndex; index < tileItems.size(); index++) {
                if (tileItems.get(index).getZIndex() < FOREST_INDEX) {
                    drawItem(g, tile, tileItems.get(index), x, y);
                    startIndex = index + 1;
                } else {
                    startIndex = index;
                    break;
                }
            }
            
            if (tile.isForested()) {
                Image forestImage = lib.getForestImage(tile.getType());
                g.drawImage(forestImage, x, y + tileHeight - forestImage.getHeight(null), null);
            }

            
            for (int index = startIndex; index < tileItems.size(); index++) {
                drawItem(g, tile, tileItems.get(index), x, y);
            }
        }
    }

    private void drawItem(Graphics2D g, Tile tile, TileItem item, int x, int y) {

        if (item instanceof Resource) {
            Image bonusImage = lib.getBonusImage(((Resource) item).getType());
            if (bonusImage != null) {
                g.drawImage(bonusImage, x + tileWidth/2 - bonusImage.getWidth(null)/2,
                            y + tileHeight/2 - bonusImage.getHeight(null)/2, null);
            }
        } else if (item instanceof LostCityRumour) {
            g.drawImage(lib.getMiscImage(ImageLibrary.LOST_CITY_RUMOUR),
                        x + (int) (RUMOUR_OFFSET_X * lib.getScalingFactor()),
                        y + (int) (RUMOUR_OFFSET_Y * lib.getScalingFactor()), null);
        } else if (item instanceof TileImprovement) {
            TileImprovement improvement = (TileImprovement) item;
            if (!improvement.isComplete()) {
                return;
            } else if (improvement.getType().getArtOverlay() != null) {
                
                g.drawImage(ResourceManager.getImage(improvement.getType().getArtOverlay()), x, y, null);
            } else if (improvement.isRiver() && improvement.getMagnitude() < TileImprovement.FJORD_RIVER) {
                g.drawImage(lib.getRiverImage(improvement.getStyle()), x, y, null);
            } else if (improvement.isRoad()) {
                long seed = Long.parseLong(Integer.toString(tile.getX()) + Integer.toString(tile.getY()));
                boolean connectedRoad = false;
                for (Direction direction : Direction.values()) {
                    Tile borderingTile = tile.getMap().getAdjacentTile(tile.getPosition(), direction);
                    if (borderingTile!=null) {
                        if (borderingTile.hasRoad()) {
                            connectedRoad =  true;
                            int nx = x + tileWidth/2;
                            int ny = y + tileHeight/2;

                            switch (direction) {
                            case N : nx = x + tileWidth/2; ny = y; break;
                            case NE: nx = x + (tileWidth*3)/4; ny = y + tileHeight/4; break;
                            case E : nx = x + tileWidth; ny = y + tileHeight/2; break;
                            case SE: nx = x + (tileWidth*3)/4; ny = y + (tileHeight*3)/4; break;
                            case S : nx = x + tileWidth/2; ny = y + tileHeight; break;
                            case SW: nx = x + tileWidth/4; ny = y + (tileHeight*3)/4; break;
                            case W : nx = x; ny = y + tileHeight/2; break;
                            case NW: nx = x + tileWidth/4; ny = y + tileHeight/4; break;
                            }

                            drawRoad(g, seed, x + tileWidth/2, y + tileHeight/2, nx, ny);
                        }
                    }
                }

                if (!connectedRoad) {
                    drawRoad(g, seed, x + tileWidth/2 - 10, y + tileHeight/2,
                             x + tileWidth/2 + 10, y + tileHeight/2);
                    drawRoad(g, seed, x + tileWidth/2, y + tileHeight/2 - 10,
                             x + tileWidth/2, y + tileHeight/2 + 10);
                }
            }
        }
    }

    
    private void displaySettlement(Graphics2D g, Map map, Tile tile, int x, int y, boolean withNumber) {  
        if (tile.isExplored()) {
            Settlement settlement = tile.getSettlement();

            if (settlement != null) {
                if (settlement instanceof Colony) {
                    Image colonyImage = lib.getSettlementImage(settlement);
                    
                    g.drawImage(colonyImage,
                                x + (lib.getTerrainImageWidth(tile.getType()) -
                                     colonyImage.getWidth(null)) / 2,
                                y + (lib.getTerrainImageHeight(tile.getType()) -
                                     colonyImage.getHeight(null)) / 2, null);

                    if (withNumber) {
                        String populationString = Integer.toString(((Colony)settlement).getUnitCount());
                        Color theColor = null;

                        int bonus = ((Colony)settlement).getProductionBonus();
                        switch (bonus) {
                        case 2:
                            theColor = Color.BLUE;
                            break;
                        case 1:
                            theColor = Color.GREEN;
                            break;
                        case -1:
                            theColor = Color.ORANGE;
                            break;
                        case -2:
                            theColor = Color.RED;
                            break;
                        default:
                            theColor = Color.WHITE;
                        break;
                        }

                        BufferedImage stringImage = createStringImage(g, populationString, theColor, lib.getTerrainImageWidth(tile.getType()), 12);
                        g.drawImage(stringImage, x + (lib.getTerrainImageWidth(tile.getType()) - stringImage.getWidth())/2 + 1, y + ((lib.getTerrainImageHeight(tile.getType()) - stringImage.getHeight()) / 2) + 1, null);
                    }
                    g.setColor(Color.BLACK);
                } else if (settlement instanceof IndianSettlement) {
                    IndianSettlement indianSettlement = (IndianSettlement) settlement;
                    Image settlementImage = lib.getSettlementImage(settlement);

                    
                    g.drawImage(settlementImage,
                                x + (lib.getTerrainImageWidth(tile.getType()) -
                                     settlementImage.getWidth(null)) / 2,
                                y + (lib.getTerrainImageHeight(tile.getType()) - 
                                     settlementImage.getHeight(null)) / 2, null);

                    
                    g.drawImage(lib.getColorChip(indianSettlement.getOwner().getColor()),
                                x + (int) (STATE_OFFSET_X * lib.getScalingFactor()),
                                y + (int) (STATE_OFFSET_Y * lib.getScalingFactor()), null);

                    
                    Unit missionary = indianSettlement.getMissionary();
                    if (missionary != null) {
                        boolean expert = missionary.hasAbility("model.ability.expertMissionary");
                        g.drawImage(lib.getMissionChip(missionary.getOwner().getColor(), expert),
                                    x + (int) (STATE_OFFSET_X * lib.getScalingFactor()) +
                                    (MISSION_OFFSET_X - STATE_OFFSET_X),
                                    y + (int) (MISSION_OFFSET_Y * lib.getScalingFactor()), null);
                    }

                    
                    if (freeColClient.getMyPlayer() != null) {
                        Tension alarm = indianSettlement.getAlarm(freeColClient.getMyPlayer());
                        if (alarm != null) {
                            
                            final boolean visited = indianSettlement.hasBeenVisited(freeColClient.getMyPlayer());
                            g.drawImage(lib.getAlarmChip(alarm.getLevel(), visited),
                                        x + (int) (STATE_OFFSET_X * lib.getScalingFactor()) +
                                        (ALARM_OFFSET_X - STATE_OFFSET_X),
                                        y + (int) (ALARM_OFFSET_Y  * lib.getScalingFactor()), null);
                        }
                    }

                    g.setColor(Color.BLACK);
                    if (indianSettlement.isCapital()) {
                        
                        g.drawString("*",
                                     x + (STATE_OFFSET_X * lib.getScalingFactor()) + TEXT_OFFSET_X + 1,
                                     y + (int) (STATE_OFFSET_Y * lib.getScalingFactor()) + TEXT_OFFSET_Y + 2);
                    } else {
                        g.drawString("-", 
                                     x + (int) (STATE_OFFSET_X * lib.getScalingFactor()) + TEXT_OFFSET_X,
                                     y + (int) (STATE_OFFSET_Y * lib.getScalingFactor()) + TEXT_OFFSET_Y);
                    }
                } else {
                    logger.warning("Requested to draw unknown settlement type.");
                }
            }
        }
    }

    
    private void displayFogOfWar(Graphics2D g, Map map, Tile tile, int x, int y) {  
        if (tile.isExplored()) {
            final boolean displayFogOfWar = freeColClient.getGame().getGameOptions().getBoolean(GameOptions.FOG_OF_WAR)
                    && freeColClient.getClientOptions().getBoolean(ClientOptions.DISPLAY_FOG_OF_WAR);
            if (displayFogOfWar
                    && freeColClient.getMyPlayer() != null
                    && !freeColClient.getMyPlayer().canSee(tile)) {
                g.setColor(Color.BLACK);
                Composite oldComposite = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                Polygon pol = new Polygon(new int[] {x + tileWidth/2, x + tileWidth, x + tileWidth/2, x},
                                          new int[] {y, y + tileHeight/2, y + tileHeight, y + tileHeight/2},
                                          4);
                g.fill(pol);
                g.setComposite(oldComposite);
            }
        }
    }

    
    private void displayUnexploredBorders(Graphics2D g, Map map, Tile tile, int x, int y) {  
        if (tile.isExplored()) {
            Map.Position pos = new Map.Position(tile.getX(), tile.getY());

            for (Direction direction : Direction.values()) {
                Tile borderingTile = map.getAdjacentTile(pos, direction);
                if (borderingTile!=null) {

                    if (borderingTile.isExplored()){
                        continue;
                    }

                    g.drawImage(lib.getBorderImage(null, direction, tile.getX(), tile.getY()), x, y, null);
                }
            }
        }
    }


    
    private void displayOptionalValues(Graphics2D g, Map map, Tile tile, int x, int y) {
        switch (displayTileText) {
        case ClientOptions.DISPLAY_TILE_TEXT_EMPTY:
            break;
        case ClientOptions.DISPLAY_TILE_TEXT_NAMES:
            if (tile.getName() != null) {
                String tileName = tile.getName();
                g.setColor(Color.BLACK);
                int b = getBreakingPoint(tileName);
                if (b == -1) {
                    g.drawString(tileName, x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(tileName))/2, y + (lib.getTerrainImageHeight(tile.getType())/2));
                    
                } else {
                    g.drawString(tileName.substring(0, b), x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(tileName.substring(0, b)))/2, y + lib.getTerrainImageHeight(tile.getType())/2 - (g.getFontMetrics().getAscent()*2)/3);
                    g.drawString(tileName.substring(b+1), x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(tileName.substring(b+1)))/2, y + lib.getTerrainImageHeight(tile.getType())/2 + (g.getFontMetrics().getAscent()*2)/3);
                    
                }
            }
            break;
        case ClientOptions.DISPLAY_TILE_TEXT_OWNERS:
            if (tile.getOwner() != null) {
                String tileOwner = tile.getOwner().getNationAsString();
                g.setColor(Color.BLACK);
                int b = getBreakingPoint(tileOwner);
                if (b == -1) {
                    g.drawString(tileOwner, x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(tileOwner))/2, y + (lib.getTerrainImageHeight(tile.getType())/2));
                    
                } else {
                    g.drawString(tileOwner.substring(0, b), x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(tileOwner.substring(0, b)))/2, y + lib.getTerrainImageHeight(tile.getType())/2 - (g.getFontMetrics().getAscent()*2)/3);
                    g.drawString(tileOwner.substring(b+1), x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(tileOwner.substring(b+1)))/2, y + lib.getTerrainImageHeight(tile.getType())/2 + (g.getFontMetrics().getAscent()*2)/3);
                    
                }
            }
            break;
        case ClientOptions.DISPLAY_TILE_TEXT_REGIONS:
            if (tile.getRegion() != null) {
                String regionString = tile.getRegion().getDisplayName();
                g.setColor(Color.BLACK);
                int b = getBreakingPoint(regionString);
                if (b == -1) {
                    g.drawString(regionString, x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(regionString))/2, y + (lib.getTerrainImageHeight(tile.getType())/2));
                    
                } else {
                    g.drawString(regionString.substring(0, b), x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(regionString.substring(0, b)))/2, y + lib.getTerrainImageHeight(tile.getType())/2 - (g.getFontMetrics().getAscent()*2)/3);
                    g.drawString(regionString.substring(b+1), x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(regionString.substring(b+1)))/2, y + lib.getTerrainImageHeight(tile.getType())/2 + (g.getFontMetrics().getAscent()*2)/3);
                    
                }
            }
            break;
        default:
            logger.warning("displayTileText out of range");
            break;
        }
        

        

        g.setColor(Color.BLACK);

        if (displayCoordinates) {
            String posString = tile.getX() + ", " + tile.getY();
            g.drawString(posString, x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(posString))/2, y + (lib.getTerrainImageHeight(tile.getType()) - g.getFontMetrics().getAscent())/2);
        }
        if (displayColonyValue && tile.isExplored() && tile.isLand()) {
            String valueString;
            if (displayColonyValuePlayer == null) {
                valueString = Integer.toString(freeColClient.getGame().getCurrentPlayer().getOutpostValue(tile));
            } else {
                valueString = Integer.toString(displayColonyValuePlayer.getColonyValue(tile));
            }
            g.drawString(valueString, x + (lib.getTerrainImageWidth(tile.getType()) - g.getFontMetrics().stringWidth(valueString))/2, y + (lib.getTerrainImageHeight(tile.getType()) - g.getFontMetrics().getAscent())/2);
        }
    }


    
    public void stopGoto() {
        freeColClient.getCanvas().setCursor(null);
        setGotoPath(null);
        updateGotoPathForActiveUnit();
        gotoStarted = false;
    }


    
    public void startGoto() {
        gotoStarted = true;
        freeColClient.getCanvas().setCursor((java.awt.Cursor) UIManager.get("cursor.go"));
        setGotoPath(null);
    }


    
    public boolean isGotoStarted() {
        return gotoStarted;
    }


    
    public void updateGotoPathForActiveUnit() {
        if (activeUnit == null || activeUnit.getDestination() == null) {
            currentPath = null;
        } else {
            if (activeUnit.getDestination() instanceof Europe) {
                currentPath = freeColClient.getGame().getMap().findPathToEurope(activeUnit, activeUnit.getTile());
            } else if (activeUnit.getDestination().getTile() == activeUnit.getTile()) {
                
                currentPath = null;
            } else {
                currentPath = activeUnit.findPath(activeUnit.getDestination().getTile());
            }
        }
    }

    
    public void setGotoPath(PathNode gotoPath) {
        this.gotoPath = gotoPath;

        freeColClient.getCanvas().refresh();
    }


    
    public PathNode getGotoPath() {
        return gotoPath;
    }

    
    public void setDisplayTileText(int tileTextType) {
        this.displayTileText = tileTextType;
    }


    
    public int getBreakingPoint(String string) {
        int center = string.length() / 2;
        int bestIndex = string.indexOf(' ');

        int index = 0;
        while (index != -1 && index != bestIndex) {
            if (Math.abs(center-index) < Math.abs(center-bestIndex)) {
                bestIndex = index;
            }

            index = string.indexOf(' ', bestIndex);
        }

        if (bestIndex == 0 || bestIndex == string.length()) {
            return -1;
        } else {
            return bestIndex;
        }
    }
    
    
    private class IndicatorImageKey {
        public Color bgColor;
        public Color fgColor;
        public String name;
        public IndicatorImageKey(Color bg, Color fg, String s) {
            this.bgColor = bg;
            this.fgColor = fg;
            this.name = s;
        }
        public int hashCode() {
            return name.hashCode();
        }
        public boolean equals(Object o) {
            if (o==null || !(o instanceof IndicatorImageKey))
                return false;
            IndicatorImageKey other = (IndicatorImageKey) o;
            return (other.bgColor.equals(this.bgColor)) && 
                   (other.fgColor.equals(this.fgColor)) && 
                   (other.name.equals(this.name));
        }
    }
    private HashMap<IndicatorImageKey, BufferedImage> indicatorImageCache = new HashMap<IndicatorImageKey, BufferedImage>();

    private Image getOccupationIndicatorImage(Unit unit) {
        Color backgroundColor = unit.getOwner().getColor();
        Color foregroundColor = getForegroundColor(unit.getOwner().getColor());
        String occupationString;
        if (unit.getOwner() != freeColClient.getMyPlayer()
                && unit.isNaval()) {
            occupationString = Integer.toString(unit.getVisibleGoodsCount());
        } else {
            occupationString = unit.getOccupationIndicator();
            if (unit.getState() == UnitState.FORTIFIED)
                foregroundColor = Color.GRAY;
        }
        
        IndicatorImageKey key = new IndicatorImageKey(backgroundColor, foregroundColor, occupationString);
        BufferedImage img = indicatorImageCache.get(key);
        if (img!=null)
            return img;
        
        Image chip = lib.getColorChip(backgroundColor);
        img = new BufferedImage(chip.getWidth(null), chip.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.drawImage(chip, 0, 0, null);
        g.setColor(foregroundColor);
        g.drawString(occupationString, TEXT_OFFSET_X, TEXT_OFFSET_Y);
        indicatorImageCache.put(key, img);
        return img;
    }


    private Color getForegroundColor(Color background) {
        
        if (background.getRed() * 0.3
                + background.getGreen() * 0.59
                + background.getBlue() * 0.11 < 126) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
    
    private Color getStringBorderColor(Color color) {
        
        if (color.getRed() * 0.3
                + color.getGreen() * 0.59
                + color.getBlue() * 0.11 < 10) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
    
    public void displayOccupationIndicator(Graphics g, Unit unit, int x, int y) {
        g.drawImage(getOccupationIndicatorImage(unit), x, y, null);
    }

    
    private void displayUnit(Graphics2D g, Unit unit, int x, int y) {
        try {
            
            
            if (viewMode.displayUnitCursor(unit,x,y)) {
                drawCursor(g,x,y);
            }

            
            
            Image image = lib.getUnitImageIcon(unit, unit.getState() == UnitState.SENTRY).getImage();
            Point p = getUnitImagePositionInTile(image, x, y);
            g.drawImage(image, p.x, p.y, null);

            
            displayOccupationIndicator(g, unit, x + (int) (STATE_OFFSET_X * lib.getScalingFactor()), y);

            
            int unitsOnTile = 0;
            if (unit.getTile() != null) {
                
                
                
                unitsOnTile = unit.getTile().getTotalUnitCount();
            }
            if (unitsOnTile > 1) {
                g.setColor(Color.WHITE);
                int unitLinesY = y + OTHER_UNITS_OFFSET_Y;
                for (int i = 0; (i < unitsOnTile) && (i < MAX_OTHER_UNITS); i++) {
                    g.drawLine(x + (int) ((STATE_OFFSET_X + OTHER_UNITS_OFFSET_X) * lib.getScalingFactor()), unitLinesY, x + (int) ((STATE_OFFSET_X + OTHER_UNITS_OFFSET_X + OTHER_UNITS_WIDTH) * lib.getScalingFactor()), unitLinesY);
                    unitLinesY += 2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        if (debugShowMission 
                && freeColClient.getFreeColServer() != null
                && (unit.getOwner().isAI() || unit.hasAbility("model.ability.piracy"))) {
            net.sf.freecol.server.ai.AIUnit au = (net.sf.freecol.server.ai.AIUnit) freeColClient.getFreeColServer().getAIMain().getAIObject(unit);
            if (au != null) {
                g.setColor(Color.WHITE);
                String text = (unit.getOwner().isAI()) ? "" : "(";
                String debuggingInfo = "";
                if (au.getMission() != null) {
                    String missionName = au.getMission().getClass().toString();
                    missionName = missionName.substring(missionName.lastIndexOf('.') + 1);
                    
                    if (FreeCol.usesExperimentalAI() && au.getGoal()!=null) {
                        missionName = "";
                        String goalName = au.getGoal().getDebugDescription();
                        text += goalName;
                    }
                    
                    text += missionName;
                    if (debugShowMissionInfo) {
                        debuggingInfo = au.getMission().getDebuggingInfo();
                    }
                } else {
                    text += "No mission";
                }                
                text += (unit.getOwner().isAI()) ? "" : ")";
                g.drawString(text, x , y);
                g.drawString(debuggingInfo, x , y+25);
            }
        }
    }
    
    
    private Point getUnitImagePositionInTile(Image unitImage, int tileX, int tileY) {
        return getUnitImagePositionInTile(unitImage.getWidth(null), unitImage.getHeight(null), tileX, tileY);
    }
    
    
    private Point getUnitImagePositionInTile(int unitImageWidth, int unitImageHeight, int tileX, int tileY) {
        int unitX = ((tileX + getTileWidth() / 2) - unitImageWidth / 2);
        int unitY = (tileY + getTileHeight() / 2) - unitImageHeight / 2 -
                    (int) (UNIT_OFFSET * lib.getScalingFactor());
        
        return new Point(unitX, unitY);
    }
    
    
    public Point getUnitLabelPositionInTile(JLabel unitLabel, Tile tile) {
        return getUnitLabelPositionInTile(unitLabel, getTilePosition(tile));
    }
    
    
    public Point getUnitLabelPositionInTile(JLabel unitLabel, Point tileP) {
        if (tileP != null) {
            int labelX = tileP.x + getTileWidth() / 2 - unitLabel.getWidth() / 2;
            int labelY = tileP.y + getTileHeight() / 2 - unitLabel.getHeight() / 2 -
                        (int) (UNIT_OFFSET * lib.getScalingFactor());
            
            return new Point(labelX, labelY);
        } else {
            return null;
        }
    }
    
    private void drawCursor(Graphics2D g, int x, int y) {
        g.drawImage(cursorImage, x, y, null);
    }


    
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }


    
    public boolean isInGame() {
        return inGame;
    }


    
    public boolean onScreen(int x, int y) {
        if (bottomRow < 0) {
            positionMap();
            return y - 2 > topRow && y + 4 < bottomRow && x - 1 > leftColumn && x + 2 < rightColumn;
        } else {
            return y - 2 > topRow && y + 4 < bottomRow && x - 1 > leftColumn && x + 2 < rightColumn;
        }
    }


    
    public boolean onScreen(Position position) {
        return onScreen(position.getX(), position.getY());
    }


    
    public Map.Position convertToMapCoordinates(int x, int y) {
        Game gameData = freeColClient.getGame();
        if ((gameData == null) || (gameData.getMap() == null)) {
            return null;
        }

        int leftOffset;
        if (focus.getX() < getLeftColumns()) {
            
            if ((focus.getY() % 2) == 0) {
                leftOffset = tileWidth * focus.getX() + tileWidth / 2;
            } else {
                leftOffset = tileWidth * (focus.getX() + 1);
            }
        } else if (focus.getX() >= (gameData.getMap().getWidth() - getRightColumns())) {
            
            if ((focus.getY() % 2) == 0) {
                leftOffset = size.width - (gameData.getMap().getWidth() - focus.getX()) * tileWidth;
            } else {
                leftOffset = size.width - (gameData.getMap().getWidth() - focus.getX() - 1) * tileWidth - tileWidth / 2;
            }
        } else {
            if ((focus.getY() % 2) == 0) {
                leftOffset = (size.width / 2);
            } else {
                leftOffset = (size.width / 2) + tileWidth / 2;
            }
        }

        int topOffset;
        if (focus.getY() < topRows) {
            
            topOffset = (focus.getY() + 1) * (tileHeight / 2);
        } else if (focus.getY() >= (gameData.getMap().getHeight() - bottomRows)) {
            
            topOffset = size.height - (gameData.getMap().getHeight() - focus.getY()) * (tileHeight / 2);
        } else {
            topOffset = (size.height / 2);
        }

        
        
        

        
        
        
        
        int diffUp = (topOffset - y) / (tileHeight / 4),
            diffLeft = (leftOffset - x) / (tileWidth / 4);

        
        
        int orDiffUp = diffUp,
            orDiffLeft = diffLeft,
            remainderUp = (topOffset - y) % (tileHeight / 4),
            remainderLeft = (leftOffset - x) % (tileWidth / 4);

        if ((diffUp % 2) == 0) {
            diffUp = diffUp / 2;
        } else {
            if (diffUp < 0) {
                diffUp = (diffUp / 2) - 1;
            } else {
                diffUp = (diffUp / 2) + 1;
            }
        }

        if ((diffLeft % 2) == 0) {
            diffLeft = diffLeft / 2;
        } else {
            if (diffLeft < 0) {
                diffLeft = (diffLeft / 2) - 1;
            } else {
                diffLeft = (diffLeft / 2) + 1;
            }
        }

        boolean done = false;
        while (!done) {
            if ((diffUp % 2) == 0) {
                if ((diffLeft % 2) == 0) {
                    diffLeft = diffLeft / 2;
                    done = true;
                } else {
                    
                    if (((orDiffLeft % 2) == 0) && ((orDiffUp % 2) == 0)) {
                        if ((orDiffLeft > 0) && (orDiffUp > 0)) {
                            
                            if ((remainderUp * 2) > remainderLeft) {
                                diffUp++;
                            } else {
                                diffLeft++;
                            }
                        } else if (orDiffUp > 0) {
                            
                            if ((remainderUp * 2) > -remainderLeft) {
                                diffUp++;
                            } else {
                                diffLeft--;
                            }
                        } else if ((orDiffLeft > 0) && (orDiffUp == 0)) {
                            if (remainderUp > 0) {
                                
                                if ((remainderUp * 2) > remainderLeft) {
                                    diffUp++;
                                } else {
                                    diffLeft++;
                                }
                            } else {
                                
                                if ((-remainderUp * 2) > remainderLeft) {
                                    diffUp--;
                                } else {
                                    diffLeft++;
                                }
                            }
                        } else if (orDiffUp == 0) {
                            if (remainderUp > 0) {
                                
                                if ((remainderUp * 2) > -remainderLeft) {
                                    diffUp++;
                                } else {
                                    diffLeft--;
                                }
                            } else {
                                
                                if ((-remainderUp * 2) > -remainderLeft) {
                                    diffUp--;
                                } else {
                                    diffLeft--;
                                }
                            }
                        } else if (orDiffLeft > 0) {
                            
                            if ((-remainderUp * 2) > remainderLeft) {
                                diffUp--;
                            } else {
                                diffLeft++;
                            }
                        } else {
                            
                            if ((-remainderUp * 2) > -remainderLeft) {
                                diffUp--;
                            } else {
                                diffLeft--;
                            }
                        }
                    } else if ((orDiffLeft % 2) == 0) {
                        if ((orDiffLeft > 0) && (orDiffUp > 0)) {
                            
                            if ((remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                diffLeft++;
                            } else {
                                diffUp--;
                            }
                        } else if (orDiffUp > 0) {
                            
                            if ((remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                diffLeft--;
                            } else {
                                diffUp--;
                            }
                        } else if (orDiffLeft > 0) {
                            
                            if ((-remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                diffLeft++;
                            } else {
                                diffUp++;
                            }
                        } else {
                            
                            if ((-remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                diffLeft--;
                            } else {
                                diffUp++;
                            }
                        }
                    } else if ((orDiffUp % 2) == 0) {
                        if ((orDiffLeft > 0) && (orDiffUp > 0)) {
                            
                            if ((remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                diffUp++;
                            } else {
                                diffLeft--;
                            }
                        } else if (orDiffUp > 0) {
                            
                            if ((remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                diffUp++;
                            } else {
                                diffLeft++;
                            }
                        } else if ((orDiffLeft > 0) && (orDiffUp == 0)) {
                            if (remainderUp > 0) {
                                
                                if ((remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                    diffUp++;
                                } else {
                                    diffLeft--;
                                }
                            } else {
                                
                                if ((-remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                    diffUp--;
                                } else {
                                    diffLeft--;
                                }
                            }
                        } else if (orDiffUp == 0) {
                            if (remainderUp > 0) {
                                
                                if ((remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                    diffUp++;
                                } else {
                                    diffLeft++;
                                }
                            } else {
                                
                                if ((-remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                    diffUp--;
                                } else {
                                    diffLeft++;
                                }
                            }
                        } else if (orDiffLeft > 0) {
                            
                            if ((-remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                diffUp--;
                            } else {
                                diffLeft--;
                            }
                        } else {
                            
                            if ((-remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                diffUp--;
                            } else {
                                diffLeft++;
                            }
                        }
                    } else {
                        if ((orDiffLeft > 0) && (orDiffUp > 0)) {
                            
                            if ((remainderUp * 2) > remainderLeft) {
                                diffLeft--;
                            } else {
                                diffUp--;
                            }
                        } else if (orDiffUp > 0) {
                            
                            if ((remainderUp * 2) > -remainderLeft) {
                                diffLeft++;
                            } else {
                                diffUp--;
                            }
                        } else if (orDiffLeft > 0) {
                            
                            if ((-remainderUp * 2) > remainderLeft) {
                                diffLeft--;
                            } else {
                                diffUp++;
                            }
                        } else {
                            
                            if ((-remainderUp * 2) > -remainderLeft) {
                                diffLeft++;
                            } else {
                                diffUp++;
                            }
                        }
                    }
                }
            } else {
                if ((diffLeft % 2) == 0) {
                    
                    if (((orDiffLeft % 2) == 0) && ((orDiffUp % 2) == 0)) {
                        if ((orDiffLeft > 0) && (orDiffUp > 0)) {
                            
                            if ((remainderUp * 2) > remainderLeft) {
                                diffUp++;
                            } else {
                                diffLeft++;
                            }
                        } else if (orDiffLeft > 0) {
                            
                            if ((-remainderUp * 2) > remainderLeft) {
                                diffUp--;
                            } else {
                                diffLeft++;
                            }
                        } else if ((orDiffUp > 0) && (orDiffLeft == 0)) {
                            if (remainderLeft > 0) {
                                
                                if ((remainderUp * 2) > remainderLeft) {
                                    diffUp++;
                                } else {
                                    diffLeft++;
                                }
                            } else {
                                
                                if ((remainderUp * 2) > -remainderLeft) {
                                    diffUp++;
                                } else {
                                    diffLeft--;
                                }
                            }
                        } else if (orDiffLeft == 0) {
                            if (remainderLeft > 0) {
                                
                                if ((-remainderUp * 2) > remainderLeft) {
                                    diffUp--;
                                } else {
                                    diffLeft++;
                                }
                            } else {
                                
                                if ((-remainderUp * 2) > -remainderLeft) {
                                    diffUp--;
                                } else {
                                    diffLeft--;
                                }
                            }
                        } else if (orDiffUp > 0) {
                            
                            if ((remainderUp * 2) > -remainderLeft) {
                                diffUp++;
                            } else {
                                diffLeft--;
                            }
                        } else {
                            
                            if ((-remainderUp * 2) > -remainderLeft) {
                                diffUp--;
                            } else {
                                diffLeft--;
                            }
                        }
                    } else if ((orDiffLeft % 2) == 0) {
                        if ((orDiffLeft > 0) && (orDiffUp > 0)) {
                            
                            if ((remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                diffLeft++;
                            } else {
                                diffUp--;
                            }
                        } else if (orDiffLeft > 0) {
                            
                            if ((-remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                diffLeft++;
                            } else {
                                diffUp++;
                            }
                        } else if ((orDiffUp > 0) && (orDiffLeft == 0)) {
                            if (remainderLeft > 0) {
                                
                                if ((remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                    diffLeft++;
                                } else {
                                    diffUp--;
                                }
                            } else {
                                
                                if ((remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                    diffLeft--;
                                } else {
                                    diffUp--;
                                }
                            }
                        } else if (orDiffLeft == 0) {
                            if (remainderLeft > 0) {
                                
                                if ((-remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                    diffLeft++;
                                } else {
                                    diffUp++;
                                }
                            } else {
                                
                                if ((-remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                    diffLeft--;
                                } else {
                                    diffUp++;
                                }
                            }
                        } else if (orDiffUp > 0) {
                            
                            if ((remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                diffLeft--;
                            } else {
                                diffUp--;
                            }
                        } else {
                            
                            if ((-remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                diffLeft--;
                            } else {
                                diffUp++;
                            }
                        }
                    } else if ((orDiffUp % 2) == 0) {
                        if ((orDiffLeft > 0) && (orDiffUp > 0)) {
                            
                            if ((remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                diffUp++;
                            } else {
                                diffLeft--;
                            }
                        } else if (orDiffUp > 0) {
                            
                            if ((remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                diffUp++;
                            } else {
                                diffLeft++;
                            }
                        } else if (orDiffLeft > 0) {
                            
                            if ((-remainderUp * 2 + remainderLeft) > (tileWidth / 4)) {
                                diffUp--;
                            } else {
                                diffLeft--;
                            }
                        } else {
                            
                            if ((-remainderUp * 2 - remainderLeft) > (tileWidth / 4)) {
                                diffUp--;
                            } else {
                                diffLeft++;
                            }
                        }
                    } else {
                        if ((orDiffLeft > 0) && (orDiffUp > 0)) {
                            
                            if ((remainderUp * 2) > remainderLeft) {
                                diffLeft--;
                            } else {
                                diffUp--;
                            }
                        } else if (orDiffUp > 0) {
                            
                            if ((remainderUp * 2) > -remainderLeft) {
                                diffLeft++;
                            } else {
                                diffUp--;
                            }
                        } else if (orDiffLeft > 0) {
                            
                            if ((-remainderUp * 2) > remainderLeft) {
                                diffLeft--;
                            } else {
                                diffUp++;
                            }
                        } else {
                            
                            if ((-remainderUp * 2) > -remainderLeft) {
                                diffLeft++;
                            } else {
                                diffUp++;
                            }
                        }
                    }
                } else {
                    if ((focus.getY() % 2) == 0) {
                        if (diffLeft < 0) {
                            diffLeft = diffLeft / 2;
                        } else {
                            diffLeft = (diffLeft / 2) + 1;
                        }
                    } else {
                        if (diffLeft < 0) {
                            diffLeft = (diffLeft / 2) - 1;
                        } else {
                            diffLeft = diffLeft / 2;
                        }
                    }
                    done = true;
                }
            }
        }
        return new Map.Position(focus.getX() - diffLeft, focus.getY() - diffUp);
    }


    
    public boolean isMapNearTop(int y) {
        if (y < topRows) {
            return true;
        } else {
            return false;
        }
    }


    
    public boolean isMapNearBottom(int y) {
        if (y >= (freeColClient.getGame().getMap().getHeight() - bottomRows)) {
            return true;
        } else {
            return false;
        }
    }


    
    public boolean isMapNearLeft(int x, int y) {
        if (x < getLeftColumns(y)) {
            return true;
        } else {
            return false;
        }
    }


    
    public boolean isMapNearRight(int x, int y) {
        if (x >= (freeColClient.getGame().getMap().getWidth() - getRightColumns(y))) {
            return true;
        } else {
            return false;
        }
    }


    
    public ImageLibrary getImageLibrary() {
        return lib;
    }

    
    public Point getTilePosition(Tile t) {
        if (t.getY() >= topRow 
                && t.getY() <= bottomRow 
                && t.getX() >= leftColumn 
                && t.getX() <= rightColumn) {
            int x = ((t.getX() - leftColumn) * tileWidth) + leftColumnX;
            int y = ((t.getY() - topRow) * tileHeight / 2) + topRowY;
            if ((t.getY() % 2) != 0) {     
                x += tileWidth / 2;
            }
            return new Point(x, y);
        } else {
            return null;
        }
    }
    
    
    public Rectangle getTileBounds(Tile tile) {
        return getTileBounds(tile.getX(), tile.getY());
    }
    
    
    public Rectangle getTileBounds(int x, int y) {
        Rectangle result = new Rectangle(0, 0, size.width, size.height);
        if (y >= topRow && y <= bottomRow && x >= leftColumn && x <= rightColumn) {
            result.y = ((y - topRow) * tileHeight / 2) + topRowY - tileHeight;
            result.x = ((x - leftColumn) * tileWidth) + leftColumnX;
            if ((y % 2) != 0) {
                result.x += tileWidth / 2;
            }
            result.width = tileWidth;
            result.height = tileHeight * 2;
        }
        return result;
    }


    
    public void forceReposition() {
        bottomRow = -1;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }
}
