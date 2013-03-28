


package net.sf.freecol.server.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

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
import net.sf.freecol.common.model.Location;
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
    
    private static final Logger logger = Logger.getLogger(ServerPlayer.class.getName());

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

    
    public int getTensionModifier(Player player, Stance newStance) {
        Stance oldStance = getStance(player);
        int modifier = 0;
        switch (newStance) {
        case UNCONTACTED:
            throw new IllegalStateException("Can not set UNCONTACTED stance");
        case ALLIANCE: case PEACE:
            switch (oldStance) {
            case UNCONTACTED: case ALLIANCE: case PEACE:
                break;
            case CEASE_FIRE:
                modifier = Tension.PEACE_TREATY_MODIFIER;
                break;
            case WAR:
                modifier = Tension.CEASE_FIRE_MODIFIER
                    + Tension.PEACE_TREATY_MODIFIER;
                break;
            default:
                throw new IllegalStateException("Bogus oldStance");
            }
            break;
        case CEASE_FIRE:
            switch (oldStance) {
            case UNCONTACTED: case ALLIANCE: case PEACE: case CEASE_FIRE:
                throw new IllegalStateException("Can only set CEASE_FIRE from WAR");
            case WAR:
                modifier = Tension.CEASE_FIRE_MODIFIER;
                break;
            default:
                throw new IllegalStateException("Bogus oldStance");
            }
            break;
        case WAR:
            switch (oldStance) {
            case UNCONTACTED: case ALLIANCE: case PEACE:
                modifier = Tension.TENSION_ADD_DECLARE_WAR_FROM_PEACE;
                break;
            case CEASE_FIRE:
                modifier = Tension.TENSION_ADD_DECLARE_WAR_FROM_CEASE_FIRE;
                break;
            case WAR: default:
                throw new IllegalStateException("Bogus oldStance");
            }
            break;
        default:
            throw new IllegalStateException("Bogus newStance");
        }
        return modifier;
    }

    
    public boolean checkForDeath() {
        

        switch (getPlayerType()) {
        case NATIVE: 
            return getUnits().isEmpty();

        case COLONIAL: 
            break;

        case REBEL: case INDEPENDENT:
            
            
            for (Colony colony : getColonies()) {
                if (colony.isConnected()) return false;
            }
            return true;

        case ROYAL: 
            Iterator<Player> players = getGame().getPlayerIterator();
            while (players.hasNext()) {
                Player enemy = players.next();
                if (enemy.getREFPlayer() == (Player) this
                    && enemy.getPlayerType() == PlayerType.REBEL) {
                    return false;
                }
            }

            
            Iterator<Unit> units = getUnitIterator();
            while (units.hasNext()) {
                if (!units.next().isInEurope()) {
                    return false;
                }
            }

            
            return true;

        case UNDEAD:
            return getUnits().isEmpty();

        default:
            throw new IllegalStateException("Bogus player type");
        }

        
        if (!getColonies().isEmpty()) {
            return false;
        }

        
        boolean hasCarrier = false;
        List<Unit> unitList = getUnits();
        for(Unit unit : unitList){
            boolean isValidUnit = false;

            if(unit.isCarrier()){
                hasCarrier = true;
                continue;
            }

            
            if(unit.isColonist()){
                isValidUnit = true;
            }

            
            if(unit.isOffensiveUnit()){
                isValidUnit = true;
            }

            if(!isValidUnit){
                continue;
            }

            
            Location unitLocation = unit.getLocation();
            
            if(unitLocation instanceof Tile){
                logger.info(getName() + " found colonist in new world");
                return false;
            }
            
            if(unit.isOnCarrier()){
                Unit carrier = (Unit) unitLocation;
                
                if(carrier.getLocation() instanceof Tile){
                    logger.info(getName() + " found colonist aboard carrier in new world");
                    return false;
                }
            }
        }
        

        
        if (getGame().getTurn().getYear() >= 1600) {
            logger.info(getName() + " no presence in new world after 1600");
            return true;
        }

        int goldNeeded = 0;
        
        if(!hasCarrier){
            

            Iterator<UnitType> navalUnits = FreeCol.getSpecification().getUnitTypesWithAbility("model.ability.navalUnit").iterator();

            int lowerPrice = Integer.MAX_VALUE;

            while(navalUnits.hasNext()){
                UnitType unit = navalUnits.next();

                int unitPrice = getEurope().getUnitPrice(unit);

                
                if(unitPrice == UnitType.UNDEFINED){
                    continue;
                }

                if(unitPrice < lowerPrice){
                    lowerPrice = unitPrice;
                }
            }

            
            if(lowerPrice == Integer.MAX_VALUE){
                logger.warning(getName() + " could not find naval unit to buy");
                return true;
            }

            goldNeeded += lowerPrice;

            
            if(goldNeeded > getGold()){
                logger.info(getName() + " does not have enough money to buy carrier");
                return true;
            }
            logger.info(getName() + " has enough money to buy carrier, has=" + getGold() + ", needs=" + lowerPrice);
        }

        
        Iterator<Unit> unitIterator = getEurope().getUnitIterator();
        while (unitIterator.hasNext()) {
            Unit unit = unitIterator.next();
            if (unit.isCarrier()) {
                
                for(Unit u : unit.getUnitList()){
                    if(u.isColonist()){
                        return false;
                    }
                }

                
                if(unit.getGoodsCount() > 0){
                    logger.info(getName() + " has goods to sell");
                    return false;
                }
                continue;
            }
            if (unit.isColonist()){
                logger.info(getName() + " has colonist unit waiting in port");
                return false;
            }
        }

        
        int goldToRecruit =  getEurope().getRecruitPrice();

        

        Iterator<UnitType> trainedUnits = FreeCol.getSpecification().getUnitTypesTrainedInEurope().iterator();

        int goldToTrain = Integer.MAX_VALUE;

        while(trainedUnits.hasNext()){
            UnitType unit = trainedUnits.next();

            if(!unit.hasAbility("model.ability.foundColony")){
                continue;
            }

            int unitPrice = getEurope().getUnitPrice(unit);

            
            if(unitPrice == UnitType.UNDEFINED){
                continue;
            }

            if(unitPrice < goldToTrain){
                goldToTrain = unitPrice;
            }
        }

        goldNeeded += Math.min(goldToTrain, goldToRecruit);

        if (goldNeeded <= getGold()) return false;
        
        logger.info(getName() + " does not have enough money for recruiting or training");
        return true;
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
