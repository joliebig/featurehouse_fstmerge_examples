

package net.sf.freecol.common.networking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Region;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.model.ServerPlayer;



public class MoveMessage extends Message {
    
    private String unitId;

    
    private String directionString;

    
    public MoveMessage(Unit unit, Direction direction) {
        this.unitId = unit.getId();
        this.directionString = String.valueOf(direction);
    }

    
    public MoveMessage(Game game, Element element) {
        this.unitId = element.getAttribute("unit");
        this.directionString = element.getAttribute("direction");
    }

    
    private void updateOthers(List<ServerPlayer> others,
                              List<ServerPlayer> contacts,
                              Unit unit, Tile oldTile,
                              Direction direction, Tile newTile) {
        for (ServerPlayer enemyPlayer : others) {
            if (!enemyPlayer.isConnected()) continue;
            Boolean seeOld = enemyPlayer.canSee(oldTile)
                && oldTile.getSettlement() == null;
            Boolean seeNew = enemyPlayer.canSee(newTile)
                && newTile.getSettlement() == null;
            if (seeOld || seeNew) {
                Element multiple = Message.createNewRootElement("multiple");
                Document doc = multiple.getOwnerDocument();
                
                
                
                
                Element move = doc.createElement("opponentMove");
                multiple.appendChild(move);
                move.setAttribute("unit", unit.getId());
                move.setAttribute("oldTile", oldTile.getId());
                move.setAttribute("newTile", newTile.getId());
                
                
                
                
                
                
                
                
                
                
                
                move.appendChild(unit.toXMLElement(enemyPlayer, doc,
                                                   false, false));

                
                
                if (contacts.contains(enemyPlayer)) {
                    
                    Element stance = doc.createElement("setStance");
                    multiple.appendChild(stance);
                    stance.setAttribute("stance", Stance.PEACE.toString());
                    stance.setAttribute("first", enemyPlayer.getId());
                    stance.setAttribute("second", unit.getOwner().getId());
                }

                
                
                
                Element update = doc.createElement("update");
                multiple.appendChild(update);
                if (seeOld) {
                    update.appendChild(oldTile.toXMLElement(enemyPlayer, doc,
                                                            false, false));
                }
                if (seeNew && !unit.isDisposed()) {
                    update.appendChild(newTile.toXMLElement(enemyPlayer, doc,
                                                            false, false));
                } else {
                    Element remove = doc.createElement("remove");
                    multiple.appendChild(remove);
                    unit.addToRemoveElement(remove);
                }
                try {
                    enemyPlayer.getConnection().sendAndWait(multiple);
                } catch (IOException e) {
                    logger.warning(e.getMessage());
                }
            }
        }
    }

    
    public Element handle(FreeColServer server, Player player,
                          Connection connection) {
        ServerPlayer serverPlayer = server.getPlayer(connection);
        Game game = server.getGame();
        Unit unit;
        try {
            unit = server.getUnitSafely(unitId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        Tile oldTile = unit.getTile();
        if (oldTile == null) {
            return Message.clientError("Unit is not on the map: " + unitId);
        }
        Location oldLocation = unit.getLocation();
        Direction direction = Enum.valueOf(Direction.class, directionString);
        Tile newTile = game.getMap().getNeighbourOrNull(direction, oldTile);
        if (newTile == null) {
            return Message.clientError("Could not find tile"
                                       + " in direction: " + direction
                                       + " from unit: " + unitId);
        }
        MoveType moveType = unit.getMoveType(direction);
        if (!moveType.isProgress()) {
            return Message.clientError("Illegal move for: " + unitId
                                       + " type: " + moveType
                                       + " from: " + oldLocation.getId()
                                       + " to: " + newTile.getId());
        }

        
        
        InGameController controller = server.getInGameController();
        List<FreeColObject> objects = new ArrayList<FreeColObject>();
        int los = unit.getLineOfSight();
        for (Tile tile : game.getMap().getSurroundingTiles(newTile, los)) {
            if (!player.canSee(tile)) {
                objects.add(tile);
            }
        }

        
        
        List<ServerPlayer> contacts
            = controller.findAdjacentUncontacted(serverPlayer, newTile);
        objects.addAll(controller.move(serverPlayer, unit, newTile));

        
        updateOthers(controller.getOtherPlayers(serverPlayer), contacts,
                     unit, oldLocation.getTile(), direction, newTile);

        
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element addMessages = null;
        Element addHistory = null;
        Element remove = null;

        
        Element updates = doc.createElement("update");
        reply.appendChild(updates);
        if (oldLocation instanceof Tile) {
            updates.appendChild(((Tile) oldLocation).toXMLElement(player, doc));
        } else if (oldLocation instanceof Unit) {
            updates.appendChild(((Unit) oldLocation).toXMLElement(player, doc));
            unit.setMovesLeft(0); 
        } else {
            throw new IllegalArgumentException("Location not a tile or unit!?!: " + unit.getId());
        }
        

        
        if (unit.isDisposed()) {
            remove = doc.createElement("remove");
            unit.addToRemoveElement(remove);
        }

        
        for (FreeColObject object : objects) {
            if (object == player) { 
                updates.appendChild(player.toXMLElementPartial(doc, "gold", "score"));
            } else if (object instanceof ModelMessage) {
                if (addMessages == null) {
                    addMessages = doc.createElement("addMessages");
                }
                addMessages.appendChild(object.toXMLElement(player, doc));
            } else if (object instanceof HistoryEvent) {
                if (addHistory == null) {
                    addHistory = doc.createElement("addHistory");
                }
                addHistory.appendChild(object.toXMLElement(player, doc));
            } else if (object instanceof ServerPlayer && contacts.contains(object)) {
                ServerPlayer other = (ServerPlayer) object;
                Element stance = doc.createElement("setStance");
                reply.appendChild(stance);
                stance.setAttribute("stance", Stance.PEACE.toString());
                stance.setAttribute("first", serverPlayer.getId());
                stance.setAttribute("second", other.getId());
                
                HistoryEvent h = new HistoryEvent(game.getTurn().getNumber(),
                                                  HistoryEvent.Type.MEET_NATION,
                                                  "%nation%", other.getNationAsString());
                serverPlayer.addHistory(h);
                if (addHistory == null) {
                    addHistory = doc.createElement("addHistory");
                }
                addHistory.appendChild(h.toXMLElement(player, doc));
            } else { 
                updates.appendChild(object.toXMLElement(player, doc,
                                                        false, false));
            }
        }

        
        
        if (!unit.isDisposed() && player.isEuropean()) {
            Unit slowedBy = controller.getSlowedBy(unit, newTile);
            if (slowedBy != null) {
                reply.setAttribute("slowedBy", slowedBy.getId());
            }

            if (newTile.isLand() && !player.isNewLandNamed()) {
                String newLandName = player.getDefaultNewLandName();
                if (player.isAI()) {
                    
                    
                    
                    
                    player.setNewLandName(newLandName);
                } else { 
                    reply.setAttribute("nameNewLand", newLandName);
                }
            }

            Region region = newTile.getDiscoverableRegion();
            if (region != null) {
                HistoryEvent h = null;
                if (region.isPacific()) {
                    reply.setAttribute("discoverPacific", "true");
                    h = region.discover(serverPlayer, game.getTurn(),
                                        "model.region.pacific");
                } else {
                    String regionName = player.getDefaultRegionName(region.getType());
                    if (player.isAI()) {
                        
                        h = region.discover(serverPlayer, game.getTurn(),
                                            regionName);
                        controller.sendUpdateToAll(region, serverPlayer);
                    } else { 
                        reply.setAttribute("discoverRegion", regionName);
                        reply.setAttribute("regionType", region.getDisplayName());
                    }
                }
                if (h != null) {
                    if (addHistory == null) {
                        addHistory = doc.createElement("addHistory");
                    }
                    serverPlayer.addHistory(h);
                    addHistory.appendChild(h.toXMLElement(player, doc));
                    updates.appendChild(region.toXMLElement(player, doc));
                }
            }

            int emigrants = serverPlayer.getRemainingEmigrants();
            if (emigrants > 0) {
                reply.setAttribute("fountainOfYouth", Integer.toString(emigrants));
            }
        }

        
        
        
        
        
        
        updates.appendChild(newTile.toXMLElement(player, doc, false, false));

        
        if (addMessages != null) reply.appendChild(addMessages);
        if (addHistory != null) reply.appendChild(addHistory);
        if (remove != null) reply.appendChild(remove);
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("unit", this.unitId);
        result.setAttribute("direction", this.directionString);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "move";
    }
}
