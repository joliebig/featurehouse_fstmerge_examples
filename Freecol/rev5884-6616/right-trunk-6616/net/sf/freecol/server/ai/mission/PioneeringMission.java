


package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileItemContainer;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIColony;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.server.ai.AIUnit;
import net.sf.freecol.server.ai.TileImprovementPlan;

import org.w3c.dom.Element;



public class PioneeringMission extends Mission {
    
    
    private static final Logger logger = Logger.getLogger(PioneeringMission.class.getName());

    private static final EquipmentType toolsType = FreeCol.getSpecification().getEquipmentType("model.equipment.tools");

    private static enum PioneeringMissionState {GET_TOOLS,IMPROVING};
    
    private PioneeringMissionState state = PioneeringMissionState.GET_TOOLS;
    
    private TileImprovementPlan tileImprovementPlan = null;
    
    private Colony colonyWithTools = null;

    private boolean invalidateMission = false;

    
    public PioneeringMission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain, aiUnit);
        
        boolean hasTools = getUnit().hasAbility("model.ability.improveTerrain");
        if(hasTools){
            state = PioneeringMissionState.IMPROVING;
        }
        else{
            state = PioneeringMissionState.GET_TOOLS;
        }
    }


    
    public PioneeringMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public PioneeringMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }
    
    
    
    public void dispose() {
        if (tileImprovementPlan != null) {
            tileImprovementPlan.setPioneer(null);
            tileImprovementPlan = null;
        }
        super.dispose();
    }

    
    public void setTileImprovementPlan(TileImprovementPlan tileImprovementPlan) {
        this.tileImprovementPlan = tileImprovementPlan;
    }

    private void updateTileImprovementPlan() {
        final AIPlayer aiPlayer = (AIPlayer) getAIMain().getAIObject(getUnit().getOwner().getId());
        final Unit carrier = (getUnit().isOnCarrier()) ? (Unit) getUnit().getLocation() : null;
        
        Tile improvementTarget = (tileImprovementPlan != null)? tileImprovementPlan.getTarget():null;
        
        if (tileImprovementPlan != null && improvementTarget == null) {
            logger.finest("Found invalid TileImprovementPlan, removing it and assigning a new one");
            aiPlayer.removeTileImprovementPlan(tileImprovementPlan);
            tileImprovementPlan.dispose();
            tileImprovementPlan = null;
        }
        
        
        
        if (tileImprovementPlan != null &&
            improvementTarget != null &&
            improvementTarget.hasImprovement(tileImprovementPlan.getType())){
            aiPlayer.removeTileImprovementPlan(tileImprovementPlan);
            tileImprovementPlan.dispose();
            tileImprovementPlan = null;
        }
        
        
        if (tileImprovementPlan != null && improvementTarget != null) {
            return;
        }
        
        final Tile startTile;
        if (getUnit().getTile() == null) {
            if (getUnit().isOnCarrier()) {
                startTile = (Tile) ((Unit) getUnit().getLocation()).getEntryLocation();
            } else {
                startTile = (Tile) getUnit().getOwner().getEntryLocation();
            }
        } else {
            startTile = getUnit().getTile();
        }
                
        TileImprovementPlan bestChoice = null;
        int bestValue = 0;
        Iterator<TileImprovementPlan> tiIterator = aiPlayer.getTileImprovementPlanIterator();            
        while (tiIterator.hasNext()) {
            TileImprovementPlan ti = tiIterator.next();
            if (ti.getPioneer() == null) {
                
                if (ti.getTarget() == null) {
                    logger.finest("Found invalid TileImprovementPlan, removing it and finding a new one");
                    aiPlayer.removeTileImprovementPlan(ti);
                    ti.dispose();
                    continue;
                }
                
                PathNode path = null;
                int value;
                if (startTile != ti.getTarget()) {
                    path = getGame().getMap().findPath(getUnit(), startTile, ti.getTarget(), carrier);
                    if (path != null) {
                        value = ti.getValue() + 10000 - (path.getTotalTurns()*5);
                        
                        
                        PathNode pn = path;
                        while (pn != null) {
                            if (pn.getTile().getFirstUnit() != null
                                && pn.getTile().getFirstUnit().getOwner() != getUnit().getOwner()) {
                                value -= 1000;
                            }
                            pn = pn.next;
                        }
                    } else {
                        value = ti.getValue();
                    }
                } else {
                    value = ti.getValue() + 10000;
                }                
                if (value > bestValue) {
                    bestChoice = ti;
                    bestValue = value;
                }
            }
        }
        
        if (bestChoice != null) {
            tileImprovementPlan = bestChoice;
            bestChoice.setPioneer(getAIUnit());
        }
        
        if(tileImprovementPlan == null){
            invalidateMission = true;
        }
    }
    
    
    
    public void doMission(Connection connection) {
        logger.finest("Entering PioneeringMission.doMission()");
        
        Unit unit = getUnit();
        
        boolean hasTools = getUnit().hasAbility("model.ability.improveTerrain");
        if(unit.getState() == UnitState.IMPROVING || hasTools){
            state = PioneeringMissionState.IMPROVING;
        }
        else{
            state = PioneeringMissionState.GET_TOOLS;
        }
        
        while(isValid() && unit.getMovesLeft() > 0){
            switch(state){
                case GET_TOOLS:
                    getTools(connection);
                    break;
                case IMPROVING:
                    processImprovementPlan(connection);
                    break;
                default:
                    logger.warning("Unknown state");
                    invalidateMission = true;
            }        
        }
    }

    private void processImprovementPlan(Connection connection) {
        if (tileImprovementPlan == null) {
            updateTileImprovementPlan();
            if (tileImprovementPlan == null) {
                invalidateMission = true;
                return;
            }
        }

        Unit unit = getUnit();
        
        if (unit.getTile() == null) {
            logger.warning("Unit is in unknown location, cannot proceed with mission");
            invalidateMission = true;
            return;
        }
                
        
        if (getUnit().getTile() != tileImprovementPlan.getTarget()) {
            PathNode pathToTarget = getUnit().findPath(tileImprovementPlan.getTarget());
            if (pathToTarget == null) {
                invalidateMission = true;
                return; 
            }

            Direction direction = moveTowards(connection, pathToTarget);
            if (direction != null
                    && unit.getMoveType(direction).isProgress()) {
                move(connection, direction);
            }
            
            if(unit.getTile() != tileImprovementPlan.getTarget()){
                unit.setMovesLeft(0);
            }
            if(unit.getMovesLeft() == 0){
                return;
            }
        }

        
        if (unit.getTile() != tileImprovementPlan.getTarget()){
            String errMsg = "Something is wrong, pioneer should be on the tile to improve, but isnt";
            logger.warning(errMsg);
            invalidateMission = true;
            return;
        }            
        makeImprovement(connection);
    }

    private void makeImprovement(Connection connection) {
        if(getUnit().getState() == UnitState.IMPROVING){
            getUnit().setMovesLeft(0);
            return;
        }
        
        if (getUnit().checkSetState(UnitState.IMPROVING)) {
            
            int price = getUnit().getOwner().getLandPrice(getUnit().getTile());
            
            if (price > 0) {
                
            }
            
            Element changeWorkTypeElement = Message.createNewRootElement("workImprovement");
            changeWorkTypeElement.setAttribute("unit", getUnit().getId());
            changeWorkTypeElement.setAttribute("improvementType", tileImprovementPlan.getType().getId());
            Element reply = null;
            try {
                reply = connection.ask(changeWorkTypeElement);
            } catch (IOException e) {
                logger.warning("Could not send message!");
            }
            if (reply==null || !reply.getTagName().equals("workImprovementConfirmed")) {
                throw new IllegalStateException("Failed to make improvement");
            }
            
            
            Element containerElement = (Element)reply.getElementsByTagName(TileItemContainer.getXMLElementTagName()).item(0);
            if (containerElement != null) {
                TileItemContainer container = (TileItemContainer) getGame().getFreeColGameObject(containerElement.getAttribute("ID"));
                if (container == null) {
                    container = new TileItemContainer(getGame(), getUnit().getTile(), containerElement);
                    getUnit().getTile().setTileItemContainer(container);
                } else {
                    container.readFromXMLElement(containerElement);
                }
            }
            
            
            Element improvementElement = (Element)reply.getElementsByTagName(TileImprovement.getXMLElementTagName()).item(0);
            if (improvementElement==null) {
                throw new IllegalStateException("Failed to make improvement");
            }
 
            TileImprovement improvement = (TileImprovement) getGame().getFreeColGameObject(improvementElement.getAttribute("ID"));
            if (improvement == null) {
                improvement = new TileImprovement(getGame(), improvementElement);
                getUnit().getTile().add(improvement);
            } else {
                improvement.readFromXMLElement(improvementElement);
            }
            getUnit().work(improvement);
        }
    }

    private void getTools(Connection connection) {
        validateColonyWithTools();
        if(invalidateMission){
            return;
        }
          
        Unit unit = getUnit();
        
        
        if(unit.getTile() != colonyWithTools.getTile()){
            PathNode path = getGame().getMap().findPath(unit, unit.getTile(), colonyWithTools.getTile());         

            if(path == null){
                invalidateMission = true;
                colonyWithTools = null;
                return;
            }

            Direction direction = moveTowards(connection, path);
            moveButDontAttack(connection, direction);

            
            if(unit.getTile() != colonyWithTools.getTile()){
                unit.setMovesLeft(0);
                return;
            }
        }
        
        equipUnitWithTools(connection);
    }


    private void equipUnitWithTools(Connection connection) {
        Unit unit = getUnit();
        logger.finest("About to equip " + unit + " in " + colonyWithTools.getName());
        AIColony ac = (AIColony) getAIMain().getAIObject(colonyWithTools);
        int amount = toolsType.getMaximumCount();
        for (AbstractGoods materials : toolsType.getGoodsRequired()) {
            int availableAmount = ac.getAvailableGoods(materials.getType());
            int requiredAmount = materials.getAmount();
            if (availableAmount < requiredAmount) {
                invalidateMission = true;
                return;
            } 
            amount = Math.min(amount, availableAmount / requiredAmount);
        }

        logger.finest("Equipping " + unit + " at=" + colonyWithTools.getName() + " amount=" + amount);
        Element equipUnitElement = Message.createNewRootElement("equipUnit");
        equipUnitElement.setAttribute("unit", unit.getId());
        equipUnitElement.setAttribute("type", toolsType.getId());
        equipUnitElement.setAttribute("amount", Integer.toString(amount));
        try {
            connection.sendAndWait(equipUnitElement);
        } catch (Exception e) {
            logger.warning("Could not send equip message.");
        }
        
        
        if(unit.getEquipmentCount(toolsType) > 0){
            state = PioneeringMissionState.IMPROVING;
        }
    }


    private boolean validateColonyWithTools() {
        if(colonyWithTools != null){
            if(colonyWithTools.isDisposed() 
                    || colonyWithTools.getOwner() != getUnit().getOwner()
                    || !colonyWithTools.canBuildEquipment(toolsType)){
                colonyWithTools = null;
            }
        }
        if(colonyWithTools == null){
            
            colonyWithTools = findColonyWithTools(getAIUnit());
            if(colonyWithTools == null){
                logger.finest("No tools found");
                invalidateMission = true;
                return false;
            }
            logger.finest("Colony found=" + colonyWithTools.getName());            
        }
        return true;
    }


        
    public Tile getTransportDestination() {
        updateTileImprovementPlan();
        if (tileImprovementPlan == null) {
            return null;
        }
        if (getUnit().isOnCarrier()) {
            return tileImprovementPlan.getTarget();
        } else if (getUnit().getTile() == tileImprovementPlan.getTarget()) {
            return null;
        } else if (getUnit().getTile() == null || getUnit().findPath(tileImprovementPlan.getTarget()) == null) {
            return tileImprovementPlan.getTarget();
        } else {
            return null;
        }
    }

    
    public int getTransportPriority() {
        if (getTransportDestination() != null) {
            return NORMAL_TRANSPORT_PRIORITY;
        } else {
            return 0;
        }
    }

    
    public boolean isValid() {
        if(getUnit().getTile() == null){
            return false;
        }
        
        switch(state){
            case GET_TOOLS:
                validateColonyWithTools();
                break;
            case IMPROVING:
                updateTileImprovementPlan();
                break;
        }        
        return !invalidateMission;
    }

        
    public static boolean isValid(AIUnit aiUnit) {
        if(!aiUnit.getUnit().isColonist()){
            return false;
        }
        
        if(aiUnit.getUnit().getTile() == null){
            return false;
        }
        
        AIPlayer aiPlayer = (AIPlayer) aiUnit.getAIMain().getAIObject(aiUnit.getUnit().getOwner().getId());
        Iterator<TileImprovementPlan> tiIterator = aiPlayer.getTileImprovementPlanIterator();            
        
        
        boolean foundImprovementPlan = false;
        while (tiIterator.hasNext()) {
            TileImprovementPlan ti = tiIterator.next();
            if (ti.getPioneer() == null) {
                foundImprovementPlan = true;
            }
        }
        if(!foundImprovementPlan){
            logger.finest("No Improvement plan found, PioneeringMission not valid");
            return false;
        }
        
        boolean unitHasToolsAvail = aiUnit.getUnit().getEquipmentCount(toolsType) > 0;
        if(unitHasToolsAvail){
            logger.finest("Tools equipped, PioneeringMission valid");
           return true; 
        }
        
        
        Colony colonyWithTools = findColonyWithTools(aiUnit);
        if(colonyWithTools != null){
            logger.finest("Tools found, PioneeringMission valid");
            return true;
        }
        
        logger.finest("Tools not found, PioneeringMission not valid");
        return false;
    }
    
    public static Colony findColonyWithTools(AIUnit aiu) {
        final int MAX_TURN_DISTANCE = 10;
        Colony best = null;
        int bestValue = Integer.MIN_VALUE;
        
        Unit unit = aiu.getUnit();
        
        if(unit == null){
            return null;
        }
        
        for(Colony colony : unit.getOwner().getColonies()){
            if(!colony.canBuildEquipment(toolsType)) {
                continue;
            }

            AIColony ac = (AIColony) aiu.getAIMain().getAIObject(colony);
            
            if(ac == null){
                continue;
            }
            
            
            PathNode pathNode = null;
            if(unit.getTile() != colony.getTile()){
                pathNode = aiu.getGame().getMap().findPath(unit, unit.getTile(), colony.getTile());
                
                if(pathNode == null){
                    continue;
                }
                
                if(pathNode.getTotalTurns() > MAX_TURN_DISTANCE){
                    continue;
                }
            }
            
            int value = 100;
            
            for(AbstractGoods goods : toolsType.getGoodsRequired()){
                value += colony.getGoodsCount(goods.getType());
            }
            
            if(pathNode != null){
                value -= pathNode.getTotalTurns() * 10;
            }
                
            if(best == null || value > bestValue){
                best = colony;
                bestValue = value;
            }
        }        
        return best;
    }
    
    public static List<AIUnit>getPlayerPioneers(AIPlayer aiPlayer){
        List<AIUnit> list = new ArrayList<AIUnit>();
        
        AIMain aiMain = aiPlayer.getAIMain();
        for(Unit u : aiPlayer.getPlayer().getUnits()){
            AIUnit aiu =  (AIUnit) aiMain.getAIObject(u);
            if(aiu == null){
                continue;
            }
            if(aiu.getMission() instanceof PioneeringMission){
                list.add(aiu);
            }
        }
        return list;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        
        out.writeAttribute("unit", getUnit().getId());
        if (tileImprovementPlan != null) {
            out.writeAttribute("tileImprovementPlan", tileImprovementPlan.getId());
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));
        
        final String tileImprovementPlanStr = in.getAttributeValue(null, "tileImprovementPlan");
        if (tileImprovementPlanStr != null) {
            tileImprovementPlan = (TileImprovementPlan) getAIMain().getAIObject(tileImprovementPlanStr);
            if (tileImprovementPlan == null) {
                tileImprovementPlan = new TileImprovementPlan(getAIMain(), tileImprovementPlanStr);
            }
        } else {
            tileImprovementPlan = null;
        }
        
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "tileImprovementPlanMission";
    }
    
    
    public String getDebuggingInfo() {
        switch(state){
            case IMPROVING:
                if(tileImprovementPlan == null){
                    return "No target";
                }
                final String action = tileImprovementPlan.getType().getNameKey();
                return tileImprovementPlan.getTarget().getPosition().toString() + " " + action;
            case GET_TOOLS:
                if (colonyWithTools == null) {
                    return "No target";
                }
                return "Getting tools from " + colonyWithTools.getName();
            default:
                logger.warning("Unknown state");
                return "";
        }
    }
}
