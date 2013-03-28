

package net.sf.freecol.server.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.ModelController;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.TradeRoute;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;


public class ServerModelController implements ModelController,PropertyChangeListener {

    private static final Logger logger = Logger.getLogger(ServerModelController.class.getName());

    private final FreeColServer freeColServer;

    private final HashMap<String, TaskEntry> taskRegister = new HashMap<String, TaskEntry>();


    
    public ServerModelController(FreeColServer freeColServer) {
        this.freeColServer = freeColServer;
    }

    
    public synchronized int getRandom(String taskID, int n) {
        int turnNumber = freeColServer.getGame().getTurn().getNumber();
        String extendedTaskID = taskID + Integer.toString(turnNumber);
        
        if (taskRegister.containsKey(extendedTaskID)) {
            return ((Integer) taskRegister.get(extendedTaskID).entry).intValue();
        } else {
            int value = getPseudoRandom().nextInt(n);
            taskRegister.put(extendedTaskID, new TaskEntry(extendedTaskID, turnNumber, true, new Integer(value)));
            return value;
        }
    }

    
    public PseudoRandom getPseudoRandom() {
        return this.freeColServer.getPseudoRandom();
    }

    
    public synchronized void clearTaskRegister() {
        int currentTurn = freeColServer.getGame().getTurn().getNumber();
        List<String> idsToRemove = new ArrayList<String>();
        for (TaskEntry te : taskRegister.values()) {
            if (te.hasExpired(currentTurn)) {
                if (!te.isSecure()) {
                    logger.warning("Possibly a cheating attempt.");
                }
                idsToRemove.add(te.taskID);
            }
        }
        if (!idsToRemove.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Clearing the task register. Removing the following items:");
            for (String id : idsToRemove) {
                taskRegister.remove(id);
                sb.append(" ");
                sb.append(id);
            }
            logger.info(sb.toString());
        }
    }

    
    public synchronized Unit createUnit(String taskID, Location location, Player owner, UnitType type) {
        return createUnit(taskID, location, owner, type, true, null);
    }

    
    public synchronized Unit createUnit(String taskID, Location location, Player owner, UnitType type, boolean secure,
            Connection connection) {
        String extendedTaskID = taskID + owner.getId()
                + Integer.toString(freeColServer.getGame().getTurn().getNumber());
        Unit unit;
        TaskEntry taskEntry;

        logger.info("Entering createUnit.");

        if (taskRegister.containsKey(extendedTaskID)) {
            taskEntry = taskRegister.get(extendedTaskID);
            unit = (Unit) taskEntry.entry;

            if (unit.getLocation().getTile() != location.getTile() || unit.getOwner() != owner
                    || unit.getType() != type) {
                logger
                        .warning("Unsynchronization between the client and the server. Maybe a cheating attempt! Differences: "
                                + ((unit.getLocation().getTile() != location.getTile()) ? "location: "
                                        + unit.getLocation().getTile() + "!=" + location.getTile() : "")
                                + ((unit.getOwner() != owner) ? "owner: " + unit.getOwner() + "!=" + owner : "")
                                + ((unit.getType() != type) ? "type: " + unit.getType() + "!=" + type : ""));

                taskRegister.remove(extendedTaskID);
                unit.dispose();
                return null;
            }

            if (secure) {
                taskEntry.secure = true;
            }
        } else {
            unit = new Unit(freeColServer.getGame(), location, owner, type, UnitState.ACTIVE);
            taskEntry = new TaskEntry(extendedTaskID, freeColServer.getGame().getTurn().getNumber(), secure, unit);
            taskRegister.put(extendedTaskID, taskEntry);
        }

        

        return unit;
    }

    
    public synchronized Building createBuilding(String taskID, Colony colony, BuildingType type) {
        return createBuilding(taskID, colony, type, true, null);
    }

    
    public synchronized Building createBuilding(String taskID, Colony colony, BuildingType type, boolean secure,
                                                Connection connection) {
        String extendedTaskID = taskID + colony.getOwner().getId()
                + Integer.toString(freeColServer.getGame().getTurn().getNumber());
        Building building;
        TaskEntry taskEntry;
        Player owner = colony.getOwner();

        logger.info("Entering createBuilding.");

        if (taskRegister.containsKey(extendedTaskID)) {
            taskEntry = taskRegister.get(extendedTaskID);
            building = (Building) taskEntry.entry;

            if (building.getColony().getTile() != colony.getTile() ||
                building.getOwner() != colony.getOwner() ||
                building.getType() != type) {
                logger.warning("Unsynchronization between the client and the server. Maybe a cheating attempt! Differences: "
                               + ((building.getColony().getTile() != colony.getTile()) ? "colony: "
                                  + building.getColony().getTile() + "!=" + colony.getTile() : "")
                               + ((building.getOwner() != owner) ? "owner: " + building.getOwner() + "!=" + owner : "")
                               + ((building.getType() != type) ? "type: " + building.getType() + "!=" + type : ""));
                
                taskRegister.remove(extendedTaskID);
                building.dispose();
                return null;
            }

            if (secure) {
                taskEntry.secure = true;
            }
        } else {
            building = new Building(freeColServer.getGame(), colony,type);
            taskEntry = new TaskEntry(extendedTaskID, freeColServer.getGame().getTurn().getNumber(), secure, building);
            taskRegister.put(extendedTaskID, taskEntry);
        }

        

        return building;
    }

    
    public synchronized Location setToVacantEntryLocation(Unit unit) {
        Game game = freeColServer.getGame();
        ServerPlayer player = (ServerPlayer) unit.getOwner();
        Location entryLocation;
        String taskID = unit.getId() + Integer.toString(freeColServer.getGame().getTurn().getNumber());

        if (taskRegister.containsKey(taskID)) {
            entryLocation = (Location) taskRegister.get(taskID).entry;

            
        } else {
            entryLocation = unit.getVacantEntryLocation();
            taskRegister.put(taskID, new TaskEntry(taskID, freeColServer.getGame().getTurn().getNumber(), true,
                    entryLocation));
        }

        unit.setLocation(entryLocation);
        unit.setState(UnitState.ACTIVE);

        
        Element updateElement = Message.createNewRootElement("update");
        List<Tile> surroundingTiles = game.getMap().getSurroundingTiles(unit.getTile(), unit.getLineOfSight());

        for (int i = 0; i < surroundingTiles.size(); i++) {
            Tile t = surroundingTiles.get(i);
            updateElement.appendChild(t.toXMLElement(player, updateElement.getOwnerDocument()));
        }

        try {
            player.getConnection().send(updateElement);
        } catch (Exception e) {
            logger.warning("Could not send message to: " + player.getName() + " with connection "
                    + player.getConnection());
        }

        
        update(unit.getTile(), player);

        return entryLocation;
    }

    
    public void update(Tile tile) {
        update(tile, null);
    }
    
    
    public void tileImprovementFinished(Unit unit, TileImprovement improvement){
        
        Tile tile = unit.getTile();
        TileType changeType = improvement.getChange(tile.getType());
        if (changeType != null) {
            
            tile.setType(changeType);
        } else {
            
            tile.add(improvement);
            
        }
        
        update(tile,unit.getOwner());
    }

    
    public void exploreTiles(Player player, ArrayList<Tile> tiles) {
        Element updateElement = Message.createNewRootElement("update");
        for (int i = 0; i < tiles.size(); i++) {
            Tile t = tiles.get(i);
            t.setExploredBy(player, true);
            updateElement.appendChild(t.toXMLElement(player, updateElement.getOwnerDocument()));
        }

        try {
            ((ServerPlayer) player).getConnection().send(updateElement);
        } catch (IOException e) {
            logger.warning("Could not send message to: " + ((ServerPlayer) player).getName() + " with connection "
                    + ((ServerPlayer) player).getConnection());
        }
    }

    
    public void setStance(Player first, Player second, Stance stance) {
        Element element = Message.createNewRootElement("setStance");
        element.setAttribute("stance", stance.toString());
        element.setAttribute("first", first.getId());
        element.setAttribute("second", second.getId());

        Iterator<Player> enemyPlayerIterator = first.getGame().getPlayerIterator();
        while (enemyPlayerIterator.hasNext()) {
            ServerPlayer enemyPlayer = (ServerPlayer) enemyPlayerIterator.next();
            if (!enemyPlayer.equals(first) && enemyPlayer.isConnected()) {
                try {
                    enemyPlayer.getConnection().send(element);
                } catch (IOException e) {
                    logger.warning("Could not send message to: " + enemyPlayer.getName() + " with connection "
                            + enemyPlayer.getConnection());
                }
            }
        }
    }

    
    public void update(Tile newTile, Player p) {
        ServerPlayer player = (ServerPlayer) p;
        Game game = freeColServer.getGame();

        Iterator<Player> enemyPlayerIterator = game.getPlayerIterator();
        while (enemyPlayerIterator.hasNext()) {
            ServerPlayer enemyPlayer = (ServerPlayer) enemyPlayerIterator.next();

            if (player != null && player.equals(enemyPlayer)) {
                continue;
            }

            try {
                if (enemyPlayer.canSee(newTile)) {
                    Element updateElement = Message.createNewRootElement("update");
                    updateElement.appendChild(newTile.toXMLElement(enemyPlayer, updateElement.getOwnerDocument()));

                    enemyPlayer.getConnection().send(updateElement);
                }
            } catch (IOException e) {
                logger.warning("Could not send message to: " + enemyPlayer.getName() + " with connection "
                        + enemyPlayer.getConnection());
            }
        }
    }

    
    public void update(Unit unit, Player p) {
        ServerPlayer player = (ServerPlayer) p;
        Game game = freeColServer.getGame();

        Iterator<Player> enemyPlayerIterator = game.getPlayerIterator();
        while (enemyPlayerIterator.hasNext()) {
            ServerPlayer enemyPlayer = (ServerPlayer) enemyPlayerIterator.next();

            if (player != null && player.equals(enemyPlayer)) {
                continue;
            }

            try {
                if (unit.isVisibleTo(enemyPlayer)) {
                    Element updateElement = Message.createNewRootElement("update");
                    updateElement.appendChild(unit.getTile()
                            .toXMLElement(enemyPlayer, updateElement.getOwnerDocument()));

                    enemyPlayer.getConnection().send(updateElement);
                }
            } catch (IOException e) {
                logger.warning("Could not send message to: " + enemyPlayer.getName() + " with connection "
                        + enemyPlayer.getConnection());
            }
        }
    }

    
    public TradeRoute getNewTradeRoute(Player player) {
        Game game = freeColServer.getGame();
        String name = "";
        return new TradeRoute(game, name, player);
    }

    
    private static class TaskEntry {
        final String taskID;

        final int createdTurn;

        final Object entry;

        private boolean secure;


        TaskEntry(String taskID, int createdTurn, boolean secure, Object entry) {
            this.taskID = taskID;
            this.createdTurn = createdTurn;
            this.secure = secure;
            this.entry = entry;
        }

        synchronized void setSecure(boolean secure) {
            this.secure = secure;
        }

        synchronized boolean isSecure() {
            return this.secure;
        }

        boolean hasExpired(int currentTurn) {
            return createdTurn + TASK_ENTRY_TIME_OUT < currentTurn;
        }


        
        private static final int TASK_ENTRY_TIME_OUT = 5;
    }
    
    public void updateModelListening(){
        for(Player player : freeColServer.getGame().getPlayers()){
            if(!player.isIndian()){
                continue;
            }
            for(Settlement settlement : player.getIndianSettlements()){
                settlement.addPropertyChangeListener("alarmLevel", this);
            }
        }
    }

    
    public void propertyChange(PropertyChangeEvent e) {
        if(e.getPropertyName() == "alarmLevel"){
            IndianSettlement settlement = (IndianSettlement) e.getSource();
            update(settlement.getTile());
        }
    }
}
