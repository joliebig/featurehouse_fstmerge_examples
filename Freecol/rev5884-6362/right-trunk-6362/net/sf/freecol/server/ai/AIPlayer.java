

package net.sf.freecol.server.ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.DiplomaticTrade;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.GiveIndependenceMessage;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.NetworkConstants;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.networking.DummyConnection;

import org.w3c.dom.Element;


public abstract class AIPlayer extends AIObject {
    private static final Logger logger = Logger.getLogger(AIPlayer.class.getName());

    
    private ServerPlayer player;

    
    private Connection debuggingConnection;

    
    private ArrayList<AIUnit> aiUnits = new ArrayList<AIUnit>();

    public AIPlayer(AIMain aiMain, String id) {
        super(aiMain, id);
    }

    
    public Player getPlayer() {
        return player;
    }

                  
    protected void setPlayer(ServerPlayer p) {
        player = p;
    }

             
    protected void clearAIUnits() {
        aiUnits.clear();    
    }

    
    protected Iterator<AIUnit> getAIUnitIterator() {
        if (aiUnits.size() == 0) {
            ArrayList<AIUnit> au = new ArrayList<AIUnit>();
            Iterator<Unit> unitsIterator = getPlayer().getUnitIterator();
            while (unitsIterator.hasNext()) {
                Unit theUnit = unitsIterator.next();
                AIUnit a = (AIUnit) getAIMain().getAIObject(theUnit.getId());
                if (a != null) {
                    au.add(a);
                } else {
                    logger.warning("Could not find the AIUnit for: " + theUnit + " (" + theUnit.getId() + ") - "
                            + (getGame().getFreeColGameObject(theUnit.getId()) != null));
                }
            }
            aiUnits = au;
        }
        return aiUnits.iterator();
    }

    
    protected Iterator<AIColony> getAIColonyIterator() {
        ArrayList<AIColony> ac = new ArrayList<AIColony>();
        for (Colony colony : getPlayer().getColonies()) {
            AIColony a = (AIColony) getAIMain().getAIObject(colony.getId());
            if (a != null) {
                ac.add(a);
            } else {
                logger.warning("Could not find the AIColony for: " + colony);
            }
        }
        return ac.iterator();
    }

    
    public Connection getConnection() {
        if (debuggingConnection != null) {
            return debuggingConnection;
        } else {
            return ((DummyConnection) player.getConnection()).getOtherConnection();
        }
    }

    
    public void setDebuggingConnection(Connection debuggingConnection) {
        this.debuggingConnection = debuggingConnection;
    }

    
    protected void sendAndWaitSafely(Element element) {
        logger.finest("Entering method sendAndWaitSafely");
        try {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("AI player (" + this + ") sending " + element.getTagName() + "...");
            }
            getConnection().sendAndWait(element);
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Sent and waited, returning.");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Couldn't send AI element " + element.getTagName() + "!", e);
        }
    }

    
    protected void sendUpdatedTilesToAll(ArrayList<Tile> tiles) {
        Iterator<Player> enemyPlayerIterator = getGame().getPlayerIterator();
        while (enemyPlayerIterator.hasNext()) {
            ServerPlayer enemyPlayer = (ServerPlayer) enemyPlayerIterator.next();
            if (equals(enemyPlayer) || enemyPlayer.getConnection() == null) {
                continue;
            }
            try {
                Element updateElement = Message.createNewRootElement("update");
                boolean send = false;
                for(Tile tile : tiles) {
                    if (enemyPlayer.canSee(tile)) {
                        updateElement.appendChild(tile.toXMLElement(enemyPlayer, updateElement.getOwnerDocument()));
                        send = true;
                    }
                }
                if (send) {
                    enemyPlayer.getConnection().send(updateElement);
                }
            } catch (IOException e) {
                logger.warning("Could not send message to: " + enemyPlayer.getName() + " with connection "
                        + enemyPlayer.getConnection());
            }
        }
    }

    
    @Override
    public String getId() {
        return player.getId();
    }

    
    public AIUnit trainAIUnitInEurope(UnitType unitType) {
        
        if (unitType==null) {
            throw new IllegalArgumentException("Invalid UnitType.");
        }
        
        AIUnit unit = null;
        try {
            Element trainUnitInEuropeElement = Message.createNewRootElement("trainUnitInEurope");
            trainUnitInEuropeElement.setAttribute("unitType", unitType.getId());
            Element reply = this.getConnection().ask(trainUnitInEuropeElement);
            if (reply!=null && reply.getTagName().equals("trainUnitInEuropeConfirmed")) {
                Element unitElement = (Element) reply.getChildNodes().item(0);
                String unitID = unitElement.getAttribute("ID");
                unit = (AIUnit) getAIMain().getAIObject(unitID);
                if (unit==null) {
                    logger.warning("Could not train the specified AI unit "+unitType.getId()+" in europe.");
                }
            } else {
                logger.warning("Could not train the specified AI unit "+unitType.getId()+" in europe.");
            }
        } catch (IOException e) {
            logger.warning("Could not send \"trainUnitInEurope\"-message to the server.");
        }
        return unit;
    }
    
    
    public AIUnit recruitAIUnitInEurope(int slot) {
        
        AIUnit unit = null;
        Element recruitUnitInEuropeElement = Message.createNewRootElement("recruitUnitInEurope");
        recruitUnitInEuropeElement.setAttribute("slot", Integer.toString(slot));
        try {
            Element reply = this.getConnection().ask(recruitUnitInEuropeElement);
            if (reply!=null && reply.getTagName().equals("recruitUnitInEuropeConfirmed")) {
                Element unitElement = (Element) reply.getChildNodes().item(0);
                String unitID = unitElement.getAttribute("ID");
                unit = (AIUnit) getAIMain().getAIObject(unitID);
                if (unit==null) {
                    logger.warning("Could not recruit the specified AI unit in europe");
                }
                return unit;
            } else {
                logger.warning("Could not recruit the specified AI unit in europe.");
            }
        } catch (IOException e) {
            logger.warning("Could not send \"recruitUnitInEurope\"-message to the server.");
        }
        return unit;
    }

    
    protected boolean checkForREFDefeat() {
        logger.finest("Entering method checkForREFDefeat");
        if (!getPlayer().isREF()) {
        	throw new IllegalStateException("Checking for REF player defeat when player not REF.");
        }
        
        List<Player> dominions = getPlayer().getDominionsAtWar();
        
        
        
        if (dominions.isEmpty()) {
            return false;
        }
        
        if (!getPlayer().getSettlements().isEmpty()) {
            return false;
        }
        
        if (getPlayer().hasManOfWar() && getPlayer().getNumberOfKingLandUnits() > 6) {
            return false;
        }
        
        for (Player p : dominions) {
            sendAndWaitSafely(new GiveIndependenceMessage(p).toXMLElement());
        }
        return true;
    }







    
    public abstract void startWorking();

    
    public abstract Iterator<TileImprovementPlan> getTileImprovementPlanIterator();
    
    
    public abstract void removeTileImprovementPlan(TileImprovementPlan plan);
    
    
    public abstract boolean hasFewColonies();
    
    
    public abstract Iterator<Wish> getWishIterator();
    
    
    public abstract FoundingFather selectFoundingFather(List<FoundingFather> foundingFathers);
    
    
    public abstract boolean acceptTax(int tax);
    
    
    public abstract boolean acceptIndianDemand(Unit unit, Colony colony, Goods goods, int gold);
    
    
    public abstract boolean acceptMercenaryOffer();
    
    public abstract boolean acceptDiplomaticTrade(DiplomaticTrade agreement);
    
    
    public abstract void registerSellGoods(Goods goods);
    
    
    public abstract int buyProposition(Unit unit, Goods goods, int gold);

    
    public abstract int tradeProposition(Unit unit, Settlement settlement, Goods goods, int gold);
    
    
    @Override
    protected abstract void toXMLImpl(XMLStreamWriter out) throws XMLStreamException;
    
    
    @Override
    protected abstract void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException;
    
    
    public static String getXMLElementTagName() {
        return "aiPlayer";
    }

}
