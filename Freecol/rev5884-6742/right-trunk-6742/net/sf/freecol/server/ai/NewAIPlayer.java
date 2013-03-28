

package net.sf.freecol.server.ai;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.NetworkConstants;
import net.sf.freecol.server.ai.mission.UnitWanderHostileMission;
import net.sf.freecol.server.ai.mission.UnitWanderMission;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.networking.DummyConnection;

import org.w3c.dom.Element;


public abstract class NewAIPlayer extends AIObject {

    private static final Logger logger = Logger.getLogger(AIPlayer.class.getName());

    protected static EquipmentType muskets = FreeCol.getSpecification().getEquipmentType("model.equipment.muskets");
    protected static EquipmentType horses = FreeCol.getSpecification().getEquipmentType("model.equipment.horses");
    protected static EquipmentType toolsType = FreeCol.getSpecification().getEquipmentType("model.equipment.tools");

    
    protected HashMap<String, Integer> sessionRegister = new HashMap<String, Integer>();

    
    private ServerPlayer player;

    
    private ArrayList<AIUnit> aiUnits = new ArrayList<AIUnit>();

    
    private Connection debuggingConnection;

    public NewAIPlayer() {
        super(null);
    }

    
    public NewAIPlayer(AIMain aiMain, ServerPlayer player) {
        super(aiMain, player.getId());
        this.player = player;
    }

    
    public NewAIPlayer(AIMain aiMain, Element element) {
        super(aiMain, element.getAttribute("ID"));
        readFromXMLElement(element);
    }

    
    public NewAIPlayer(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain, in.getAttributeValue(null, "ID"));
        readFromXML(in);
    }

    
    public abstract void startWorking();

    
    protected void determineStances() {
        logger.finest("Entering method determineStances");
        Player player = getPlayer();
        for (Player p : getGame().getPlayers()) {
            if (p != player) {
                Stance stance = getPlayer().getStance(p);
                Tension tension = getPlayer().getTension(p);
                if (stance != Stance.UNCONTACTED && tension != null) {
                    if (p.getREFPlayer() == getPlayer() && p.getPlayerType() == PlayerType.REBEL) {
                        tension.modify(1000);
                    }
                    if (stance != Stance.WAR &&
                        tension.getLevel() == Tension.Level.HATEFUL) {
                        getPlayer().changeRelationWithPlayer(p, Stance.WAR);
                    } else if (stance == Stance.WAR
                               && tension.getLevel().compareTo(Tension.Level.CONTENT) <= 0) {
                        getPlayer().changeRelationWithPlayer(p, Stance.CEASE_FIRE);
                    } else if (stance == Stance.CEASE_FIRE
                               && tension.getLevel().compareTo(Tension.Level.HAPPY) <= 0) {
                        getPlayer().changeRelationWithPlayer(p, Stance.PEACE);
                    }
                }
            }
        }
    }

    
    protected void abortInvalidMissions() {
        logger.finest("Entering method abortInvalidMissions");
        Iterator<AIUnit> aiUnitsIterator = getAIUnitIterator();
        while (aiUnitsIterator.hasNext()) {
            AIUnit aiUnit = aiUnitsIterator.next();
            if (aiUnit.getMission() == null) {
                continue;
            }
            if (!aiUnit.getMission().isValid()) {
                aiUnit.setMission(null);
            }
        }
    }

    
    protected void abortInvalidAndOneTimeMissions() {
        logger.finest("Entering method abortInvalidAndOneTimeMissions");
        Iterator<AIUnit> aiUnitsIterator = getAIUnitIterator();
        while (aiUnitsIterator.hasNext()) {
            AIUnit aiUnit = aiUnitsIterator.next();
            if (aiUnit.getMission() == null) {
                continue;
            }
            if (!aiUnit.getMission().isValid() || aiUnit.getMission() instanceof UnitWanderHostileMission
                || aiUnit.getMission() instanceof UnitWanderMission
                
                
                ) {
                aiUnit.setMission(null);
            }
        }
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

    
    protected void doMissions() {
        logger.finest("Entering method doMissions");
        Iterator<AIUnit> aiUnitsIterator = getAIUnitIterator();
        while (aiUnitsIterator.hasNext()) {
            AIUnit aiUnit = aiUnitsIterator.next();
            if (aiUnit.hasMission() && aiUnit.getMission().isValid()
                && !(aiUnit.getUnit().isOnCarrier())) {
                try {
                    aiUnit.doMission(getConnection());
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.warning(sw.toString());
                }
            }
        }
    }

    
    public Unit getBestTreasureTrain(Tile tile) {
        Unit bestTreasureTrain = null;
        for (Unit unit : tile.getUnitList()) {
            if (unit.canCarryTreasure() &&
                (bestTreasureTrain == null ||
                 bestTreasureTrain.getTreasureAmount() < unit.getTreasureAmount())) {
                bestTreasureTrain = unit;
            }
        }

        return bestTreasureTrain;
    }

    int getUnitSeekAndDestroyMissionValue(Unit unit, Tile newTile, int turns) {
        logger.finest("Entering method getUnitSeekAndDestroyMissionValue");

        Unit defender = newTile.getDefendingUnit(unit);

        if(!isTargetValidForSeekAndDestroy(unit, defender)){
            return Integer.MIN_VALUE;
        }

        int value = 10020;
        CombatModel combatModel = unit.getGame().getCombatModel();

        if (getBestTreasureTrain(newTile) != null) {
            value += Math.min(getBestTreasureTrain(newTile).getTreasureAmount() / 10, 50);
        }
        if (defender.getType().getOffence() > 0 &&
            newTile.getSettlement() == null) {
            value += 200 - combatModel.getDefencePower(unit, defender) * 2 - turns * 50;
        }

        value += combatModel.getOffencePower(defender, unit) -
            combatModel.getDefencePower(defender, unit);
        value -= turns * 10;

        if (!defender.isNaval()) {
            if (defender.hasAbility("model.ability.expertSoldier")
                && !defender.isArmed()) {
                value += 10 - combatModel.getDefencePower(unit, defender) * 2 - turns * 25;
            }
            if (newTile.getSettlement() != null) {
                value += 300;
                Iterator<Unit> dp = newTile.getUnitIterator();
                while (dp.hasNext()) {
                    Unit u = dp.next();
                    if (u.isDefensiveUnit()) {
                        if (combatModel.getDefencePower(unit, u) > combatModel.getOffencePower(unit, u)) {
                            value -= 100 * (combatModel.getDefencePower(unit, u) - combatModel.getOffencePower(unit, u));
                        } else {
                            value -= combatModel.getDefencePower(unit, u);
                        }
                    }
                }
            }
        }
        return Math.max(0, value);
    }

    boolean isTargetValidForSeekAndDestroy(Unit attacker, Unit defender){    	
    	
    	if(defender == null){
            return false;
    	}
    	
    	
    	boolean attackerInLand = true;
    	if(attacker.getTile() != null)
            attackerInLand = attacker.getTile().isLand();
    	
    	boolean defenderInLand = true;
    	if(defender.getTile() != null)
            defenderInLand = defender.getTile().isLand();
    		
    	
        if(attackerInLand != defenderInLand){
            return false;
        }

        
        if(attacker.isNaval() != defender.isNaval()){
            return false;
        }

    	Player attackerPlayer = attacker.getOwner();
    	Player defenderPlayer = defender.getOwner();

        
        if(attackerPlayer == defenderPlayer){
            return false;
        }

        boolean notAtWar = attackerPlayer.getStance(defenderPlayer) != Stance.WAR;
        
        if(attackerPlayer.isEuropean() && notAtWar){
            return false;
        }

        
        if(attackerPlayer.isIndian()){
            boolean inFriendlyMood = attackerPlayer.getTension(defenderPlayer).getLevel().compareTo(Tension.Level.CONTENT) >= 0;
        	
            if(notAtWar && inFriendlyMood)
            	return false;
        }

        return true;
    }

    
    public Iterator<AIUnit> getAIUnitIterator() {
        if (aiUnits.size() == 0) {
            ArrayList<AIUnit> au = new ArrayList<AIUnit>();
            Iterator<Unit> unitsIterator = player.getUnitIterator();
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

    
    public Player getPlayer() {
        return player;
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

    
    @Override
    public String getId() {
        return player.getId();
    }

    
    @Override
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute("ID", getId());
        out.writeEndElement();
    }

    
    @Override
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        player = (ServerPlayer) getAIMain().getFreeColGameObject(in.getAttributeValue(null, "ID"));
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "aiPlayer";
    }


    
    public void registerSellGoods(Goods goods) {
        String goldKey = "tradeGold#" + goods.getType().getIndex() + "#" + goods.getAmount()
            + "#" + goods.getLocation().getId();
        sessionRegister.put(goldKey, null);
    }

    
    public int buyProposition(Unit unit, Settlement settlement, Goods goods, int gold) {
        logger.finest("Entering method buyProposition");
        String goldKey = "tradeGold#" + goods.getType().getIndex() + "#" + goods.getAmount()
            + "#" + settlement.getId();
        String hagglingKey = "tradeHaggling#" + unit.getId();

        Integer registered = sessionRegister.get(goldKey);
        if (registered == null) {
            int price = ((IndianSettlement) settlement).getPriceToSell(goods)
                + player.getTension(unit.getOwner()).getValue();
            sessionRegister.put(goldKey, new Integer(price));
            return price;
        } else {
            int price = registered.intValue();
            if (price < 0 || price == gold) {
                return price;
            } else if (gold < (price * 9) / 10) {
                logger.warning("Cheating attempt: sending a offer too low");
                sessionRegister.put(goldKey, new Integer(-1));
                return NetworkConstants.NO_TRADE;
            } else {
                int haggling = 1;
                if (sessionRegister.containsKey(hagglingKey)) {
                    haggling = sessionRegister.get(hagglingKey).intValue();
                }
                if (getRandom().nextInt(3 + haggling) <= 3) {
                    sessionRegister.put(goldKey, new Integer(gold));
                    sessionRegister.put(hagglingKey, new Integer(haggling + 1));
                    return gold;
                } else {
                    sessionRegister.put(goldKey, new Integer(-1));
                    return NetworkConstants.NO_TRADE;
                }
            }
        }
    }

    protected void clearAIUnits() {
        aiUnits.clear();
    }
}
