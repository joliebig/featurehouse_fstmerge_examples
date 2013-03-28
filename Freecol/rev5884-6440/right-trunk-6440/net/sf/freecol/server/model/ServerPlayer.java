


package net.sf.freecol.server.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.Ability;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.UnitTypeChange.ChangeType;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.option.BooleanOption;
import net.sf.freecol.common.util.Utils;



public class ServerPlayer extends Player implements ServerModelObject {
    
    
    private Socket socket;

    
    private Connection connection;

    private boolean connected = false;

    
    private int remainingEmigrants = 0;

    private String serverID;



    
    public ServerPlayer(Game game, String name, boolean admin, Socket socket, Connection connection) {
        super(game, name, admin);

        this.socket = socket;
        this.connection = connection;

        resetExploredTiles(getGame().getMap());
        resetCanSeeTiles();

        connected = (connection != null);
    }

    
    public ServerPlayer(Game game, String name, boolean admin, boolean ai, Socket socket, Connection connection,
                        Nation nation) {
        super(game, name, admin, ai, nation);

        this.socket = socket;
        this.connection = connection;

        resetExploredTiles(getGame().getMap());
        resetCanSeeTiles();

        connected = (connection != null);
    }


    public ServerPlayer(XMLStreamReader in) throws XMLStreamException {
        readFromServerAdditionElement(in);
    }


    
    public boolean isConnected() {
        return connected;
    }


    
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getRemainingEmigrants() {
        return remainingEmigrants;
    }
    
    public void setRemainingEmigrants(int emigrants) {
        remainingEmigrants = emigrants;
    }

    
    public void claimLand(Tile tile, Settlement settlement, int price) {
        Player owner = tile.getOwner();
        Settlement ownerSettlement = tile.getOwningSettlement();

        if (price > 0) {
            modifyGold(-price);
            owner.modifyGold(price);
        } else if (price < 0 && ownerSettlement instanceof IndianSettlement) {
            owner.modifyTension(this, Tension.TENSION_ADD_LAND_TAKEN,
                                (IndianSettlement) ownerSettlement);
        }
        tile.setOwningSettlement(settlement);
        tile.setOwner(this);
        tile.updatePlayerExploredTiles();
    }

    
    public List<FreeColObject> declareIndependence(String nationName,
                                                   String countryName) {
        ArrayList<FreeColObject> result = new ArrayList<FreeColObject>();
        
        setIndependentNationName(nationName);
        setNewLandName(countryName);
        setPlayerType(PlayerType.REBEL);
        getFeatureContainer().addAbility(new Ability("model.ability.independenceDeclared"));
        modifyScore(SCORE_INDEPENDENCE_DECLARED);
        history.add(new HistoryEvent(getGame().getTurn().getNumber(),
                                     HistoryEvent.Type.DECLARE_INDEPENDENCE));

        
        ArrayList<String> unitNames = new ArrayList<String>();
        for (Unit unit : europe.getUnitList()) {
            unitNames.add(unit.getName());
            result.add(unit);
        }
        europe.disposeUnitList();
        if (!unitNames.isEmpty()) {
            result.add(new ModelMessage(ModelMessage.MessageType.UNIT_LOST,
                                        "model.player.independence.unitsSeized", this)
                       .addName("%units%", Utils.join(", ", unitNames)));
        }

        
        java.util.Map<UnitType, UnitType> upgrades = new HashMap<UnitType, UnitType>();
        for (UnitType unitType : Specification.getSpecification().getUnitTypeList()) {
            UnitType upgrade = unitType.getUnitTypeChange(ChangeType.INDEPENDENCE, this);
            if (upgrade != null) {
                upgrades.put(unitType, upgrade);
            }
        }

        for (Colony colony : getColonies()) {
            int sol = colony.getSoL();
            if (sol > 50) {
                java.util.Map<UnitType, List<Unit>> unitMap = new HashMap<UnitType, List<Unit>>();
                List<Unit> allUnits = new ArrayList<Unit>(colony.getTile().getUnitList());
                allUnits.addAll(colony.getUnitList());
                for (Unit unit : allUnits) {
                    if (upgrades.containsKey(unit.getType())) {
                        List<Unit> unitList = unitMap.get(unit.getType());
                        if (unitList == null) {
                            unitList = new ArrayList<Unit>();
                            unitMap.put(unit.getType(), unitList);
                        }
                        unitList.add(unit);
                    }
                }
                for (Entry<UnitType, List<Unit>> entry : unitMap.entrySet()) {
                    int limit = (entry.getValue().size() + 2) * (sol - 50) / 100;
                    if (limit > 0) {
                        for (int index = 0; index < limit; index++) {
                            Unit unit = entry.getValue().get(index);
                            if (unit == null) break;
                            unit.setType(upgrades.get(entry.getKey()));
                            result.add(unit);
                        }
                        result.add(new ModelMessage(ModelMessage.MessageType.UNIT_IMPROVED,
                                                    "model.player.continentalArmyMuster",
                                                    this, colony)
                                   .addName("%colony%", colony.getName())
                                   .addAmount("%number%", limit)
                                   .addName("%oldUnit%", entry.getKey().getName())
                                   .addName("%unit%", upgrades.get(entry.getKey()).getName()));
                    }
                }
            }
        }

        
        divertModelMessages(europe, null);
        europe.dispose();
        europe = null;
        monarch = null; 

        
        result.add(this);
        return result;
    }

    
    public List<ModelMessage> giveIndependence(ServerPlayer REFplayer) {
        ArrayList<ModelMessage> messages = new ArrayList<ModelMessage>();
        setPlayerType(PlayerType.INDEPENDENT);
        modifyScore(SCORE_INDEPENDENCE_GRANTED - getGame().getTurn().getNumber());
        setTax(0);
        reinitialiseMarket();
        getHistory().add(new HistoryEvent(getGame().getTurn().getNumber(),
                                          HistoryEvent.Type.INDEPENDENCE));
        messages.add(new ModelMessage("model.player.independence", this));
        ArrayList<Unit> surrenderUnits = new ArrayList<Unit>();
        ArrayList<String> unitNames = new ArrayList<String>();
        for (Unit u : REFplayer.getUnits()) {
            if (!u.isNaval()) surrenderUnits.add(u);
        }
        for (Unit u : surrenderUnits) {
            if (u.getType().hasAbility("model.ability.refUnit")) {
                
                
                UnitType downgrade = u.getType().getUnitTypeChange(ChangeType.CAPTURE, this);
                if (downgrade != null) u.setType(downgrade);
            }
            u.setOwner(this);
            unitNames.add(u.getName());
        }
        messages.add(new ModelMessage("model.player.independence.unitsAcquired", this)
                     .addName("%units%", Utils.join(", ", unitNames)));
        return messages;
    }

    
    public ModelMessage cashInTreasureTrain(Unit unit) {
        int fullAmount = unit.getTreasureAmount();
        int cashInAmount = (fullAmount - unit.getTransportFee())
            * (100 - getTax()) / 100;

        modifyGold(cashInAmount);
        String messageId = "model.unit.cashInTreasureTrain.colonial";
        if (getPlayerType() == PlayerType.REBEL
            || getPlayerType() == PlayerType.INDEPENDENT) {
            messageId = "model.unit.cashInTreasureTrain.independent";
        }
        ModelMessage m = new ModelMessage(messageId, this, unit)
            .addAmount("%amount%", fullAmount)
            .addAmount("%cashInAmount%", cashInAmount);
        unit.dispose();
        return m;
    }

    
    public void addHistory(HistoryEvent event) {
        history.add(event);
    }

    
    public void resetExploredTiles(Map map) {
        if (map != null) {
            Iterator<Unit> unitIterator = getUnitIterator();
            while (unitIterator.hasNext()) {
                Unit unit = unitIterator.next();

                setExplored(unit.getTile());

                Iterator<Position> positionIterator;
                if (unit.getColony() != null) {
                    positionIterator = map.getCircleIterator(unit.getTile().getPosition(), true, 2);
                } else {
                    positionIterator = map.getCircleIterator(unit.getTile().getPosition(), true, unit.getLineOfSight());
                }

                while (positionIterator.hasNext()) {
                    Map.Position p = positionIterator.next();
                    setExplored(map.getTile(p));
                }
            }

        }

    }

    
    public boolean hasExplored(Tile tile) {
        return tile.isExploredBy(this);
    }


    
    public void setExplored(Tile tile) {
        tile.setExploredBy(this, true);
    }


    
    public void setExplored(Unit unit) {
        if (getGame() == null || getGame().getMap() == null || unit == null || unit.getLocation() == null || unit.getTile() == null) {
            return;
        }

        if (canSeeTiles == null) {
            resetCanSeeTiles();
        }

        setExplored(unit.getTile());
        canSeeTiles[unit.getTile().getPosition().getX()][unit.getTile().getPosition().getY()] = true;

        Iterator<Position> positionIterator = getGame().getMap().getCircleIterator(unit.getTile().getPosition(), true, unit.getLineOfSight());
        while (positionIterator.hasNext()) {
            Map.Position p = positionIterator.next();
            if (p == null) {
                continue;
            }
            setExplored(getGame().getMap().getTile(p));
            if (canSeeTiles != null) {
                canSeeTiles[p.getX()][p.getY()] = true;
            } else {
                invalidateCanSeeTiles();
            }
        }
    }
    

    
    public void revealMap() {
        Iterator<Position> positionIterator = getGame().getMap().getWholeMapIterator();

        while (positionIterator.hasNext()) {
            Map.Position p = positionIterator.next();
            setExplored(getGame().getMap().getTile(p));
        }
        
        ((BooleanOption) getGame().getGameOptions().getObject(GameOptions.FOG_OF_WAR)).setValue(false);
        
        resetCanSeeTiles();
    }


    
    public Socket getSocket() {
        return socket;
    }


    
    public Connection getConnection() {
        return connection;
    }
    
    
    
    public void setConnection(Connection connection) {
        this.connection = connection;
        connected = (connection != null);
    }
    
    public void toServerAdditionElement(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getServerAdditionXMLElementTagName());

        out.writeAttribute("ID", getId());
        
        out.writeEndElement();
    }
    
    
    
    public void updateID() {
        setId(serverID);
    }
    
    
    public void readFromServerAdditionElement(XMLStreamReader in) throws XMLStreamException {
        serverID = in.getAttributeValue(null, "ID");
        in.nextTag();
    }
    
    
    
    public static String getServerAdditionXMLElementTagName() {
        return "serverPlayer";
    }
    
    @Override
    public String toString() {
        return "ServerPlayer[name="+getName()+",serverID=" + serverID + ",conn=" + connection + "]";
    }
}
