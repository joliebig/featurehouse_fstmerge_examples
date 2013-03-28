

package net.sf.freecol.client.control;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.Canvas.MissionaryAction;
import net.sf.freecol.client.gui.Canvas.ScoutAction;
import net.sf.freecol.client.gui.Canvas.TradeAction;
import net.sf.freecol.client.gui.action.BuildColonyAction;
import net.sf.freecol.client.gui.animation.Animations;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.option.FreeColActionUI;
import net.sf.freecol.client.gui.panel.ChoiceItem;
import net.sf.freecol.client.gui.panel.ConfirmDeclarationDialog;
import net.sf.freecol.client.gui.panel.DeclarationDialog;
import net.sf.freecol.client.gui.panel.EventPanel;
import net.sf.freecol.client.gui.panel.PreCombatDialog;
import net.sf.freecol.client.gui.panel.ReportTurnPanel;
import net.sf.freecol.client.gui.panel.SelectDestinationDialog;
import net.sf.freecol.client.gui.panel.TradeRouteDialog;
import net.sf.freecol.client.gui.sound.SoundLibrary.SoundEffect;
import net.sf.freecol.client.networking.Client;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.AbstractUnit;
import net.sf.freecol.common.model.BuildableType;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.DiplomaticTrade;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.ExportData;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsContainer;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Nameable;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Region;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.common.model.TileItemContainer;
import net.sf.freecol.common.model.TradeRoute;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.WorkLocation;
import net.sf.freecol.common.model.CombatModel.CombatResult;
import net.sf.freecol.common.model.CombatModel.CombatResultType;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.TradeRoute.Stop;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.UnitTypeChange.ChangeType;
import net.sf.freecol.common.networking.AskSkillMessage;
import net.sf.freecol.common.networking.BuildColonyMessage;
import net.sf.freecol.common.networking.BuyMessage;
import net.sf.freecol.common.networking.BuyPropositionMessage;
import net.sf.freecol.common.networking.CashInTreasureTrainMessage;
import net.sf.freecol.common.networking.ChatMessage;
import net.sf.freecol.common.networking.ClaimLandMessage;
import net.sf.freecol.common.networking.CloseTransactionMessage;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.DebugForeignColonyMessage;
import net.sf.freecol.common.networking.DeclareIndependenceMessage;
import net.sf.freecol.common.networking.DeliverGiftMessage;
import net.sf.freecol.common.networking.DemandTributeMessage;
import net.sf.freecol.common.networking.DiplomacyMessage;
import net.sf.freecol.common.networking.DisembarkMessage;
import net.sf.freecol.common.networking.EmbarkMessage;
import net.sf.freecol.common.networking.EmigrateUnitMessage;
import net.sf.freecol.common.networking.GetTransactionMessage;
import net.sf.freecol.common.networking.GoodsForSaleMessage;
import net.sf.freecol.common.networking.InciteMessage;
import net.sf.freecol.common.networking.JoinColonyMessage;
import net.sf.freecol.common.networking.LearnSkillMessage;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.MissionaryMessage;
import net.sf.freecol.common.networking.MoveMessage;
import net.sf.freecol.common.networking.NetworkConstants;
import net.sf.freecol.common.networking.NewLandNameMessage;
import net.sf.freecol.common.networking.NewRegionNameMessage;
import net.sf.freecol.common.networking.RenameMessage;
import net.sf.freecol.common.networking.ScoutIndianSettlementMessage;
import net.sf.freecol.common.networking.SellMessage;
import net.sf.freecol.common.networking.SellPropositionMessage;
import net.sf.freecol.common.networking.SetDestinationMessage;
import net.sf.freecol.common.networking.SpySettlementMessage;
import net.sf.freecol.common.networking.StatisticsMessage;
import net.sf.freecol.common.networking.UpdateCurrentStopMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class InGameController implements NetworkConstants {

    private static final Logger logger = Logger.getLogger(InGameController.class.getName());

    private final FreeColClient freeColClient;

    
    private boolean endingTurn = false;

    
    private boolean canAutoEndTurn = false;
    
    
    private boolean executeGoto = false;

    
    private HashMap<String, Integer> messagesToIgnore = new HashMap<String, Integer>();

    
    private ArrayList<File> allSaveGames = new ArrayList<File>();

    
    public InGameController(FreeColClient freeColClient) {
        this.freeColClient = freeColClient;
    }

    
    public void saveGame() {
        Canvas canvas = freeColClient.getCanvas();
        Player player = freeColClient.getMyPlayer();
        String fileName = player.getName() + "_" + player.getNationAsString()
            + "_" + freeColClient.getGame().getTurn().toSaveGameString();
        fileName = fileName.replaceAll(" ", "_");
        if (freeColClient.canSaveCurrentGame()) {
            final File file = canvas.showSaveDialog(FreeCol.getSaveDirectory(), fileName);
            if (file != null) {
                FreeCol.setSaveDirectory(file.getParentFile());
                saveGame(file);
            }
        }
    }

    
    public void saveGame(final File file) {
        Canvas canvas = freeColClient.getCanvas();
        canvas.showStatusPanel(Messages.message("status.savingGame"));
        try {
            freeColClient.getFreeColServer().saveGame(file, freeColClient.getMyPlayer().getName());
            canvas.closeStatusPanel();
        } catch (IOException e) {
            canvas.errorMessage("couldNotSaveGame");
        }
        canvas.requestFocusInWindow();
    }

    
    public void loadGame() {
        Canvas canvas = freeColClient.getCanvas();
        File file = canvas.showLoadDialog(FreeCol.getSaveDirectory());
        if (file == null) {
            return;
        }
        if (!file.isFile()) {
            canvas.errorMessage("fileNotFound");
            return;
        }
        if (!canvas.showConfirmDialog("stopCurrentGame.text",
                                      "stopCurrentGame.yes",
                                      "stopCurrentGame.no")) {
            return;
        }

        freeColClient.getConnectController().quitGame(true);
        canvas.removeInGameComponents();
        freeColClient.getConnectController().loadGame(file);
    }
    
    
    public void setInDebugMode(boolean debug) {
        FreeCol.setInDebugMode(debug);
        freeColClient.updateMenuBar();
    }

    
    private Element askExpecting(Client client, Element element, String tag) {
        
        Element reply = null;
        try {
            reply = client.ask(element);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not send " + element, e);
            return null;
        }
        if (reply == null) {
            logger.warning("Received null reply to " + element);
            return null;
        }

        
        if (tag.equals(reply.getTagName())) {
            return reply;
        }

        
        if ("error".equals(reply.getTagName())) {
            String messageID = null;
            String message = null;
            if (element.hasAttribute("message")) {
                message = element.getAttribute("message");
                logger.warning(message);
            } else {
                logger.warning("Received error response to " + element);
            }
            if (element.hasAttribute("messageID")) {
                messageID = element.getAttribute("messageID");
            }
            if (messageID != null || FreeCol.isInDebugMode()) {
                freeColClient.getCanvas().errorMessage(messageID, message);
            }
            return null;
        }

        
        logger.warning("Received reply"
                       + " with tag " + reply.getTagName()
                       + " which should have been " + tag
                       + " to message " + element);
        return null;
    }


    
    public void setCurrentPlayer(Player player) {
        logger.finest("Entering client setCurrentPlayer("
                      + player.getName() + ")");
        Game game = freeColClient.getGame();
        game.setCurrentPlayer(player);

        if (freeColClient.getMyPlayer().equals(player)) {
            autosaveGame();
            removeUnitsOutsideLOS();

            if (player.checkEmigrate()) {
                if (player.hasAbility("model.ability.selectRecruit")
                    && player.getEurope().recruitablesDiffer()) {
                    Canvas canvas = freeColClient.getCanvas();
                    int index = canvas.showEmigrationPanel(false);
                    emigrateUnitInEurope(index + 1);
                } else {
                    emigrateUnitInEurope(0);
                }
            }

            if (!freeColClient.isSingleplayer()) {
                freeColClient.playSound(player.getNation().getAnthem());
            }
            
            checkTradeRoutesInEurope();
            displayModelMessages(true);
            nextActiveUnit();
        }
        logger.finest("Exiting client setCurrentPlayer("
                      + player.getName() + ")");
    }

    
    private void autosaveGame() {
        if (freeColClient.getFreeColServer() != null) {
            Game game = freeColClient.getGame();
            ClientOptions options = freeColClient.getClientOptions();
            int savegamePeriod = options.getInteger(ClientOptions.AUTOSAVE_PERIOD);
            int turnNumber = game.getTurn().getNumber();
            if (savegamePeriod == 1 || (savegamePeriod != 0
                                        && turnNumber % savegamePeriod == 0)) {
                String filename = Messages.message("clientOptions.savegames.autosave.fileprefix")
                    + '-' + game.getTurn().toSaveGameString() + ".fsg";
                File saveGameFile = new File(FreeCol.getAutosaveDirectory(),
                                             filename);
                saveGame(saveGameFile);
                int generations = options.getInteger(ClientOptions.AUTOSAVE_GENERATIONS);
                if (generations > 0) {
                    allSaveGames.add(saveGameFile);
                    if (allSaveGames.size() > generations) {
                        File fileToDelete = allSaveGames.remove(0);
                        fileToDelete.delete();
                    }
                }
            }
        }
    }

    
    private void removeUnitsOutsideLOS() {
        Player player = freeColClient.getMyPlayer();
        Map map = freeColClient.getGame().getMap();

        player.resetCanSeeTiles();

        Iterator<Position> tileIterator = map.getWholeMapIterator();
        while (tileIterator.hasNext()) {
            Tile t = map.getTile(tileIterator.next());
            if (t != null && !player.canSee(t) && t.getFirstUnit() != null) {
                if (t.getFirstUnit().getOwner() == player) {
                    logger.warning("Could not see one of my own units!");
                }
                t.disposeAllUnits();
            }
        }

        player.resetCanSeeTiles();
    }

    
    private void emigrateUnitInEurope(int slot) {
        Game game = freeColClient.getGame();
        Player player = freeColClient.getMyPlayer();
        if (game.getCurrentPlayer() != player) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        askEmigrate(slot);
    }

    
    private void askEmigrate(int slot) {
        Client client = freeColClient.getClient();
        EmigrateUnitMessage message = new EmigrateUnitMessage(slot);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
    }

    
    public void moveActiveUnit(Direction direction) {
        if (freeColClient.getGame().getCurrentPlayer()
            != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Unit unit = freeColClient.getGUI().getActiveUnit();
        if (unit != null) {
            clearGotoOrders(unit);
            move(unit, direction);

            
            
            
            boolean alwaysCenter = freeColClient.getClientOptions().getBoolean(ClientOptions.ALWAYS_CENTER);
            if (alwaysCenter && unit.getTile() != null) {
                centerOnUnit(unit);
            }
        } 
    }

    
    public void selectDestination(Unit unit) {
        final Player player = freeColClient.getMyPlayer();
        Map map = freeColClient.getGame().getMap();

        Canvas canvas = freeColClient.getCanvas();
        Location destination = canvas.showFreeColDialog(new SelectDestinationDialog(canvas, unit));

        if (destination == null) {
            
            return;
        }

        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            setDestination(unit, destination);
            return;
        }

        if (destination instanceof Europe && unit.getTile() != null
            && (unit.getTile().canMoveToEurope() || map.isAdjacentToMapEdge(unit.getTile()))) {
            moveToEurope(unit);
            nextActiveUnit();
        } else {
            setDestination(unit, destination);
            moveToDestination(unit);
        }
    }


    
    public void moveToDestination(Unit unit) {
        final Canvas canvas = freeColClient.getCanvas();
        final Map map = freeColClient.getGame().getMap();
        final Location destination = unit.getDestination();

        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            canvas.showInformationMessage("notYourTurn");
            return;
        }

        if (unit.getTradeRoute() != null) {
            Stop currStop = unit.getCurrentStop();
            if (!TradeRoute.isStopValid(unit, currStop)) {
                String oldTradeRouteName = unit.getTradeRoute().getName();
                logger.info("Trade unit " + unit.getId()
                            + " in route " + oldTradeRouteName
                            + " cannot continue: stop invalid.");
                canvas.showInformationMessage("traderoute.broken",
                                              "%name%", oldTradeRouteName);
                clearOrders(unit);
                return;
            }
        	
            if (unit.getLocation().getTile() == currStop.getLocation().getTile()) {
                
                logger.info("Trade unit " + unit.getId()
                            + " in route " + unit.getTradeRoute().getName()
                            + " is at " + unit.getCurrentStop().getLocation().getLocationName());
                followTradeRoute(unit);
                return;
            } else {
                logger.info("Unit " + unit.getId()
                            + " is a trade unit in route " + unit.getTradeRoute().getName()
                            + ", going to " + unit.getCurrentStop().getLocation().getLocationName());
            }
        } else {
            logger.info("Moving unit " + unit.getId()
                        + " to position " + unit.getDestination().getLocationName());
        }

        
        
        if (!(destination instanceof Europe)
            && (destination.getTile() == null
                || unit.getTile() == destination.getTile())) {
            clearGotoOrders(unit);
            return;
        }
        
        PathNode path;
        if (destination instanceof Europe) {
            path = map.findPathToEurope(unit, unit.getTile());
        } else {
            path = map.findPath(unit, unit.getTile(), destination.getTile());
        }

        if (path == null) {
            canvas.showInformationMessage("selectDestination.failed", unit,
                                          "%destination%", destination.getLocationName());
            setDestination(unit, null);
            return;
        }

        while (path != null) {
            MoveType mt = unit.getMoveType(path.getDirection());
            switch (mt) {
            case MOVE:
                moveMove(unit, path.getDirection());
                break;
            case EXPLORE_LOST_CITY_RUMOUR:
                moveExplore(unit, path.getDirection());
                if (unit.isDisposed())
                    return;
                break;
            case MOVE_HIGH_SEAS:
                if (destination instanceof Europe) {
                    moveToEurope(unit);
                    path = null;
                } else if (path == path.getLastNode()) {
                    move(unit, path.getDirection());
                    path = null;
                } else {
                    moveMove(unit, path.getDirection());
                }
                break;
            case DISEMBARK:
                moveDisembark(unit, path.getDirection());
                path = null;
                break;
            case MOVE_NO_MOVES:
                
                
                unit.setMovesLeft(0);
                return;
            default:
                if (path == path.getLastNode() && mt.isLegal()
                    && (mt != MoveType.ATTACK || knownEnemyOnLastTile(path))) {
                    move(unit, path.getDirection());
                    
                    if (unit.isDisposed()) {
                        return;
                    }
                } else {
                    freeColClient.getGUI().setActiveUnit(unit);
                    return;
                }
            }
            if (path != null) {
                path = path.next;
            }
        }

        if (unit.getTile() != null && destination instanceof Europe
            && map.isAdjacentToMapEdge(unit.getTile())) {
            moveToEurope(unit);
        }

        
        
        if (unit.getTradeRoute() == null) {
            setDestination(unit, null);
        } else {
            followTradeRoute(unit);
        }

        
        
        if (checkCashInTreasureTrain(unit)) {
            unit = null;
        }

        if (unit != null && unit.getMovesLeft() > 0 && unit.getTile() != null) {
            freeColClient.getGUI().setActiveUnit(unit);
        } else if (unit == null || freeColClient.getGUI().getActiveUnit() == unit) {
            nextActiveUnit();
        }
        return;
    }

    private boolean knownEnemyOnLastTile(PathNode path) {
        if ((path != null) && path.getLastNode() != null) {
            Tile tile = path.getLastNode().getTile();
            return ((tile.getFirstUnit() != null &&
                     tile.getFirstUnit().getOwner() != freeColClient.getMyPlayer()) ||
                    (tile.getSettlement() != null &&
                     tile.getSettlement().getOwner() != freeColClient.getMyPlayer()));
        } else {
            return false;
        }
    }

    private void checkTradeRoutesInEurope() {
        Europe europe = freeColClient.getMyPlayer().getEurope();
        if (europe == null) {
            return;
        }
        List<Unit> units = europe.getUnitList();
        for(Unit unit : units) {
            
            
            if (unit.getTradeRoute() != null && unit.isInEurope()) {
                followTradeRoute(unit);
            }
        }
    }

    private void followTradeRoute(Unit unit) {
        Stop stop = unit.getCurrentStop();
        if (!TradeRoute.isStopValid(unit, stop)) {
            freeColClient.getCanvas().showInformationMessage("traderoute.broken",
                                                             "%name%",
                                                             unit.getTradeRoute().getName());
            return;
        }

        boolean inEurope = unit.isInEurope();
        
        
        if (freeColClient.getMyPlayer().getEurope() == stop.getLocation() &&
            !inEurope) {
            return;
        }
        
        
        
        if (unit.getInitialMovesLeft() == unit.getMovesLeft()){
            Stop oldStop = unit.getCurrentStop();
                
            if (inEurope) {
                buyTradeGoodsFromEurope(unit);
            } else {
                loadTradeGoodsFromColony(unit);
            }
              
            
            UpdateCurrentStopMessage message = new UpdateCurrentStopMessage(unit);
            Element reply = askExpecting(freeColClient.getClient(),
                                         message.toXMLElement(), "update");
            if (reply != null) {
                freeColClient.getInGameInputHandler().update(reply);

                Stop nextStop = unit.getCurrentStop();
                
                
                if (nextStop != null && nextStop.getLocation() != unit.getColony()) {
                    if (unit.isInEurope()) {
                        moveToAmerica(unit);
                    } else {
                        moveToDestination(unit);
                    }
                }
            }

            
            
            if (oldStop.getLocation().getTile() == unit.getCurrentStop().getLocation().getTile()){
                unit.setMovesLeft(0);
            }
        } else {
            
            
            logger.info("Trade unit " + unit.getId() + " in route " + unit.getTradeRoute().getName() +
                        " arrives at " + unit.getCurrentStop().getLocation().getLocationName());
                
            if (inEurope) {
                sellTradeGoodsInEurope(unit);
            } else {
                unloadTradeGoodsToColony(unit);
            }               
            unit.setMovesLeft(0);
        }
    }
    
    private void loadTradeGoodsFromColony(Unit unit){
        Stop stop = unit.getCurrentStop();
        Location location = unit.getColony();

        logger.info("Trade unit " + unit.getId() + " loading in " + location.getLocationName());
        
        GoodsContainer warehouse = location.getGoodsContainer();
        if (warehouse == null) {
            throw new IllegalStateException("No warehouse in a stop's location");
        }
        
        ArrayList<GoodsType> goodsTypesToLoad = stop.getCargo();
        Iterator<Goods> goodsIterator = unit.getGoodsIterator();
        
        
        while (goodsIterator.hasNext()) {
            Goods goods = goodsIterator.next();
            if (goods.getAmount() < 100) {
                for (int index = 0; index < goodsTypesToLoad.size(); index++) {
                    GoodsType goodsType = goodsTypesToLoad.get(index);
                    ExportData exportData =   unit.getColony().getExportData(goodsType);
                    if (goods.getType() == goodsType) {
                        
                        
                        int amountPresent = warehouse.getGoodsCount(goodsType) - exportData.getExportLevel();
                        if (amountPresent > 0) {
                            logger.finest("Automatically loading goods " + goods.getName());
                            int amountToLoad = Math.min(100 - goods.getAmount(), amountPresent);
                            loadCargo(new Goods(freeColClient.getGame(), location, goods.getType(),
                                                amountToLoad), unit);
                        }
                    }
                    
                    
                    goodsTypesToLoad.remove(index);
                    break;
                }
            }   
        }
        
        
        
        for (GoodsType goodsType : goodsTypesToLoad) {
            
            if (unit.getSpaceLeft() == 0) {
                break;
            }
                
            
            ExportData exportData = unit.getColony().getExportData(goodsType);
                
            int amountPresent = warehouse.getGoodsCount(goodsType) - exportData.getExportLevel();
            
            if (amountPresent > 0){
                logger.finest("Automatically loading goods " + goodsType.getName());
                loadCargo(new Goods(freeColClient.getGame(), location, goodsType,
                                    Math.min(amountPresent, 100)), unit);
            } else {
                logger.finest("Can not load " + goodsType.getName() + " due to export settings.");
            }
        }
        
    }
    
    private void unloadTradeGoodsToColony(Unit unit){
        Stop stop = unit.getCurrentStop();
        Location location = unit.getColony();
        
        logger.info("Trade unit " + unit.getId() + " unloading in " + location.getLocationName());
        
        GoodsContainer warehouse = location.getGoodsContainer();
        if (warehouse == null) {
            throw new IllegalStateException("No warehouse in a stop's location");
        }
        
        ArrayList<GoodsType> goodsTypesToKeep = stop.getCargo();
        Iterator<Goods> goodsIterator = unit.getGoodsIterator();
        
        while (goodsIterator.hasNext()) {
            Goods goods = goodsIterator.next();
            boolean toKeep = false;
            
            for (int index = 0; index < goodsTypesToKeep.size(); index++) {
                
                GoodsType goodsType = goodsTypesToKeep.get(index);
                if (goods.getType() == goodsType) {
                    
                    
                    goodsTypesToKeep.remove(index);
                    toKeep = true;
                    break;
                }
            }
                
            
            if(toKeep)
                continue;
                
            
            String colonyName = ((Colony) location).getName();
            boolean all;
            int capacity = ((Colony) location).getWarehouseCapacity()
                - warehouse.getGoodsCount(goods.getType());
            int overflow = goods.getAmount() - capacity;
            if (overflow <= 0) { 
                all = true;
                logger.finest("Automatically unloading: "
                              + Integer.toString(goods.getAmount())
                              + " " + goods.getName()
                              + " at " + colonyName);
            } else { 
                Canvas canvas = freeColClient.getCanvas();
                int option = freeColClient.getClientOptions()
                    .getInteger(ClientOptions.UNLOAD_OVERFLOW_RESPONSE);
                switch (option) {
                case ClientOptions.UNLOAD_OVERFLOW_RESPONSE_ASK:
                    String msg = Messages.message("traderoute.warehouseCapacity",
                                                  "%unit%", unit.getName(),
                                                  "%colony%", colonyName,
                                                  "%amount%", String.valueOf(overflow),
                                                  "%goods%", goods.getName());
                    all = canvas.showConfirmDialog(msg, "yes", "no");
                    break;
                case ClientOptions.UNLOAD_OVERFLOW_RESPONSE_NEVER:
                    all = false;
                    break;
                case ClientOptions.UNLOAD_OVERFLOW_RESPONSE_ALWAYS:
                    all = true;
                    break;
                default:
                    logger.warning("Illegal UNLOAD_OVERFLOW_RESPONSE: "
                                   + Integer.toString(option));
                    continue; 
                }
                if (all) {
                    logger.finest("Automatically unloading: "
                                  + Integer.toString(goods.getAmount())
                                  + " " + goods.getName()
                                  + " at " + colonyName
                                  + " overflowing " + Integer.toString(overflow));
                } else {
                    logger.finest("Automatically unloading: "
                                  + Integer.toString(capacity)
                                  + " " + goods.getName()
                                  + " at " + colonyName
                                  + " retaining " + Integer.toString(overflow));
                }
                if (option != ClientOptions.UNLOAD_OVERFLOW_RESPONSE_ASK) {
                    String whichMessage = (all) ? "traderoute.overflow"
                        : "traderoute.nounload";
                    Player player = freeColClient.getMyPlayer();
                    ModelMessage m = new ModelMessage(player,
                                                      ModelMessage.MessageType.WAREHOUSE_CAPACITY,
                                                      player,
                                                      whichMessage,
                                                      "%colony%", colonyName,
                                                      "%unit%", unit.getName(),
                                                      "%overflow%", String.valueOf(overflow),
                                                      "%goods%", goods.getName());
                    player.addModelMessage(m);
                }
            }
            if (all) {
                unloadCargo(goods);
            } else {
                unloadCargo(new Goods(freeColClient.getGame(), unit,
                                      goods.getType(), capacity));
            }
        }
    }
    
    private void sellTradeGoodsInEurope(Unit unit) {

        Stop stop = unit.getCurrentStop();

        
        ArrayList<GoodsType> goodsTypesToLoad = stop.getCargo();
        Iterator<Goods> goodsIterator = unit.getGoodsIterator();
        while (goodsIterator.hasNext()) {
            Goods goods = goodsIterator.next();
            boolean toKeep = false;
            for (int index = 0; index < goodsTypesToLoad.size(); index++) {
                GoodsType goodsType = goodsTypesToLoad.get(index);
                if (goods.getType() == goodsType) {
                    
                    
                    goodsTypesToLoad.remove(index);
                    toKeep = true;
                    break;
                }
            }
            if(toKeep)
                continue;
            
            
            logger.finest("Automatically unloading " + goods.getName());
            sellGoods(goods);
        }
    }
    
    private void buyTradeGoodsFromEurope(Unit unit) {

        Stop stop = unit.getCurrentStop();

        
        ArrayList<GoodsType> goodsTypesToLoad = stop.getCargo();
        Iterator<Goods> goodsIterator = unit.getGoodsIterator();
        while (goodsIterator.hasNext()) {
            Goods goods = goodsIterator.next();
            for (int index = 0; index < goodsTypesToLoad.size(); index++) {
                GoodsType goodsType = goodsTypesToLoad.get(index);
                if (goods.getType() == goodsType) {
                    if (goods.getAmount() < 100) {
                        logger.finest("Automatically loading goods " + goods.getName());
                        buyGoods(goods.getType(), (100 - goods.getAmount()), unit);
                    }
                    
                    
                    goodsTypesToLoad.remove(index);
                    break;
                }
            }
        }

        
        for (GoodsType goodsType : goodsTypesToLoad) {
            if (unit.getSpaceLeft() > 0) {
                logger.finest("Automatically loading goods " + goodsType.getName());
                buyGoods(goodsType, 100, unit);
            }
        }
    }

    
    
    
    
    
    
    
    

    
    public void declareIndependence() {
        Canvas canvas = freeColClient.getCanvas();
        Player player = freeColClient.getMyPlayer();
        if (freeColClient.getGame().getCurrentPlayer() != player) {
            canvas.showInformationMessage("notYourTurn");
            return;
        }

        
        if (player.getSoL() < 50) {
            canvas.showInformationMessage("declareIndependence.notMajority",
                FreeCol.getSpecification().getGoodsType("model.goods.bells"),
                "%percentage%", Integer.toString(player.getSoL()));
            return;
        }

        
        List<String> names = canvas.showFreeColDialog(new ConfirmDeclarationDialog(canvas));
        if (names == null
            || names.get(0) == null || names.get(0).length() == 0
            || names.get(1) == null || names.get(1).length() == 0) {
            
            return;
        }

        
        String nationName = names.get(0);
        String countryName = names.get(1);
        if (askDeclare(nationName, countryName)
            && player.getPlayerType() == PlayerType.REBEL) {
            canvas.showFreeColDialog(new DeclarationDialog(canvas));
            freeColClient.getActionManager().update();
            nextModelMessage();
        }
    }

    
    private boolean askDeclare(String nation, String country) {
        Client client = freeColClient.getClient();
        DeclareIndependenceMessage message
            = new DeclareIndependenceMessage(nation, country);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }


    
    public void sendChat(String message) {
        ChatMessage chatMessage
            = new ChatMessage(freeColClient.getMyPlayer(), message, false);
        freeColClient.getClient().sendAndWait(chatMessage.toXMLElement());
    }


    
    public void rename(Nameable object) {
        Player player = freeColClient.getMyPlayer();
        if (!(object instanceof Ownable)
            || ((Ownable) object).getOwner() != player) {
            return;
        }

        Canvas canvas = freeColClient.getCanvas();
        String name = null;
        if (object instanceof Colony) {
            name = canvas.showInputDialog("renameColony.text",
                                          object.getName(),
                                          "renameColony.yes",
                                          "renameColony.no");
            if (name == null || name.length() == 0) {
                return; 
            }
            if (player.getSettlement(name) != null) {
                
                canvas.showInformationMessage("nameColony.notUnique",
                                              "%name%", name);
                return;
            }
        } else if (object instanceof Unit) {
            name = canvas.showInputDialog("renameUnit.text",
                                          object.getName(),
                                          "renameUnit.yes",
                                          "renameUnit.no",
                                          false);
            if (name == null) {
                return; 
            }
        } else {
            logger.warning("Tried to rename an unsupported Nameable: "
                           + object.toString());
            return;
        }

        askRename((FreeColGameObject) object, name);
    }

    
    private boolean askRename(FreeColGameObject object, String name) {
        RenameMessage message = new RenameMessage(object, name);
        Client client = freeColClient.getClient();
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }


    
    public void buildColony() {
        Canvas canvas = freeColClient.getCanvas();
        Player player = freeColClient.getMyPlayer();
        if (freeColClient.getGame().getCurrentPlayer() != player) {
            canvas.showInformationMessage("notYourTurn");
            return;
        }

        
        
        GUI gui = freeColClient.getGUI();
        Unit unit = gui.getActiveUnit();
        if (unit == null) return;
        Tile tile = unit.getTile();
        if (tile == null) return;
        if (tile.getColony() != null) {
            askJoinColony(unit, tile.getColony());
            return;
        }
        if (!unit.canBuildColony()) return;
        if (freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_COLONY_WARNINGS)
            && !showColonyWarnings(tile, unit)) {
            return;
        }

        
        String name = canvas.showInputDialog("nameColony.text",
                                             player.getDefaultSettlementName(false),
                                             "nameColony.yes", "nameColony.no");
        if (name == null) return; 
        if (player.getSettlement(name) != null) {
            
            canvas.showInformationMessage("nameColony.notUnique",
                                          "%name%", name);
            return;
        }

        if (askBuildColony(name, unit) && tile.getSettlement() != null) {
            player.invalidateCanSeeTiles();
            freeColClient.playSound(SoundEffect.BUILDING_COMPLETE);

            
            
            ArrayList<Unit> units = new ArrayList<Unit>(tile.getUnitList());
            for (Unit unitInTile : units) {
                checkCashInTreasureTrain(unitInTile);
            }

            gui.setActiveUnit(null);
            gui.setSelectedTile(tile.getPosition());
        }
    }

    
    private boolean showColonyWarnings(Tile tile, Unit unit) {
        boolean landLocked = true;
        boolean ownedByEuropeans = false;
        boolean ownedBySelf = false;
        boolean ownedByIndians = false;

        java.util.Map<GoodsType, Integer> goodsMap = new HashMap<GoodsType, Integer>();
        for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
            if (goodsType.isFoodType()) {
                int potential = 0;
                if (tile.primaryGoods() == goodsType) {
                    potential = tile.potential(goodsType, null);
                }
                goodsMap.put(goodsType, new Integer(potential));
            } else if (goodsType.isBuildingMaterial()) {
                while (goodsType.isRefined()) {
                    goodsType = goodsType.getRawMaterial();
                }
                int potential = 0;
                if (tile.secondaryGoods() == goodsType) {
                    potential = tile.potential(goodsType, null);
                }
                goodsMap.put(goodsType, new Integer(potential));
            }
        }

        Map map = tile.getGame().getMap();
        Iterator<Position> tileIterator = map.getAdjacentIterator(tile.getPosition());
        while (tileIterator.hasNext()) {
            Tile newTile = map.getTile(tileIterator.next());
            if (newTile.isLand()) {
                for (Entry<GoodsType, Integer> entry : goodsMap.entrySet()) {
                    entry.setValue(entry.getValue().intValue() +
                                   newTile.potential(entry.getKey(), null));
                }
                Player tileOwner = newTile.getOwner();
                if (tileOwner == unit.getOwner()) {
                    if (newTile.getOwningSettlement() != null) {
                        
                        ownedBySelf = true;
                    } else {
                        Iterator<Position> ownTileIt = map.getAdjacentIterator(newTile.getPosition());
                        while (ownTileIt.hasNext()) {
                            Colony colony = map.getTile(ownTileIt.next()).getColony();
                            if (colony != null && colony.getOwner() == unit.getOwner()) {
                                
                                ownedBySelf = true;
                                break;
                            }
                        }
                    }
                } else if (tileOwner != null && tileOwner.isEuropean()) {
                    ownedByEuropeans = true;
                } else if (tileOwner != null) {
                    ownedByIndians = true;
                }
            } else {
                landLocked = false;
            }
        }

        int food = 0;
        for (Entry<GoodsType, Integer> entry : goodsMap.entrySet()) {
            if (entry.getKey().isFoodType()) {
                food += entry.getValue().intValue();
            }
        }

        ArrayList<ModelMessage> messages = new ArrayList<ModelMessage>();
        if (landLocked) {
            messages.add(new ModelMessage(unit, ModelMessage.MessageType.MISSING_GOODS,
                                          FreeCol.getSpecification().getGoodsType("model.goods.fish"),
                                          "buildColony.landLocked"));
        }
        if (food < 8) {
            messages.add(new ModelMessage(unit, ModelMessage.MessageType.MISSING_GOODS,
                                          FreeCol.getSpecification().getGoodsType("model.goods.food"),
                                          "buildColony.noFood"));
        }
        for (Entry<GoodsType, Integer> entry : goodsMap.entrySet()) {
            if (!entry.getKey().isFoodType() && entry.getValue().intValue() < 4) {
                messages.add(new ModelMessage(unit, ModelMessage.MessageType.MISSING_GOODS, entry.getKey(),
                                              "buildColony.noBuildingMaterials",
                                              "%goods%", entry.getKey().getName()));
            }
        }

        if (ownedBySelf) {
            messages.add(new ModelMessage(unit, ModelMessage.MessageType.WARNING,
                                          null, "buildColony.ownLand"));
        }
        if (ownedByEuropeans) {
            messages.add(new ModelMessage(unit, ModelMessage.MessageType.WARNING,
                                          null, "buildColony.EuropeanLand"));
        }
        if (ownedByIndians) {
            messages.add(new ModelMessage(unit, ModelMessage.MessageType.WARNING,
                                          null, "buildColony.IndianLand"));
        }

        if (messages.isEmpty()) return true;
        ModelMessage[] modelMessages = messages.toArray(new ModelMessage[messages.size()]);
        return freeColClient.getCanvas().showConfirmDialog(modelMessages,
                                                           "buildColony.yes",
                                                           "buildColony.no");
    }

    
    private boolean askBuildColony(String name, Unit unit) {
        Client client = freeColClient.getClient();
        BuildColonyMessage message = new BuildColonyMessage(name, unit);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private boolean askJoinColony(Unit unit, Colony colony) {
        Client client = freeColClient.getClient();
        JoinColonyMessage message = new JoinColonyMessage(colony, unit);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }


    
    public void setDestination(Unit unit, Location destination) {
        askSetDestination(unit, destination);
    }

    
    private boolean askSetDestination(Unit unit, Location destination) {
        SetDestinationMessage message
            = new SetDestinationMessage(unit, destination);
        Client client = freeColClient.getClient();
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }


    
    public void move(Unit unit, Direction direction) {
        if (freeColClient.getGame().getCurrentPlayer()
            != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        
        MoveType move = unit.getMoveType(direction);
        switch (move) {
        case MOVE:
            moveMove(unit, direction);
            break;
        case MOVE_HIGH_SEAS:
            moveHighSeas(unit, direction);
            break;
        case EXPLORE_LOST_CITY_RUMOUR:
            moveExplore(unit, direction);
            break;
        case ATTACK:
            moveAttack(unit, direction);
            break;
        case EMBARK:
            moveEmbark(unit, direction);
            break;
        case DISEMBARK:
            moveDisembark(unit, direction);
            break;
        case ENTER_INDIAN_VILLAGE_WITH_FREE_COLONIST:
            moveLearnSkill(unit, direction);
            break;
        case ENTER_INDIAN_VILLAGE_WITH_SCOUT:
            moveScoutIndianSettlement(unit, direction);
            break;
        case ENTER_INDIAN_VILLAGE_WITH_MISSIONARY:
            moveUseMissionary(unit, direction);
            break;
        case ENTER_FOREIGN_COLONY_WITH_SCOUT:
            moveScoutColony(unit, direction);
            break;
        case ENTER_SETTLEMENT_WITH_CARRIER_AND_GOODS:
            moveTrade(unit, direction);
            break;
        default:
            freeColClient.playSound(SoundEffect.ILLEGAL_MOVE);
            break;
        }

        
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    freeColClient.getActionManager().update();
                    freeColClient.updateMenuBar();
                }
            });
    }

    
    private void moveMove(Unit unit, Direction direction) {
        
        final Canvas canvas = freeColClient.getCanvas();
        Game game = freeColClient.getGame();
        Player player = unit.getOwner();
        Map map = game.getMap();
        String speed = (freeColClient.getMyPlayer() == player)
            ? ClientOptions.MOVE_ANIMATION_SPEED
            : ClientOptions.ENEMY_MOVE_ANIMATION_SPEED;
        if (!freeColClient.isHeadless()
            && freeColClient.getClientOptions().getInteger(speed) > 0) {
            Tile src = unit.getTile();
            Tile dst = map.getNeighbourOrNull(direction, src);
            Animations.unitMove(canvas, unit, src, dst);
        }

        
        if (unit.getSpaceLeft() > 0
            && (unit.getColony() != null || unit.isInEurope())) {
            for (Unit sentry : new ArrayList<Unit>(unit.getLocation().getUnitList())) {
                if (sentry.getState() == UnitState.SENTRY) {
                    if (sentry.getSpaceTaken() <= unit.getSpaceLeft()) {
                        boardShip(sentry, unit);
                        logger.finest("Unit " + unit.getName()
                                      + " loaded sentry " + sentry.getName());
                    } else {
                        logger.finest("Unit " + sentry.getName()
                                      + " is too big to board " + unit.getName());
                    }
                }
            }
        }

        
        Element reply = askMove(unit, direction);
        if (reply == null) return;

        
        if (reply.hasAttribute("slowedBy")) { 
            Unit slowedBy = (Unit) game.getFreeColGameObject(reply.getAttribute("slowedBy"));
            String enemy = slowedBy.getOwner().getNationAsString();
            canvas.showInformationMessage("model.unit.slowed", slowedBy,
                                          "%unit%", unit.getName(),
                                          "%enemyUnit%", slowedBy.getName(),
                                          "%enemyNation%", enemy);
        }

        if (reply.hasAttribute("nameNewLand")) {
            String newLandName = reply.getAttribute("nameNewLand");
            newLandName = canvas.showInputDialog("newLand.text",
                                                 newLandName,
                                                 "newLand.yes", null);
            if (askNewLandName(newLandName)
                && newLandName.equals(player.getNewLandName())) {
                canvas.showFreeColDialog(new EventPanel(canvas, EventPanel.EventType.FIRST_LANDING));
                BuildColonyAction bca
                    = (BuildColonyAction) freeColClient.getActionManager()
                    .getFreeColAction(BuildColonyAction.id);
                String key = FreeColActionUI.getHumanKeyStrokeText(bca.getAccelerator());
                ModelMessage m = new ModelMessage(player, ModelMessage.MessageType.TUTORIAL,
                                                  player,
                                                  "tutorial.buildColony",
                                                  "%build_colony_key%", key,
                                                  "%build_colony_menu_item%", Messages.message("unit.state.7"),
                                                  "%orders_menu_item%", Messages.message("menuBar.orders"));
                player.addModelMessage(m);
            }
        }

        if (reply.hasAttribute("discoverPacific")) {
            canvas.showFreeColDialog(new EventPanel(canvas, EventPanel.EventType.DISCOVER_PACIFIC));
        }
        if (reply.hasAttribute("discoverRegion")
            && reply.hasAttribute("regionType")) {
            String newRegionName = reply.getAttribute("discoverRegion");
            String newRegionType = reply.getAttribute("regionType");
            newRegionName = canvas.showInputDialog("nameRegion.text",
                                                   newRegionName,
                                                   "ok", "cancel",
                                                   "%name%", newRegionType);
            askNewRegionName(newRegionName, unit);
        }

        if (reply.hasAttribute("fountainOfYouth")) {
            
            
            final int emigrants = Integer.parseInt(reply.getAttribute("fountainOfYouth"));
            freeColClient.playMusicOnce("fountain");
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < emigrants; i++) {
                            int index = canvas.showEmigrationPanel(true);
                            emigrateUnitInEurope(index + 1);
                        }
                    }
                });
        }

        
        if (unit.isDisposed() || checkCashInTreasureTrain(unit)) {
            nextActiveUnit(unit.getTile());
        } else if (unit.getTile().getSettlement() != null
                   && unit.isCarrier() && unit.getTradeRoute() == null
                   && (unit.getDestination() == null
                       || unit.getDestination().getTile() == unit.getTile())) {
            canvas.showColonyPanel((Colony) unit.getTile().getSettlement());
        } else if (unit.getMovesLeft() <= 0) {
            nextActiveUnit(unit.getTile());
        }
        nextModelMessage();
    }

    
    private Element askMove(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        MoveMessage message = new MoveMessage(unit, direction);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return null;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return reply;
    }

    
    private boolean askNewLandName(String name) {
        Client client = freeColClient.getClient();
        NewLandNameMessage message = new NewLandNameMessage(name);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private boolean askNewRegionName(String name, Unit unit) {
        Client client = freeColClient.getClient();
        NewRegionNameMessage message
            = new NewRegionNameMessage(name, unit);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private void moveHighSeas(Unit unit, Direction direction) {
        
        
        
        Map map = freeColClient.getGame().getMap();
        Tile oldTile = unit.getTile();
        Tile newTile = map.getNeighbourOrNull(direction, oldTile);
        if (newTile == null
            || (!oldTile.canMoveToEurope() && newTile.canMoveToEurope())) {
            Canvas canvas = freeColClient.getCanvas();
            if (canvas.showConfirmDialog("highseas.text",
                                         "highseas.yes", "highseas.no")) {
                moveToEurope(unit);
                nextActiveUnit();
            }
        } else {
            moveMove(unit, direction);
        }
    }

    
    private void moveExplore(Unit unit, Direction direction) {
        
        freeColClient.getGUI().setFocusImmediately(unit.getTile().getPosition());

        
        Canvas canvas = freeColClient.getCanvas();
        if (canvas.showConfirmDialog("exploreLostCityRumour.text",
                                     "exploreLostCityRumour.yes",
                                     "exploreLostCityRumour.no")) {
            moveMove(unit, direction);
        }
    }

    
    private void moveAttack(Unit unit, Direction direction) {
        Canvas canvas = freeColClient.getCanvas();
        Game game = freeColClient.getGame();

        
        Tile tile = unit.getTile();
        Tile target = game.getMap().getNeighbourOrNull(direction, tile);
        Settlement settlement = target.getSettlement();
        if (settlement != null
            && settlement instanceof IndianSettlement
            && unit.isArmed()) {
            IndianSettlement natives = (IndianSettlement) settlement;
            switch (canvas.showArmedUnitIndianSettlementDialog(natives)) {
            case CANCEL:
                return;
            case INDIAN_SETTLEMENT_ATTACK:
                break; 
            case INDIAN_SETTLEMENT_TRIBUTE:
                moveTribute(unit, direction);
                return;
            default:
                logger.warning("showArmedUnitIndianSettlementDialog failure.");
                return;
            }
        }

        
        if (confirmHostileAction(unit, target)
            && confirmPreCombat(unit, target)) {
            attack(unit, direction);
        }
    }

    
    private void moveTribute(Unit unit, Direction direction) {
        if (askDemandTribute(unit, direction)) {
            
            freeColClient.getCanvas().updateGoldLabel();
            nextModelMessage();
            nextActiveUnit(unit.getTile());
        }
    }

    
    private boolean askDemandTribute(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        DemandTributeMessage message
            = new DemandTributeMessage(unit, direction);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private boolean confirmHostileAction(Unit attacker, Tile target) {
        if (attacker.hasAbility("model.ability.piracy")) {
            
            return true;
        }

        Player enemy;
        if (target.getSettlement() != null) {
            enemy = target.getSettlement().getOwner();
        } else {
            Unit defender = target.getDefendingUnit(attacker);
            if (defender == null) {
                logger.warning("Attacking, but no defender - will try!");
                return true;
            }
            if (defender.hasAbility("model.ability.piracy")) {
                
                return true;
            }
            enemy = defender.getOwner();
        }

        
        Canvas canvas = freeColClient.getCanvas();
        String enemyNation = enemy.getNationAsString();
        switch (attacker.getOwner().getStance(enemy)) {
        case UNCONTACTED: case PEACE:
            return canvas.showConfirmDialog("model.diplomacy.attack.peace",
                                            "model.diplomacy.attack.confirm",
                                            "cancel",
                                            "%nation%", enemyNation);
        case WAR:
            logger.finest("Player at war, no confirmation needed");
            break;
        case CEASE_FIRE:
            return canvas.showConfirmDialog("model.diplomacy.attack.ceaseFire",
                                            "model.diplomacy.attack.confirm",
                                            "cancel",
                                            "%nation%", enemyNation);
        case ALLIANCE:
            return canvas.showConfirmDialog("model.diplomacy.attack.alliance",
                                            "model.diplomacy.attack.confirm",
                                            "cancel",
                                            "%nation%", enemyNation);
        }
        return true;
    }

    
    private boolean confirmPreCombat(Unit attacker, Tile target) {
        if (freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_PRECOMBAT)) {
            Settlement settlementOrNull = target.getSettlement();
            
            Unit defenderOrNull = (settlementOrNull != null) ? null
                : target.getDefendingUnit(attacker);
            Canvas canvas = freeColClient.getCanvas();
            PreCombatDialog dialog = new PreCombatDialog(attacker,
                                                         defenderOrNull,
                                                         settlementOrNull,
                                                         canvas);
            return canvas.showFreeColDialog(dialog);
        }
        return true;
    }

    
    private void attack(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        Game game = freeColClient.getGame();
        Tile target = game.getMap().getNeighbourOrNull(direction, unit.getTile());

        Element attackElement = Message.createNewRootElement("attack");
        attackElement.setAttribute("unit", unit.getId());
        attackElement.setAttribute("direction", direction.toString());

        
        Element attackResultElement = client.ask(attackElement);
        if (attackResultElement != null &&
            attackResultElement.getTagName().equals("attackResult")) {
            
            CombatResultType result = Enum.valueOf(CombatResultType.class, attackResultElement.getAttribute("result"));
            int damage = Integer.parseInt(attackResultElement.getAttribute("damage"));
            int plunderGold = Integer.parseInt(attackResultElement.getAttribute("plunderGold"));
            Location repairLocation = (Location) game.getFreeColGameObjectSafely(attackResultElement.getAttribute("repairIn"));

            
            
            Element utElement = getChildElement(attackResultElement, Tile.getXMLElementTagName());
            if (utElement != null) {
                Tile updateTile = (Tile) game.getFreeColGameObject(utElement.getAttribute("ID"));
                updateTile.readFromXMLElement(utElement);
            }

            
            NodeList capturedGoods = attackResultElement.getElementsByTagName("capturedGoods");
            for (int i = 0; i < capturedGoods.getLength(); ++i) {
                Element goods = (Element) capturedGoods.item(i);
                GoodsType type = FreeCol.getSpecification().getGoodsType(goods.getAttribute("type"));
                int amount = Integer.parseInt(goods.getAttribute("amount"));
                unit.getGoodsContainer().addGoods(type, amount);
            }

            
            Element unitElement = getChildElement(attackResultElement, Unit.getXMLElementTagName());
            Unit defender;
            if (unitElement != null) {
                defender = (Unit) game.getFreeColGameObject(unitElement.getAttribute("ID"));
                if (defender == null) {
                    defender = new Unit(game, unitElement);
                } else {
                    defender.readFromXMLElement(unitElement);
                }
                defender.setLocation(target);
            } else {
                
                logger.log(Level.SEVERE, "Server reallyAttack did not return a defender!");
                defender = target.getDefendingUnit(unit);
                if (defender == null) {
                    throw new IllegalStateException("No defender available!");
                }
            }

            if (result == CombatResultType.DONE_SETTLEMENT) {
                freeColClient.playSound(SoundEffect.CAPTURED_BY_ARTILLERY);
            } else if (defender.isNaval() && result == CombatResultType.GREAT_WIN 
                       || unit.isNaval() && result == CombatResultType.GREAT_LOSS) {
                freeColClient.playSound(SoundEffect.SUNK);
            } else if (unit.isNaval()) {
                freeColClient.playSound(SoundEffect.ATTACK_NAVAL);
            } else if (unit.hasAbility("model.ability.bombard")) {
                freeColClient.playSound(SoundEffect.ATTACK_ARTILLERY);
            } else if (unit.isMounted()) {
                freeColClient.playSound(SoundEffect.ATTACK_DRAGOON);
            }
            
            Animations.unitAttack(freeColClient.getCanvas(), unit, defender, result);

            try {
                game.getCombatModel().attack(unit, defender, new CombatResult(result, damage), plunderGold, repairLocation);
            } catch (Exception e) {
                
                
                LogRecord lr = new LogRecord(Level.WARNING, "Exception in reallyAttack");
                lr.setThrown(e);
                logger.log(lr);
            }

            
            Element convertElement = getChildElement(attackResultElement, "convert");
            Unit convert;
            if (convertElement != null) {
                unitElement = (Element) convertElement.getFirstChild();
                convert = (Unit) game.getFreeColGameObject(unitElement.getAttribute("ID"));
                if (convert == null) {
                    convert = new Unit(game, unitElement);
                } else {
                    convert.readFromXMLElement(unitElement);
                }
                convert.setLocation(convert.getLocation());
                
                String nation = defender.getOwner().getNationAsString();
                ModelMessage message = new ModelMessage(convert,
                                                        "model.unit.newConvertFromAttack",
                                                        new String[][] {
                                                            {"%nation%", nation},
                                                            {"%unit%", convert.getName()}},
                                                        ModelMessage.MessageType.UNIT_ADDED);
                freeColClient.getMyPlayer().addModelMessage(message);
                nextModelMessage();
            }
            
            if (defender.canCarryTreasure() &&
                (result == CombatResultType.WIN ||
                 result == CombatResultType.GREAT_WIN)) {
                checkCashInTreasureTrain(defender);
            }
                
            if (!defender.isDisposed()
                && ((result == CombatResultType.DONE_SETTLEMENT && unitElement != null)
                    || defender.getLocation() == null || !defender.isVisibleTo(freeColClient.getMyPlayer()))) {
                defender.dispose();
            }
 
            Element updateElement = getChildElement(attackResultElement, "update");
            if (updateElement != null) {
                freeColClient.getInGameInputHandler().handle(client.getConnection(), updateElement);
            }
            
            
            if(attackResultElement.getAttribute("indianCapitalBurned") != ""){
            	Player indianPlayer = defender.getOwner();
            	indianPlayer.surrenderTo(freeColClient.getMyPlayer());
            	
            	ModelMessage message = new ModelMessage(indianPlayer,
                         "indianSettlement.capitalBurned",
                         new String[][] {
                             {"%name%", indianPlayer.getDefaultSettlementName(true)},
                             {"%nation%", indianPlayer.getNationAsString()}},
                         ModelMessage.MessageType.COMBAT_RESULT);
            	freeColClient.getMyPlayer().addModelMessage(message);
            	nextModelMessage();
            }

            if (unit.getMovesLeft() <= 0) {
                nextActiveUnit(unit.getTile());
            }

            freeColClient.getCanvas().refresh();
        } else {
            logger.log(Level.SEVERE, "Server returned null from attack!");
        }
    }

    
    private void moveEmbark(Unit unit, Direction direction) {
        
        Canvas canvas = freeColClient.getCanvas();
        Tile sourceTile = unit.getTile();
        Map map = freeColClient.getGame().getMap();
        Tile destinationTile = map.getNeighbourOrNull(direction, sourceTile);
        Unit carrier = null;
        ArrayList<Unit> choices = new ArrayList<Unit>();
        for (Unit nextUnit : destinationTile.getUnitList()) {
            if (nextUnit.getSpaceLeft() >= unit.getType().getSpaceTaken()) {
                choices.add(nextUnit);
            }
        }
        if (choices.size() == 0) {
            throw new RuntimeException("Unit " + unit.getId()
                                       + " found no carrier to embark upon.");
        } else if (choices.size() == 1) {
            carrier = choices.get(0);
        } else {
            carrier = canvas.showSimpleChoiceDialog(Messages.message("embark.text"),
                                                    Messages.message("embark.cancel"),
                                                    choices);
            if (carrier == null) return; 
        }

        
        if (askEmbark(unit, carrier, direction)
            && unit.getLocation() == carrier) {
            Animations.unitMove(canvas, unit, sourceTile, destinationTile);
            if (carrier.getMovesLeft() > 0) {
                freeColClient.getGUI().setActiveUnit(carrier);
            } else {
                nextActiveUnit(destinationTile);
            }
        }
    }

    
    private void moveDisembark(Unit unit, Direction direction) {
        
        Canvas canvas = freeColClient.getCanvas();
        if (!canvas.showConfirmDialog("disembark.text",
                                      "disembark.yes", "disembark.no")) {
            return;
        }

        
        
        unit.setStateToAllChildren(UnitState.ACTIVE);
        for (Unit toDisembark : unit.getUnitList()) {
            if (toDisembark.getMovesLeft() > 0) {
                
                
                
                move(toDisembark, direction);
                break;
            }
        }
    }

    
    private void moveLearnSkill(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        Canvas canvas = freeColClient.getCanvas();
        Map map = freeColClient.getGame().getMap();
        IndianSettlement settlement = (IndianSettlement) map.getNeighbourOrNull(direction, unit.getTile()).getSettlement();
        UnitType skill = settlement.getLearnableSkill();

        if (skill == null) {
            Element askSkill = Message.createNewRootElement("askSkill");
            askSkill.setAttribute("unit", unit.getId());
            askSkill.setAttribute("direction", direction.toString());
            Element reply = client.ask(askSkill);
            if (reply.getTagName().equals("provideSkill")) {
                if (reply.hasAttribute("skill")) {
                    skill = FreeCol.getSpecification().getUnitType(reply.getAttribute("skill"));
                    settlement.setLearnableSkill(skill);
                }
            } else {
                logger.warning("Server gave an invalid reply to an askSkill message");
                return;
            }
        }

        unit.setMovesLeft(0);
        if (skill == null) {
            canvas.errorMessage("indianSettlement.noMoreSkill");
        } else if (!unit.getType().canBeUpgraded(skill, ChangeType.NATIVES)) {
            canvas.showInformationMessage("indianSettlement.cantLearnSkill",
                                          settlement,
                                          "%unit%", unit.getName(),
                                          "%skill%", skill.getName());
        } else if (canvas.showConfirmDialog("learnSkill.text",
                                            "learnSkill.yes", "learnSkill.no",
                                            "%skill%", skill.getName())) {
            if (askLearnSkill(unit, direction)) {
                if (unit.isDisposed()) {
                    canvas.showInformationMessage("learnSkill.die");
                } else if (unit.getType() != skill) {
                    canvas.showInformationMessage("learnSkill.leave");
                }
            }
        }
        nextActiveUnit(unit.getTile());
    }

    
    private boolean askSkill(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        AskSkillMessage message = new AskSkillMessage(unit, direction);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private boolean askLearnSkill(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        LearnSkillMessage message = new LearnSkillMessage(unit, direction);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private void moveScoutIndianSettlement(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        Canvas canvas = freeColClient.getCanvas();
        Map map = freeColClient.getGame().getMap();
        Tile tile = map.getNeighbourOrNull(direction, unit.getTile());
        IndianSettlement settlement = (IndianSettlement) tile.getSettlement();

        
        switch (canvas.showScoutIndianSettlementDialog(settlement)) {
        case CANCEL:
            return;
        case INDIAN_SETTLEMENT_ATTACK:
            if (confirmPreCombat(unit, tile)) {
                attack(unit, direction);
            }
            return;
        case INDIAN_SETTLEMENT_SPEAK:
            Player player = unit.getOwner();
            int gold = player.getGold();
            String result = askScoutSpeak(unit, direction);
            if (result == null) return;
            if ("die".equals(result)) {
                canvas.showInformationMessage("scoutSettlement.speakDie",
                                              settlement);
            } else if ("expert".equals(result)) {
                canvas.showInformationMessage("scoutSettlement.expertScout",
                                              settlement,
                                              "%unit%", unit.getType().getName());
            } else if ("tales".equals(result)) {
                canvas.showInformationMessage("scoutSettlement.speakTales",
                                              settlement);
            } else if ("beads".equals(result)) {
                canvas.updateGoldLabel();
                canvas.showInformationMessage("scoutSettlement.speakBeads",
                                              settlement,
                                              "%amount%", Integer.toString(player.getGold() - gold));
            } else if ("nothing".equals(result)) {
                canvas.showInformationMessage("scoutSettlement.speakNothing",
                                              settlement);
            } else {
                logger.warning("No valid result attribute from askScoutSpeak.");
            }
            nextActiveUnit(unit.getTile());
            break;
        case INDIAN_SETTLEMENT_TRIBUTE:
            moveTribute(unit, direction);
            break;
        default:
            logger.warning("Bad return from showScoutIndianSettlementDialog.");
            break;
        }
    }

    
    private String askScoutSpeak(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        ScoutIndianSettlementMessage message
            = new ScoutIndianSettlementMessage(unit, direction);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return null;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return reply.getAttribute("result");
    }

    
    private void moveUseMissionary(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        Canvas canvas = freeColClient.getCanvas();
        Map map = freeColClient.getGame().getMap();
        Tile tile = map.getNeighbourOrNull(direction, unit.getTile());
        IndianSettlement settlement = (IndianSettlement) tile.getSettlement();

        
        List<Object> response = canvas.showUseMissionaryDialog(settlement);
        MissionaryAction action = (MissionaryAction) response.get(0);
        switch (action) {
        case CANCEL:
            return;
        case ESTABLISH_MISSION:
            if (askMissionary(unit, direction, false)) {
                if (settlement.getMissionary() == unit) {
                    freeColClient.playSound(SoundEffect.MISSION_ESTABLISHED);
                }
                nextModelMessage();
                nextActiveUnit();
            }
            break;
        case DENOUNCE_HERESY:
            if (askMissionary(unit, direction, true)) {
                if (settlement.getMissionary() == unit) {
                    freeColClient.playSound(SoundEffect.MISSION_ESTABLISHED);
                }
                nextModelMessage();
                nextActiveUnit();
            }
            break;
        case INCITE_INDIANS:
            Player enemy = (Player) response.get(1);
            int gold = askIncite(unit, direction, enemy, -1);
            if (gold < 0) return;
            if (canvas.showInciteDialog(enemy, gold)) {
                int goldOut = askIncite(unit, direction, enemy, gold);
                if (goldOut < 0) {
                    ; 
                } else if (goldOut == 0) {
                    canvas.showInformationMessage("notEnoughGold");
                } else {
                    
                    
                    canvas.updateGoldLabel();
                }
            }
            nextActiveUnit();
            break;
        default:
            logger.warning("Bad return from showUseMissionaryDialog.");
            break;
        }
    }

    
    private boolean askMissionary(Unit unit, Direction direction,
                                  boolean denounce) {
        Client client = freeColClient.getClient();
        MissionaryMessage message
            = new MissionaryMessage(unit, direction, denounce);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private int askIncite(Unit unit, Direction direction, Player enemy,
                          int gold) {
        Client client = freeColClient.getClient();
        InciteMessage message
            = new InciteMessage(unit, direction, enemy, gold);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null || reply.getAttribute("gold") == null) return -1;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return Integer.parseInt(reply.getAttribute("gold"));
    }

    
    private void moveScoutColony(Unit unit, Direction direction) {
        Canvas canvas = freeColClient.getCanvas();
        Map map = freeColClient.getGame().getMap();
        Tile tile = map.getNeighbourOrNull(direction, unit.getTile());
        Colony colony = tile.getColony();
        Player player = unit.getOwner();
        if (colony != null && !player.hasContacted(colony.getOwner())) {
            player.setContacted(colony.getOwner());
        }

        ScoutAction userAction = canvas.showScoutForeignColonyDialog(colony, unit);
        switch (userAction) {
        case CANCEL:
            break;
        case FOREIGN_COLONY_ATTACK:
            moveAttack(unit, direction);
            break;
        case FOREIGN_COLONY_NEGOTIATE:
            moveTradeColony(unit, direction);
            break;
        case FOREIGN_COLONY_SPY:
            moveSpy(unit, direction);
            break;
        default:
            logger.warning("Incorrect response returned from Canvas.showScoutForeignColonyDialog()");
            return;
        }
    }

    
    private void moveTradeColony(Unit unit, Direction direction) {
        Game game = freeColClient.getGame();
        Tile tile = game.getMap().getNeighbourOrNull(direction, unit.getTile());
        if (tile == null) return;
        Settlement settlement = tile.getSettlement();
        if (settlement == null) return;

        
        if (settlement.getOwner() == unit.getOwner().getREFPlayer()) {
            throw new IllegalStateException("Unit tried to negotiate with REF");
        }

        Player player = freeColClient.getMyPlayer();
        Client client = freeColClient.getClient();
        Canvas canvas = freeColClient.getCanvas();
        DiplomaticTrade oldAgreement = null;
        DiplomaticTrade newAgreement = null;
        DiplomacyMessage message;
        Element reply;
        for (;;) {
            newAgreement = canvas.showNegotiationDialog(unit, settlement,
                                                        oldAgreement);
            if (newAgreement == null) {
                if (oldAgreement != null) {
                    
                    message = new DiplomacyMessage(unit, direction,
                                                   oldAgreement);
                    message.setReject();
                    client.sendAndWait(message.toXMLElement());
                }
                break;
            }

            
            message = askDiplomacy(unit, direction, newAgreement);

            
            if (message == null || message.isReject()) {
                String nation = message.getOtherNationName(player);
                canvas.showInformationMessage("negotiationDialog.offerRejected",
                                              "%nation%", nation);
                break;
            } else if (message.isAccept()) {
                String nation = message.getOtherNationName(player);
                canvas.showInformationMessage("negotiationDialog.offerAccepted",
                                              "%nation%", nation);
                break;
            } else { 
                oldAgreement = message.getAgreement();
            }
        }
        nextActiveUnit();
    }

    
    private DiplomacyMessage askDiplomacy(Unit unit, Direction direction,
                                          DiplomaticTrade agreement) {
        Client client = freeColClient.getClient();
        Game game = freeColClient.getGame();
        DiplomacyMessage message = new DiplomacyMessage(unit, direction,
                                                        agreement);
        if (agreement.isAccept()) message.setAccept();
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "multiple");
        if (reply == null) return null;

        
        Node diplomacy = reply.getLastChild();
        if (diplomacy == null
            || diplomacy.getNodeType() != Node.ELEMENT_NODE
            || !diplomacy.getNodeName().equals("diplomacy")) {
            System.err.println("DIP = " + ((diplomacy==null) ? "null" : diplomacy.getNodeName()));
            return null;
        }

        
        
        diplomacy = reply.removeChild(diplomacy);
        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return new DiplomacyMessage(game, (Element) diplomacy);
    }

    
    private void moveSpy(Unit unit, Direction direction) {
        Game game = freeColClient.getGame();
        Tile tile = game.getMap().getNeighbourOrNull(direction, unit.getTile());
        Element reply;
        if (tile != null && tile.getColony() != null
            && (reply = askSpy(unit, direction)) != null) {
            
            
            
            
            Element tileElement = (Element) reply.getFirstChild();
            tile.readFromXMLElement(tileElement);
            freeColClient.getCanvas().showColonyPanel(tile.getColony());
            reply.removeChild(tileElement);
            freeColClient.getInGameInputHandler().update(reply);
        }
        nextActiveUnit();
    }

    
    private Element askSpy(Unit unit, Direction direction) {
        Client client = freeColClient.getClient();
        SpySettlementMessage message = new SpySettlementMessage(unit, direction);
        return askExpecting(client, message.toXMLElement(), "update");
    }

    
    private void moveTrade(Unit unit, Direction direction) {
        Map map = freeColClient.getGame().getMap();
        Tile tile = map.getNeighbourOrNull(direction, unit.getTile());
        Settlement settlement = tile.getSettlement();
        Player player = unit.getOwner();
        if (settlement instanceof Colony) {
            moveTradeColony(unit, direction);
        } else if (settlement instanceof IndianSettlement) {
            if (player.hasContacted(settlement.getOwner())) {
                moveTradeIndianSettlement(unit, direction);
            } else {
                Canvas canvas = freeColClient.getCanvas();
                canvas.showInformationMessage("noContactWithIndians");
            }
        } else {
            logger.warning("Bogus settlement: " + settlement.getId());
        }
    }

    
    private void moveTradeIndianSettlement(Unit unit, Direction direction) {
        Canvas canvas = freeColClient.getCanvas();
        if (freeColClient.getGame().getCurrentPlayer()
            != freeColClient.getMyPlayer()) {
            canvas.showInformationMessage("notYourTurn");
            return;
        }

        
        if (!unit.canCarryGoods()) {
            throw new IllegalArgumentException("Unit " + unit.getId()
                                               + " can not carry goods.");
        }
        Map map = freeColClient.getGame().getMap();
        Tile tile = unit.getTile();
        if (tile == null) {
            throw new IllegalArgumentException("Unit " + unit.getId()
                                               + " is not on the map!");
        }
        if ((tile = map.getNeighbourOrNull(direction, tile)) == null) {
            throw new IllegalArgumentException("No tile in " + direction);
        }
        Settlement settlement = tile.getSettlement();
        if (settlement == null) {
            throw new IllegalArgumentException("No settlement in given direction!");
        }
        if (unit.getGoodsCount() == 0) {
            canvas.errorMessage("trade.noGoodsOnboard");
            return;
        }

        java.util.Map<String, Boolean> session;
        while ((session = askOpenTransactionSession(unit, settlement)) != null) {
            
            
            
            boolean buy = session.get("canBuy")  && (unit.getSpaceLeft() > 0);
            boolean sel = session.get("canSell") && (unit.getGoodsCount() > 0);
            boolean gif = session.get("canGift") && (unit.getGoodsCount() > 0);
            if (!buy && !sel && !gif) break;

            TradeAction tradeType
                = canvas.showIndianSettlementTradeDialog(settlement, buy, sel, gif);
            if (tradeType == null) break; 
            switch (tradeType) {
            case BUY:
                attemptBuyFromSettlement(unit, settlement);
                break;
            case SELL:
                attemptSellToSettlement(unit, settlement);
                break;
            case GIFT:
                attemptGiftToSettlement(unit, settlement);
                break;
            default:
                throw new IllegalArgumentException("Unknown trade type");
            }
        }

        askCloseTransactionSession(unit, settlement);
        if (unit.getMovesLeft() > 0) { 
            freeColClient.getGUI().setActiveUnit(unit);
        } else {
            nextActiveUnit();
        }
    }

    
    private java.util.Map<String,Boolean> askOpenTransactionSession(Unit unit, Settlement settlement) {
        Client client = freeColClient.getClient();
        GetTransactionMessage message = new GetTransactionMessage(unit, settlement);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "getTransactionAnswer");
        if (reply == null) return null;

        java.util.Map<String,Boolean> transactionSession = new HashMap<String,Boolean>();
        transactionSession.put("canBuy", new Boolean(reply.getAttribute("canBuy")));
        transactionSession.put("canSell", new Boolean(reply.getAttribute("canSell")));
        transactionSession.put("canGift", new Boolean(reply.getAttribute("canGift")));
        return transactionSession;
    }

    
    private boolean askCloseTransactionSession(Unit unit, Settlement settlement) {
        Client client = freeColClient.getClient();
        CloseTransactionMessage message
            = new CloseTransactionMessage(unit, settlement);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private void attemptBuyFromSettlement(Unit unit, Settlement settlement) {
        
        List<Goods> forSale = askGoodsForSaleInSettlement(unit, settlement);

        Canvas canvas = freeColClient.getCanvas();
        Goods goods = null;
        for (;;) {
            
            List<ChoiceItem<Goods>> goodsOffered
                = new ArrayList<ChoiceItem<Goods>>();
            for (Goods sell : forSale) {
                goodsOffered.add(new ChoiceItem<Goods>(sell));
            }

            
            goods = canvas.showChoiceDialog(Messages.message("buyProposition.text"),
                Messages.message("buyProposition.nothing"),
                goodsOffered);
            if (goods == null) break; 

            int gold = -1; 
            for (;;) {
                gold = askBuyPriceFromSettlement(unit, settlement, goods, gold);
                if (gold == NO_TRADE) { 
                    canvas.showInformationMessage("trade.noTrade");
                    return;
                } else if (gold < NO_TRADE) { 
                    return;
                }

                
                final int CHOOSE_BUY = 1;
                final int CHOOSE_HAGGLE = 2;
                String text = Messages.message("buy.text",
                        "%nation%", settlement.getOwner().getNationAsString(),
                        "%goods%", goods.getName(),
                        "%gold%", Integer.toString(gold));
                List<ChoiceItem<Integer>> choices = new ArrayList<ChoiceItem<Integer>>();
                choices.add(new ChoiceItem<Integer>(Messages.message("buy.takeOffer"), CHOOSE_BUY));
                choices.add(new ChoiceItem<Integer>(Messages.message("buy.moreGold"), CHOOSE_HAGGLE));
                Integer offerReply = canvas.showChoiceDialog(text,
                        Messages.message("buyProposition.cancel"), choices);
                if (offerReply == null) {
                    
                    break;
                }
                switch (offerReply.intValue()) {
                case CHOOSE_BUY: 
                    if (askBuyFromSettlement(unit, settlement, goods, gold)) {
                        canvas.updateGoldLabel(); 
                    }
                    return;
                case CHOOSE_HAGGLE: 
                    gold = gold * 9 / 10;
                    break;
                default:
                    throw new IllegalStateException("Unknown choice.");
                }
            }
        }
    }

    
    private List<Goods> askGoodsForSaleInSettlement(Unit unit,
                                                    Settlement settlement) {
        Client client = freeColClient.getClient();
        GoodsForSaleMessage message = new GoodsForSaleMessage(unit, settlement);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     message.getXMLElementTagName());
        if (reply == null) return null;

        Game game = freeColClient.getGame();
        ArrayList<Goods> goodsOffered = new ArrayList<Goods>();
        NodeList childNodes = reply.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            goodsOffered.add(new Goods(game, (Element) childNodes.item(i)));
        }
        return goodsOffered;
    }

    
    private int askBuyPriceFromSettlement(Unit unit, Settlement settlement,
                                          Goods goods, int gold) {
        Client client = freeColClient.getClient();
        BuyPropositionMessage message
            = new BuyPropositionMessage(unit, settlement, goods, gold);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     message.getXMLElementTagName());
        if (reply == null) return NO_TRADE - 1; 

        message = new BuyPropositionMessage(freeColClient.getGame(), reply);
        return message.getGold();
    }

    
    private boolean askBuyFromSettlement(Unit unit, Settlement settlement,
                                         Goods goods, int gold) {
        Client client = freeColClient.getClient();
        BuyMessage message = new BuyMessage(unit, settlement, goods, gold);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private void attemptSellToSettlement(Unit unit, Settlement settlement) {
        Canvas canvas = freeColClient.getCanvas();
        Client client = freeColClient.getClient();
        Goods goods = null;
        for (;;) {
            
            goods = canvas.showSimpleChoiceDialog(Messages.message("sellProposition.text"),
                Messages.message("sellProposition.nothing"),
                unit.getGoodsList());
            if (goods == null) break; 

            int gold = -1; 
            for (;;) {
                gold = askSellPriceToSettlement(unit, settlement, goods, gold);
                if (gold == NO_NEED_FOR_THE_GOODS) {
                    canvas.showInformationMessage("trade.noNeedForTheGoods",
                                                  "%goods%", goods.getName());
                    return;
                } else if (gold == NO_TRADE) {
                    canvas.showInformationMessage("trade.noTrade");
                    return;
                } else if (gold < NO_TRADE) { 
                    return;
                }

                
                final int CHOOSE_SELL = 1;
                final int CHOOSE_HAGGLE = 2;
                final int CHOOSE_GIFT = 3;
                String text = Messages.message("sell.text",
                        "%nation%", settlement.getOwner().getNationAsString(),
                        "%goods%", goods.getName(),
                        "%gold%", Integer.toString(gold));
                List<ChoiceItem<Integer>> choices = new ArrayList<ChoiceItem<Integer>>();
                choices.add(new ChoiceItem<Integer>(Messages.message("sell.takeOffer"), CHOOSE_SELL));
                choices.add(new ChoiceItem<Integer>(Messages.message("sell.moreGold"), CHOOSE_HAGGLE));
                choices.add(new ChoiceItem<Integer>(Messages.message("sell.gift", "%goods%",
                                                                     goods.getName()), CHOOSE_GIFT));
                Integer offerReply = canvas.showChoiceDialog(text, Messages.message("sellProposition.cancel"), choices);
                if (offerReply == null) {
                    
                    break;
                }
                switch (offerReply.intValue()) {
                case CHOOSE_SELL: 
                    if (askSellToSettlement(unit, settlement, goods, gold)) {
                        
                        canvas.updateGoldLabel();
                    }
                    return;
                case CHOOSE_HAGGLE: 
                    gold = (gold * 11) / 10;
                    break;
                case CHOOSE_GIFT: 
                    askDeliverGiftToSettlement(unit, settlement, goods);
                    return;
                default:
                    throw new IllegalStateException("Unknown choice.");
                }
            }
        }
    }

    
    private int askSellPriceToSettlement(Unit unit, Settlement settlement,
                                         Goods goods, int gold) {
        Client client = freeColClient.getClient();
        SellPropositionMessage message
            = new SellPropositionMessage(unit, settlement, goods, gold);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     message.getXMLElementTagName());
        if (reply == null) return NO_TRADE - 1; 

        message = new SellPropositionMessage(freeColClient.getGame(), reply);
        return message.getGold();
    }

    
    private boolean askSellToSettlement(Unit unit, Settlement settlement,
                                        Goods goods, int gold) {
        Client client = freeColClient.getClient();
        SellMessage message = new SellMessage(unit, settlement, goods, gold);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    
    private void attemptGiftToSettlement(Unit unit, Settlement settlement) {
        Canvas canvas = freeColClient.getCanvas();
        Goods goods = canvas.showSimpleChoiceDialog(Messages.message("gift.text"),
                                                    Messages.message("cancel"),
                                                    unit.getGoodsList());
        if (goods != null) {
            askDeliverGiftToSettlement(unit, settlement, goods);
        }
    }

    
    private boolean askDeliverGiftToSettlement(Unit unit, Settlement settlement,
                                               Goods goods) {
        Client client = freeColClient.getClient();
        DeliverGiftMessage message
            = new DeliverGiftMessage(unit, settlement, goods);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }

    


    
    public void debugForeignColony(Tile tile) {
        if (FreeCol.isInDebugMode() && tile != null) {
            DebugForeignColonyMessage message = new DebugForeignColonyMessage(tile);
            Element reply = askExpecting(freeColClient.getClient(),
                                         message.toXMLElement(),
                                         "update");
            if (reply != null) {
                
                Element tileElement = (Element) reply.getFirstChild();
                tile.readFromXMLElement(tileElement);
                freeColClient.getCanvas().showColonyPanel(tile.getColony());
                reply.removeChild(tileElement);
                freeColClient.getInGameInputHandler().update(reply);
            }
        }
    }


    
    public boolean claimLand(Tile tile, Colony colony, int offer) {
        Canvas canvas = freeColClient.getCanvas();
        Player player = freeColClient.getMyPlayer();
        if (freeColClient.getGame().getCurrentPlayer() != player) {
            canvas.showInformationMessage("notYourTurn");
            return false;
        }

        Player owner = tile.getOwner();
        int price = (owner == null) ? 0 : player.getLandPrice(tile);
        if (price < 0) { 
            return false;
        } else if (price > 0) { 
            if (offer >= price || offer < 0) {
                price = offer;
            } else {
                final int CLAIM_ACCEPT = 1;
                final int CLAIM_STEAL = 2;
                List<ChoiceItem<Integer>> choices = new ArrayList<ChoiceItem<Integer>>();
                if (price <= player.getGold()) {
                    choices.add(new ChoiceItem<Integer>(Messages.message("indianLand.pay", "%amount%",
                                                                         Integer.toString(price)), CLAIM_ACCEPT));
                }
                choices.add(new ChoiceItem<Integer>(Messages.message("indianLand.take"), CLAIM_STEAL));
                Integer ci = canvas.showChoiceDialog(Messages.message("indianLand.text",
                                                                      "%player%", owner.getNationAsString()),
                                                     Messages.message("indianLand.cancel"),
                                                     choices);
                if (ci == null) { 
                    return false;
                } else if (ci.intValue() == CLAIM_ACCEPT) { 
                    ;
                } else if (ci.intValue() == CLAIM_STEAL) {
                    price = -1; 
                } else {
                    logger.warning("Impossible choice");
                    return false;
                }
            }
        } 

        
        if (askClaimLand(tile, colony, price) && tile.getOwner() == player) {
            canvas.updateGoldLabel();
            return true;
        }
        return false;
    }

    
    private boolean askClaimLand(Tile tile, Colony colony, int price) {
        Client client = freeColClient.getClient();
        ClaimLandMessage message = new ClaimLandMessage(tile, colony, price);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }


    
    public boolean checkCashInTreasureTrain(Unit unit) {
        if (!unit.canCarryTreasure() || !unit.canCashInTreasureTrain()) {
            return false; 
        }

        Canvas canvas = freeColClient.getCanvas();
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            canvas.showInformationMessage("notYourTurn");
            return false;
        }

        
        boolean cash;
        Europe europe = unit.getOwner().getEurope();
        if (europe == null || unit.getLocation() == europe) {
            cash = true; 
        } else {
            String confirm = (unit.getTransportFee() == 0)
                ? "cashInTreasureTrain.free"
                : "cashInTreasureTrain.pay";
            cash = canvas.showConfirmDialog(confirm,
                                            "cashInTreasureTrain.yes",
                                            "cashInTreasureTrain.no");
        }

        
        boolean active = freeColClient.getGUI().getActiveUnit() == unit;
        if (cash && askCashInTreasureTrain(unit) && unit.isDisposed()) {
            if (active) {
                nextActiveUnit();
            }
            canvas.updateGoldLabel();
            return true;
        }
        return false;
    }

    
    private boolean askCashInTreasureTrain(Unit unit) {
        Client client = freeColClient.getClient();
        CashInTreasureTrainMessage message
            = new CashInTreasureTrainMessage(unit);
        Element reply = askExpecting(client, message.toXMLElement(),
                                    "multiple");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }


    
    public boolean boardShip(Unit unit, Unit carrier) {
        if (freeColClient.getGame().getCurrentPlayer()
            != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return false;
        }

        
        if (unit == null) {
            logger.warning("unit == null");
            return false;
        }
        if (carrier == null) {
            logger.warning("Trying to load onto a non-existent carrier.");
            return false;
        }
        if (unit.isNaval()) {
            logger.warning("Trying to load a ship onto another carrier.");
            return false;
        }
        if (unit.isInEurope() != carrier.isInEurope()
            || unit.getTile() != carrier.getTile()) {
            logger.warning("Unit and carrier are not co-located.");
            return false;
        }

        
        Location oldLocation = unit.getLocation();
        if (askEmbark(unit, carrier, null) && unit.getLocation() == carrier) {
            freeColClient.playSound(SoundEffect.LOAD_CARGO);
            return true;
        }
        return false;
    }

    
    private boolean askEmbark(Unit unit, Unit carrier, Direction direction) {
        Client client = freeColClient.getClient();
        EmbarkMessage message = new EmbarkMessage(unit, carrier, direction);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }


    
    public boolean leaveShip(Unit unit) {
        if (freeColClient.getGame().getCurrentPlayer()
            != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return false;
        }

        
        if (!(unit.getLocation() instanceof Unit)) {
            logger.warning("Unit " + unit.getId() + " is not on a carrier.");
            return false;
        }
        Unit carrier = (Unit) unit.getLocation();

        
        if (askDisembark(unit) && unit.getLocation() != carrier) {
            if (checkCashInTreasureTrain(unit)) {
                nextActiveUnit();
            }
            return true;
        }
        return false;
    }

    
    private boolean askDisembark(Unit unit) {
        Client client = freeColClient.getClient();
        DisembarkMessage message = new DisembarkMessage(unit);
        Element reply = askExpecting(client, message.toXMLElement(),
                                     "update");
        if (reply == null) return false;

        Connection conn = client.getConnection();
        freeColClient.getInGameInputHandler().handle(conn, reply);
        return true;
    }


    
    public void loadCargo(Goods goods, Unit carrier) {
        Canvas canvas = freeColClient.getCanvas();
        if (freeColClient.getGame().getCurrentPlayer()
            != freeColClient.getMyPlayer()) {
            canvas.showInformationMessage("notYourTurn");
            return;
        }

        
        if (goods == null) {
            logger.warning("goods == null");
            return;
        }
        if (carrier == null) {
            logger.warning("carrier == null");
            return;
        }

        freeColClient.playSound(SoundEffect.LOAD_CARGO);

        Client client = freeColClient.getClient();
        goods.adjustAmount();

        Element loadCargoElement = Message.createNewRootElement("loadCargo");
        loadCargoElement.setAttribute("carrier", carrier.getId());
        loadCargoElement.appendChild(goods.toXMLElement(freeColClient.getMyPlayer(), loadCargoElement
                                                        .getOwnerDocument()));

        goods.loadOnto(carrier);

        client.sendAndWait(loadCargoElement);
    }

    
    public void unloadCargo(Goods goods) {
        unloadCargo(goods, false);
    }

    
    public void unloadCargo(Goods goods, boolean dump) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        if (!dump && goods.getLocation() instanceof Unit &&
            ((Unit) goods.getLocation()).getLocation() instanceof Europe){
            sellGoods(goods);
            return;
        }

        Client client = freeColClient.getClient();

        goods.adjustAmount();

        Element unloadCargoElement = Message.createNewRootElement("unloadCargo");
        unloadCargoElement.appendChild(goods.toXMLElement(freeColClient.getMyPlayer(), unloadCargoElement
                                                          .getOwnerDocument()));

        if (!dump && goods.getLocation() instanceof Unit &&
            ((Unit) goods.getLocation()).getColony() != null) {
            goods.unload();
        } else {
            goods.setLocation(null);
        }

        client.sendAndWait(unloadCargoElement);
    }

    
    public void clearSpeciality(Unit unit) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        } else {
            UnitType newUnit = unit.getType().getUnitTypeChange(ChangeType.CLEAR_SKILL, unit.getOwner());
            if (newUnit == null) {
                freeColClient.getCanvas().showInformationMessage("clearSpeciality.impossible",
                                                                 "%unit%", unit.getName());
                return;
            } else if (!freeColClient.getCanvas().showConfirmDialog("clearSpeciality.areYouSure", "yes", "no",
                                                                    "%oldUnit%", unit.getName(),
                                                                    "%unit%", newUnit.getName())) {
                return;
            }
        }

        Client client = freeColClient.getClient();

        Element clearSpecialityElement = Message.createNewRootElement("clearSpeciality");
        clearSpecialityElement.setAttribute("unit", unit.getId());

        unit.clearSpeciality();

        client.sendAndWait(clearSpecialityElement);
    }

    
    public void buyGoods(GoodsType type, int amount, Unit carrier) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Client client = freeColClient.getClient();
        Player myPlayer = freeColClient.getMyPlayer();
        Canvas canvas = freeColClient.getCanvas();

        if (carrier == null) {
            throw new NullPointerException();
        }

        if (carrier.getOwner() != myPlayer
            || (carrier.getSpaceLeft() <= 0 && (carrier.getGoodsContainer().getGoodsCount(type) % 100 == 0))) {
            return;
        }

        if (carrier.getSpaceLeft() <= 0) {
            amount = Math.min(amount, 100 - carrier.getGoodsContainer().getGoodsCount(type) % 100);
        }

        if (myPlayer.getMarket().getBidPrice(type, amount) > myPlayer.getGold()) {
            canvas.errorMessage("notEnoughGold");
            return;
        }

        freeColClient.playSound(SoundEffect.LOAD_CARGO);

        Element buyGoodsElement = Message.createNewRootElement("buyGoods");
        buyGoodsElement.setAttribute("carrier", carrier.getId());
        buyGoodsElement.setAttribute("type", type.getId());
        buyGoodsElement.setAttribute("amount", Integer.toString(amount));

        carrier.buyGoods(type, amount);
        freeColClient.getCanvas().updateGoldLabel();

        client.sendAndWait(buyGoodsElement);
    }

    
    public void sellGoods(Goods goods) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Client client = freeColClient.getClient();
        Player player = freeColClient.getMyPlayer();

        freeColClient.playSound(SoundEffect.SELL_CARGO);

        goods.adjustAmount();

        Element sellGoodsElement = Message.createNewRootElement("sellGoods");
        sellGoodsElement.appendChild(goods.toXMLElement(freeColClient.getMyPlayer(), sellGoodsElement
                                                        .getOwnerDocument()));

        player.getMarket().sell(goods, player);
        freeColClient.getCanvas().updateGoldLabel();

        client.sendAndWait(sellGoodsElement);
    }

    
    public void setGoodsLevels(Colony colony, GoodsType goodsType) {
        Client client = freeColClient.getClient();
        ExportData data = colony.getExportData(goodsType);

        Element setGoodsLevelsElement = Message.createNewRootElement("setGoodsLevels");
        setGoodsLevelsElement.setAttribute("colony", colony.getId());
        setGoodsLevelsElement.appendChild(data.toXMLElement(colony.getOwner(), setGoodsLevelsElement
                                                            .getOwnerDocument()));

        client.sendAndWait(setGoodsLevelsElement);
    }

    
    public void equipUnit(Unit unit, EquipmentType type, int amount) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }
        if (amount == 0) {
            
            return;
        }

        Client client = freeColClient.getClient();
        Player myPlayer = freeColClient.getMyPlayer();

        Unit carrier = null;
        if (unit.isOnCarrier()) {
            carrier = (Unit) unit.getLocation();
            if (!leaveShip(unit)) return;
        }

        Element equipUnitElement = Message.createNewRootElement("equipUnit");
        equipUnitElement.setAttribute("unit", unit.getId());
        equipUnitElement.setAttribute("type", type.getId());
        equipUnitElement.setAttribute("amount", Integer.toString(amount));

        if (amount > 0) {
            for (AbstractGoods requiredGoods : type.getGoodsRequired()) {
                GoodsType goodsType = requiredGoods.getType();
                if (unit.isInEurope()) {
                    if (!myPlayer.canTrade(goodsType)) {
                        payArrears(goodsType);
                        if (!myPlayer.canTrade(goodsType)) {
                            return; 
                        }
                    }
                }
            }
            unit.equipWith(type, amount);
        } else {
            unit.removeEquipment(type, -amount);
        }

        freeColClient.getCanvas().updateGoldLabel();

        client.sendAndWait(equipUnitElement);

        if (unit.getLocation() instanceof Colony || unit.getLocation() instanceof Building
            || unit.getLocation() instanceof ColonyTile) {
            putOutsideColony(unit);
        } else if (carrier != null) {
            boardShip(unit, carrier);
        }
    }

    
    public void work(Unit unit, WorkLocation workLocation) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Tile tile = workLocation.getTile();
        if ((tile.getOwner() != unit.getOwner()
             || tile.getOwningSettlement() != workLocation.getColony())
            && !claimLand(tile, workLocation.getColony(), 0)) {
            logger.warning("Unit " + unit.getId()
                           + " is unable to claim tile " + tile.toString());
            return;
        }

        Client client = freeColClient.getClient();
        Element workElement = Message.createNewRootElement("work");
        workElement.setAttribute("unit", unit.getId());
        workElement.setAttribute("workLocation", workLocation.getId());

        unit.work(workLocation);

        client.sendAndWait(workElement);
    }

    
    public boolean putOutsideColony(Unit unit) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            throw new IllegalStateException("Not your turn.");
        } else if (unit.getColony() == null) {
            throw new IllegalStateException("Unit is not in colony.");
        } else if (!unit.getColony().canReducePopulation()) {
            throw new IllegalStateException("Colony can not reduce population.");
        }

        int oldPopulation = unit.getColony().getUnitCount();
        Location oldLocation = unit.getLocation();

        Element putOutsideColonyElement = Message.createNewRootElement("putOutsideColony");
        putOutsideColonyElement.setAttribute("unit", unit.getId());

        Client client = freeColClient.getClient();
        Element reply = client.ask(putOutsideColonyElement);
        if (reply != null && reply.getTagName().equals("update")) {
            freeColClient.getInGameInputHandler().update(reply);
            
            if (oldLocation instanceof Building) {
                ((Building) oldLocation).firePropertyChange(Building.UNIT_CHANGE, unit, null);
            } else if (oldLocation instanceof ColonyTile) {
                ((ColonyTile) oldLocation).firePropertyChange(ColonyTile.UNIT_CHANGE, unit, null);
            }
            unit.getColony().firePropertyChange(Colony.ColonyChangeEvent.POPULATION_CHANGE.toString(),
                                                oldPopulation, unit.getColony().getUnitCount());
            unit.getTile().firePropertyChange(Tile.UNIT_CHANGE, null, unit);
        } else {
            logger.warning("putOutsideColony message missing update");
        }

        return true;
    }

    
    public void changeWorkType(Unit unit, GoodsType workType) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Client client = freeColClient.getClient();

        Element changeWorkTypeElement = Message.createNewRootElement("changeWorkType");
        changeWorkTypeElement.setAttribute("unit", unit.getId());
        changeWorkTypeElement.setAttribute("workType", workType.getId());

        unit.setWorkType(workType);

        client.sendAndWait(changeWorkTypeElement);
    }

    
    public void changeWorkImprovementType(Unit unit, TileImprovementType improvementType) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        if (!(unit.checkSetState(UnitState.IMPROVING))) {
            return; 
        }

        if (!improvementType.isNatural()
            && freeColClient.getMyPlayer() != unit.getTile().getOwner()
            && !claimLand(unit.getTile(), null, 0)) {
            logger.warning("Unit " + unit.getId()
                           + " is unable to claim tile " + unit.getTile().toString());
            return;
        }

        Element changeWorkTypeElement = Message.createNewRootElement("workImprovement");
        changeWorkTypeElement.setAttribute("unit", unit.getId());
        changeWorkTypeElement.setAttribute("improvementType", improvementType.getId());

        Element reply = freeColClient.getClient().ask(changeWorkTypeElement);
        Element containerElement = getChildElement(reply, TileItemContainer.getXMLElementTagName());
        if (containerElement != null) {
            TileItemContainer container = (TileItemContainer) freeColClient.getGame()
                .getFreeColGameObject(containerElement.getAttribute("ID"));
            if (container == null) {
                container = new TileItemContainer(freeColClient.getGame(), unit.getTile(), containerElement);
                unit.getTile().setTileItemContainer(container);
            } else {
                container.readFromXMLElement(containerElement);
            }
        }
        Element improvementElement = getChildElement(reply, TileImprovement.getXMLElementTagName());
        if (improvementElement != null) {
            TileImprovement improvement = (TileImprovement) freeColClient.getGame()
                .getFreeColGameObject(improvementElement.getAttribute("ID"));
            if (improvement == null) {
                improvement = new TileImprovement(freeColClient.getGame(), improvementElement);
                unit.getTile().add(improvement);
            } else {
                improvement.readFromXMLElement(improvementElement);
            }
            unit.work(improvement);
        }
    }

    
    public void assignTeacher(Unit student, Unit teacher) {
        Player player = freeColClient.getMyPlayer();

        if (freeColClient.getGame().getCurrentPlayer() != player) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }
        if (!student.canBeStudent(teacher)) {
            throw new IllegalStateException("Unit can not be student!");
        }
        if (!teacher.getColony().canTrain(teacher)) {
            throw new IllegalStateException("Unit can not be teacher!");
        }
        if (student.getOwner() != player) {
            throw new IllegalStateException("Student is not your unit!");
        }
        if (teacher.getOwner() != player) {
            throw new IllegalStateException("Teacher is not your unit!");
        }
        if (student.getColony() != teacher.getColony()) {
            throw new IllegalStateException("Student and teacher are not in the same colony!");
        }
        if (!(student.getLocation() instanceof WorkLocation)) {
            throw new IllegalStateException("Student is not in a WorkLocation!");
        }

        Element assignTeacherElement = Message.createNewRootElement("assignTeacher");
        assignTeacherElement.setAttribute("student", student.getId());
        assignTeacherElement.setAttribute("teacher", teacher.getId());

        if (student.getTeacher() != null) {
            student.getTeacher().setStudent(null);
        }
        student.setTeacher(teacher);
        if (teacher.getStudent() != null) {
            teacher.getStudent().setTeacher(null);
        }
        teacher.setStudent(student);

        freeColClient.getClient().sendAndWait(assignTeacherElement);
    }

    
    public void setBuildQueue(Colony colony, List<BuildableType> buildQueue) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        colony.setBuildQueue(buildQueue);

        Element setBuildQueueElement = Message.createNewRootElement("setBuildQueue");
        setBuildQueueElement.setAttribute("colony", colony.getId());
        setBuildQueueElement.setAttribute("size", Integer.toString(buildQueue.size()));
        for (int x = 0; x < buildQueue.size(); x++) {
            setBuildQueueElement.setAttribute("x" + Integer.toString(x), buildQueue.get(x).getId());
        }
        freeColClient.getClient().sendAndWait(setBuildQueueElement);
    }

    
    public void changeState(Unit unit, UnitState state) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Client client = freeColClient.getClient();
        Game game = freeColClient.getGame();
        Canvas canvas = freeColClient.getCanvas();

        if (!(unit.checkSetState(state))) {
            return; 
        }
        if (state == UnitState.FORTIFYING && unit.isOffensiveUnit() &&
            !unit.hasAbility("model.ability.piracy")) { 
            Tile tile = unit.getTile();
            if (tile != null && tile.getOwningSettlement() != null) { 
                Player myPlayer = unit.getOwner();
                Player enemy = tile.getOwningSettlement().getOwner();
                if (myPlayer != enemy && myPlayer.getStance(enemy) != Stance.ALLIANCE
                    && !confirmHostileAction(unit, tile.getOwningSettlement().getTile())) { 
                    return;
                }
            }
        }

        unit.setState(state);

        
        
        Element changeStateElement = Message.createNewRootElement("changeState");
        changeStateElement.setAttribute("unit", unit.getId());
        changeStateElement.setAttribute("state", state.toString());
        client.sendAndWait(changeStateElement);

        if (!freeColClient.getCanvas().isShowingSubPanel() &&
            (unit.getMovesLeft() == 0 || unit.getState() == UnitState.SENTRY ||
             unit.getState() == UnitState.SKIPPED)) {
            nextActiveUnit();
        } else {
            freeColClient.getCanvas().refresh();
        }

    }

    
    public void clearOrders(Unit unit) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        if (unit == null) {
            return;
        }

        if (unit.getState()==UnitState.IMPROVING) {
            
            
            ModelMessage message = new ModelMessage(unit, ModelMessage.MessageType.WARNING, unit,
                                                    "model.unit.confirmCancelWork", "%turns%", new Integer(unit.getWorkLeft()).toString());
            boolean cancelWork = freeColClient.getCanvas().showConfirmDialog(new ModelMessage[] {message}, "yes", "no");
            if (!cancelWork) {
                return;
            }
        }

        
        clearGotoOrders(unit);
        assignTradeRoute(unit, TradeRoute.NO_TRADE_ROUTE);
        changeState(unit, UnitState.ACTIVE);
    }

    
    public void clearGotoOrders(Unit unit) {
        if (unit == null) {
            return;
        }

        
        if (unit.getDestination() != null)
            setDestination(unit, null);
    }

    
    public void moveToEurope(Unit unit) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Client client = freeColClient.getClient();

        unit.moveToEurope();

        Element moveToEuropeElement = Message.createNewRootElement("moveToEurope");
        moveToEuropeElement.setAttribute("unit", unit.getId());

        client.sendAndWait(moveToEuropeElement);
    }

    
    public void moveToAmerica(Unit unit) {
        final Canvas canvas = freeColClient.getCanvas();
        final Player player = freeColClient.getMyPlayer();
        if (freeColClient.getGame().getCurrentPlayer() != player) {
            canvas.showInformationMessage("notYourTurn");
            return;
        }

        final Client client = freeColClient.getClient();
        final ClientOptions co = canvas.getClient().getClientOptions();

        
        if (unit.getLocation() instanceof Europe) {
            final boolean autoload = co.getBoolean(ClientOptions.AUTOLOAD_EMIGRANTS);
            if (autoload) {
                int spaceLeft = unit.getSpaceLeft();
                List<Unit> unitsInEurope = new ArrayList<Unit>(unit.getLocation().getUnitList());
                for (Unit possiblePassenger : unitsInEurope) {
                    if (possiblePassenger.isNaval()) {
                        continue;
                    }
                    if (possiblePassenger.getType().getSpaceTaken() <= spaceLeft) {
                        boardShip(possiblePassenger, unit);
                        spaceLeft -= possiblePassenger.getType().getSpaceTaken();
                    } else {
                        break;
                    }
                }
            }
        }
        unit.moveToAmerica();

        Element moveToAmericaElement = Message.createNewRootElement("moveToAmerica");
        moveToAmericaElement.setAttribute("unit", unit.getId());

        client.sendAndWait(moveToAmericaElement);
    }

    
    public void trainUnitInEurope(UnitType unitType) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Client client = freeColClient.getClient();
        Canvas canvas = freeColClient.getCanvas();
        Game game = freeColClient.getGame();
        Player myPlayer = freeColClient.getMyPlayer();
        Europe europe = myPlayer.getEurope();

        if (myPlayer.getGold() < europe.getUnitPrice(unitType)) {
            canvas.errorMessage("notEnoughGold");
            return;
        }

        Element trainUnitInEuropeElement = Message.createNewRootElement("trainUnitInEurope");
        trainUnitInEuropeElement.setAttribute("unitType", unitType.getId());

        Element reply = client.ask(trainUnitInEuropeElement);
        if (reply.getTagName().equals("trainUnitInEuropeConfirmed")) {
            Element unitElement = (Element) reply.getFirstChild();
            Unit unit = (Unit) game.getFreeColGameObject(unitElement.getAttribute("ID"));
            if (unit == null) {
                unit = new Unit(game, unitElement);
            } else {
                unit.readFromXMLElement(unitElement);
            }
            europe.train(unit);
        } else {
            logger.warning("Could not train unit in europe.");
            return;
        }

        freeColClient.getCanvas().updateGoldLabel();
    }

    
    public void payForBuilding(Colony colony) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        if (!freeColClient.getCanvas()
            .showConfirmDialog("payForBuilding.text", "payForBuilding.yes", "payForBuilding.no",
                               "%replace%", Integer.toString(colony.getPriceForBuilding()))) {
            return;
        }

        if (!colony.canPayToFinishBuilding()) {
            freeColClient.getCanvas().errorMessage("notEnoughGold");
            return;
        }

        Element payForBuildingElement = Message.createNewRootElement("payForBuilding");
        payForBuildingElement.setAttribute("colony", colony.getId());

        colony.payForBuilding();

        freeColClient.getClient().sendAndWait(payForBuildingElement);
    }

    
    public void recruitUnitInEurope(int slot) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Client client = freeColClient.getClient();
        Canvas canvas = freeColClient.getCanvas();
        Game game = freeColClient.getGame();
        Player myPlayer = freeColClient.getMyPlayer();
        Europe europe = myPlayer.getEurope();

        if (myPlayer.getGold() < myPlayer.getRecruitPrice()) {
            canvas.errorMessage("notEnoughGold");
            return;
        }

        Element recruitUnitInEuropeElement = Message.createNewRootElement("recruitUnitInEurope");
        recruitUnitInEuropeElement.setAttribute("slot", Integer.toString(slot));

        Element reply = client.ask(recruitUnitInEuropeElement);
        if (reply.getTagName().equals("recruitUnitInEuropeConfirmed")) {
            Element unitElement = (Element) reply.getFirstChild();
            Unit unit = (Unit) game.getFreeColGameObject(unitElement.getAttribute("ID"));
            if (unit == null) {
                unit = new Unit(game, unitElement);
            } else {
                unit.readFromXMLElement(unitElement);
            }
            String unitId = reply.getAttribute("newRecruitable");
            UnitType unitType = FreeCol.getSpecification().getUnitType(unitId);
            europe.recruit(slot, unit, unitType);
        } else {
            logger.warning("Could not recruit the specified unit in europe.");
            return;
        }

        freeColClient.getCanvas().updateGoldLabel();
    }

    
    public void updateTradeRoute(TradeRoute route) {
        logger.finest("Entering method updateTradeRoute");
        
        Element tradeRouteElement = Message.createNewRootElement("updateTradeRoute");
        tradeRouteElement.appendChild(route.toXMLElement(null, tradeRouteElement.getOwnerDocument()));
        freeColClient.getClient().sendAndWait(tradeRouteElement);

    }

    
    public void setTradeRoutes(List<TradeRoute> routes) {
        Player myPlayer = freeColClient.getMyPlayer();
        myPlayer.setTradeRoutes(routes);
        
        Element tradeRoutesElement = Message.createNewRootElement("setTradeRoutes");
        for(TradeRoute route : routes) {
            Element routeElement = tradeRoutesElement.getOwnerDocument().createElement(TradeRoute.getXMLElementTagName());
            routeElement.setAttribute("id", route.getId());
            tradeRoutesElement.appendChild(routeElement);
        }
        freeColClient.getClient().sendAndWait(tradeRoutesElement);

    }

    
    public void assignTradeRoute(Unit unit) {
        Canvas canvas = freeColClient.getCanvas();
        TradeRoute tradeRoute = canvas.showFreeColDialog(new TradeRouteDialog(canvas, unit.getTradeRoute()));
        assignTradeRoute(unit, tradeRoute);
    }

    public void assignTradeRoute(Unit unit, TradeRoute tradeRoute) {
        if (tradeRoute != null) {
            Element assignTradeRouteElement = Message.createNewRootElement("assignTradeRoute");
            assignTradeRouteElement.setAttribute("unit", unit.getId());
            if (tradeRoute == TradeRoute.NO_TRADE_ROUTE) {
                unit.setTradeRoute(null);
                freeColClient.getClient().sendAndWait(assignTradeRouteElement);
                setDestination(unit, null);
            } else {
                unit.setTradeRoute(tradeRoute);
                assignTradeRouteElement.setAttribute("tradeRoute", tradeRoute.getId());
                freeColClient.getClient().sendAndWait(assignTradeRouteElement);
                Location location = unit.getLocation();
                if (location instanceof Tile)
                    location = ((Tile) location).getColony();
                if (tradeRoute.getStops().get(0).getLocation() == location) {
                    followTradeRoute(unit);
                } else if (freeColClient.getGame().getCurrentPlayer() == freeColClient.getMyPlayer()) {
                    moveToDestination(unit);
                }
            }
        }
    }

    
    public void payArrears(Goods goods) {
        payArrears(goods.getType());
    }

    
    public void payArrears(GoodsType type) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        Client client = freeColClient.getClient();
        Player player = freeColClient.getMyPlayer();

        int arrears = player.getArrears(type);
        if (player.getGold() >= arrears) {
            if (freeColClient.getCanvas().showConfirmDialog("model.europe.payArrears", "ok", "cancel",
                                                            "%replace%", String.valueOf(arrears))) {
                player.modifyGold(-arrears);
                freeColClient.getCanvas().updateGoldLabel();
                player.resetArrears(type);
                
                Element payArrearsElement = Message.createNewRootElement("payArrears");
                payArrearsElement.setAttribute("goodsType", type.getId());
                client.sendAndWait(payArrearsElement);
            }
        } else {
            freeColClient.getCanvas().showInformationMessage("model.europe.cantPayArrears",
                                                             "%amount%", String.valueOf(arrears));
        }
    }

    
    public void purchaseUnitFromEurope(UnitType unitType) {
        trainUnitInEurope(unitType);
    }

    
    public Element getForeignAffairsReport() {
        return freeColClient.getClient().ask(Message.createNewRootElement("foreignAffairs"));
    }

    
    public Element getHighScores() {
        return freeColClient.getClient().ask(Message.createNewRootElement("highScores"));
    }

    
    public List<AbstractUnit> getREFUnits() {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return Collections.emptyList();
        }

        Element reply = freeColClient.getClient().ask(Message.createNewRootElement("getREFUnits"));
        if (reply == null) {
            return Collections.emptyList();
        } else {
            List<AbstractUnit> result = new ArrayList<AbstractUnit>();
            NodeList childElements = reply.getChildNodes();
            for (int index = 0; index < childElements.getLength(); index++) {
                AbstractUnit unit = new AbstractUnit();
                unit.readFromXMLElement((Element) childElements.item(index));
                result.add(unit);
            }
            return result;
        }
    }

    
    public void disbandActiveUnit() {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        GUI gui = freeColClient.getGUI();
        Unit unit = gui.getActiveUnit();
        Client client = freeColClient.getClient();

        if (unit == null) {
            return;
        }

        if (!freeColClient.getCanvas().showConfirmDialog("disbandUnit.text", "disbandUnit.yes", "disbandUnit.no")) {
            return;
        }

        Element disbandUnit = Message.createNewRootElement("disbandUnit");
        disbandUnit.setAttribute("unit", unit.getId());

        unit.dispose();

        client.sendAndWait(disbandUnit);

        nextActiveUnit();
    }

    
    public void centerActiveUnit() {
        Unit activeUnit = freeColClient.getGUI().getActiveUnit();
        if (activeUnit == null){
            return;
        }

        centerOnUnit(activeUnit);
    }

    
    public void centerOnUnit(Unit unit) {
        
        if(unit == null){
            return;
        }
        Tile unitTile = unit.getTile();
        if(unitTile == null){
            return;
        }
        
        freeColClient.getGUI().setFocus(unitTile.getPosition());
    }
    
    
    public void executeGotoOrders() {
        executeGoto = true;
        nextActiveUnit(null);
    }

    
    public void nextActiveUnit() {
        nextActiveUnit(null);
    }

    
    public void nextActiveUnit(Tile tile) {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        nextModelMessage();

        Canvas canvas = freeColClient.getCanvas();
        Player myPlayer = freeColClient.getMyPlayer();
        if (endingTurn || executeGoto) {
            while (!freeColClient.getCanvas().isShowingSubPanel() && myPlayer.hasNextGoingToUnit()) {
                Unit unit = myPlayer.getNextGoingToUnit();
                moveToDestination(unit);
                nextModelMessage();
                if (unit.getMovesLeft() > 0) {
                    if (endingTurn) {
                        unit.setMovesLeft(0);
                    } else {
                        return;
                    }
                }
            }

            if (!myPlayer.hasNextGoingToUnit() && !freeColClient.getCanvas().isShowingSubPanel()) {
                if (endingTurn) {
                    canvas.getGUI().setActiveUnit(null);
                    endingTurn = false;

                    Element endTurnElement = Message.createNewRootElement("endTurn");
                    freeColClient.getClient().send(endTurnElement);
                    return;
                } else {
                    executeGoto = false;
                }
            }
        }
        
        GUI gui = freeColClient.getGUI();
        Unit nextActiveUnit = myPlayer.getNextActiveUnit();

        if (nextActiveUnit != null) {
            canAutoEndTurn = true;
            gui.setActiveUnit(nextActiveUnit);
        } else {
            
            nextActiveUnit = myPlayer.getNextGoingToUnit();
            if (nextActiveUnit != null) {
                moveToDestination(nextActiveUnit);
            } else if (tile != null) {
                Position p = tile.getPosition();
                if (p != null) {
                    
                    gui.setSelectedTile(p);
                }
                gui.setActiveUnit(null);
            } else {
                gui.setActiveUnit(null);
            }
            
            if (canAutoEndTurn && !endingTurn
                && freeColClient.getClientOptions().getBoolean(ClientOptions.AUTO_END_TURN)) {
                endTurn();
            }
        }
    }

    
    public synchronized void ignoreMessage(ModelMessage message, boolean flag) {
        String key = message.getSource().getId();
        String[] data = message.getData();
        for (int index = 0; index < data.length; index += 2) {
            if (data[index].equals("%goods%")) {
                key += data[index + 1];
                break;
            }
        }
        if (flag) {
            startIgnoringMessage(key, freeColClient.getGame().getTurn().getNumber());
        } else {
            stopIgnoringMessage(key);
        }
    }

    
    public void nextModelMessage() {
        displayModelMessages(false);
    }

    public void displayModelMessages(final boolean allMessages) {

        int thisTurn = freeColClient.getGame().getTurn().getNumber();

        final ArrayList<ModelMessage> messageList = new ArrayList<ModelMessage>();
        List<ModelMessage> inputList;
        if (allMessages) {
            inputList = freeColClient.getMyPlayer().getModelMessages();
        } else {
            inputList = freeColClient.getMyPlayer().getNewModelMessages();
        }

        for (ModelMessage message : inputList) {
            if (shouldAllowMessage(message)) {
                if (message.getType() == ModelMessage.MessageType.WAREHOUSE_CAPACITY) {
                    String key = message.getSource().getId();
                    String[] data = message.getData();
                    for (int index = 0; index < data.length; index += 2) {
                        if (data[index].equals("%goods%")) {
                            key += data[index + 1];
                            break;
                        }
                    }

                    Integer turn = getTurnForMessageIgnored(key);
                    if (turn != null && turn.intValue() == thisTurn - 1) {
                        startIgnoringMessage(key, thisTurn);
                        message.setBeenDisplayed(true);
                        continue;
                    }
                } else if (message.getType() == ModelMessage.MessageType.BUILDING_COMPLETED) {
                    freeColClient.playSound(SoundEffect.BUILDING_COMPLETE);
                } else if (message.getType() == ModelMessage.MessageType.FOREIGN_DIPLOMACY) {
                    if (message.getId().equals("EventPanel.MEETING_AZTEC")) {
                        freeColClient.playMusicOnce("aztec");
                    }
                }
                messageList.add(message);
            }

            
            message.setBeenDisplayed(true);
        }

        purgeOldMessagesFromMessagesToIgnore(thisTurn);
        final ModelMessage[] messages = messageList.toArray(new ModelMessage[0]);

        Runnable uiTask = new Runnable() {
                public void run() {
                    Canvas canvas = freeColClient.getCanvas();
                    if (messageList.size() > 0) {
                        if (allMessages || messageList.size() > 5) {
                            canvas.addAsFrame(new ReportTurnPanel(canvas, messages));
                        } else {
                            canvas.showModelMessages(messages);
                        }
                    }
                    freeColClient.getActionManager().update();
                }
            };
        if (SwingUtilities.isEventDispatchThread()) {
            uiTask.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(uiTask);
            } catch (InterruptedException e) {
                
            } catch (InvocationTargetException e) {
                
            }
        }
    }

    private synchronized Integer getTurnForMessageIgnored(String key) {
        return messagesToIgnore.get(key);
    }

    private synchronized void startIgnoringMessage(String key, int turn) {
        logger.finer("Ignoring model message with key " + key);
        messagesToIgnore.put(key, new Integer(turn));
    }

    private synchronized void stopIgnoringMessage(String key) {
        logger.finer("Removing model message with key " + key + " from ignored messages.");
        messagesToIgnore.remove(key);
    }

    private synchronized void purgeOldMessagesFromMessagesToIgnore(int thisTurn) {
        List<String> keysToRemove = new ArrayList<String>();
        for (Entry<String, Integer> entry : messagesToIgnore.entrySet()) {
            if (entry.getValue().intValue() < thisTurn - 1) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer("Removing old model message with key " + entry.getKey() + " from ignored messages.");
                }
                keysToRemove.add(entry.getKey());
            }
        }
        for (String key : keysToRemove) {
            stopIgnoringMessage(key);
        }
    }

    
    private boolean shouldAllowMessage(ModelMessage message) {

        switch (message.getType()) {
        case DEFAULT:
            return true;
        case WARNING:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_WARNING);
        case SONS_OF_LIBERTY:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_SONS_OF_LIBERTY);
        case GOVERNMENT_EFFICIENCY:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_GOVERNMENT_EFFICIENCY);
        case WAREHOUSE_CAPACITY:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_WAREHOUSE_CAPACITY);
        case UNIT_IMPROVED:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_UNIT_IMPROVED);
        case UNIT_DEMOTED:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_UNIT_DEMOTED);
        case UNIT_LOST:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_UNIT_LOST);
        case UNIT_ADDED:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_UNIT_ADDED);
        case BUILDING_COMPLETED:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_BUILDING_COMPLETED);
        case FOREIGN_DIPLOMACY:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_FOREIGN_DIPLOMACY);
        case MARKET_PRICES:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_MARKET_PRICES);
        case MISSING_GOODS:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_MISSING_GOODS);
        case TUTORIAL:
            return freeColClient.getClientOptions().getBoolean(ClientOptions.SHOW_TUTORIAL);
        default:
            return true;
        }
    }

    
    public void endTurn() {
        if (freeColClient.getGame().getCurrentPlayer() != freeColClient.getMyPlayer()) {
            freeColClient.getCanvas().showInformationMessage("notYourTurn");
            return;
        }

        endingTurn = true;
        canAutoEndTurn = false;

        nextActiveUnit(null);
    }

    
    protected Element getChildElement(Element element, String tagName) {
        NodeList n = element.getChildNodes();
        for (int i = 0; i < n.getLength(); i++) {
            if (((Element) n.item(i)).getTagName().equals(tagName)) {
                return (Element) n.item(i);
            }
        }

        return null;
    }

    
    public void abandonColony(Colony colony) {
        if (colony == null) {
            return;
        }

        Client client = freeColClient.getClient();

        Element abandonColony = Message.createNewRootElement("abandonColony");
        abandonColony.setAttribute("colony", colony.getId());
        colony.getOwner().getHistory()
            .add(new HistoryEvent(colony.getGame().getTurn().getNumber(),
                                  HistoryEvent.Type.ABANDON_COLONY,
                                  "%colony%", colony.getName()));

        colony.dispose();
        client.sendAndWait(abandonColony);
    }
    
    
    public StatisticsMessage getServerStatistics() {
        Element request = Message.createNewRootElement(StatisticsMessage.getXMLElementTagName());
        Element reply = freeColClient.getClient().ask(request);
        StatisticsMessage m = new StatisticsMessage(reply);
        return m;
    }
}
