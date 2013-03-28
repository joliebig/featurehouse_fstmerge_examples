


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
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FreeColGameObject;
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
import net.sf.freecol.common.model.StringTemplate;
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
    
    public static final int SCORE_INDEPENDENCE_GRANTED = 1000;

    
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

    
    public List<Object> severEurope() {
        List<Object> objects = new ArrayList<Object>();
        objects.addAll(europe.disposeList());
        europe = null;
        objects.add(monarch);
        monarch = null;
        return objects;
    }

    
    public List<ModelMessage> giveIndependence(ServerPlayer REFplayer) {
        ArrayList<ModelMessage> messages = new ArrayList<ModelMessage>();
        setPlayerType(PlayerType.INDEPENDENT);
        modifyScore(SCORE_INDEPENDENCE_GRANTED - getGame().getTurn().getNumber());
        setTax(0);
        reinitialiseMarket();
        getHistory().add(new HistoryEvent(getGame().getTurn().getNumber(),
                                          HistoryEvent.EventType.INDEPENDENCE));
        messages.add(new ModelMessage("model.player.independence", this));
        ArrayList<Unit> surrenderUnits = new ArrayList<Unit>();
        for (Unit u : REFplayer.getUnits()) {
            if (!u.isNaval()) surrenderUnits.add(u);
        }
        StringTemplate surrender = StringTemplate.label(", ");
        for (Unit u : surrenderUnits) {
            if (u.getType().hasAbility("model.ability.refUnit")) {
                
                
                UnitType downgrade = u.getType().getUnitTypeChange(ChangeType.CAPTURE, this);
                if (downgrade != null) u.setType(downgrade);
            }
            u.setOwner(this);
            surrender.addStringTemplate(u.getLabel());
        }
        messages.add(new ModelMessage("model.player.independence.unitsAcquired", this)
                     .addStringTemplate("%units%", surrender));
        return messages;
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
