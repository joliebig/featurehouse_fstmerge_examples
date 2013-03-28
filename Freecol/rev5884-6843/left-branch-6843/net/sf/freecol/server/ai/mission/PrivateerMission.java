


package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.CombatModel;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.model.Unit.MoveType;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.DisembarkMessage;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;


public class PrivateerMission extends Mission {
    private static final Logger logger = Logger.getLogger(PrivateerMission.class.getName());

	private static enum PrivateerMissionState {HUNTING,TRANSPORTING};
	private PrivateerMissionState state = PrivateerMissionState.HUNTING;
	private Location nearestPort = null;
	private Tile target = null;
	private boolean invalidateMission = false;
	
	
    
    public PrivateerMission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain, aiUnit);
        Unit unit = aiUnit.getUnit();
        logger.finest("Assigning PrivateerMission to unit=" + unit + " at " + unit.getTile());
    }


    
    public PrivateerMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }
    
    
    public PrivateerMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }


    
    public void doMission(Connection connection) {
    	logger.finest("Entering doMission");
    	Unit unit = getUnit();
        while(isValid() && unit.getMovesLeft() > 0){
        	
        	if(unit.getLocation() instanceof Europe){
        		if(unit.isBetweenEuropeAndNewWorld()){
        			unit.setMovesLeft(0);
        			return;
        		}
        	}
            switch(state){
                case HUNTING:
                    hunt4Target(connection);
                    break;
                case TRANSPORTING:
                    gotoNearestPort(connection);
                    break;
            }
        }
    }
    
    private void hunt4Target(Connection  connection){
        Unit unit = getUnit();

        if(unit.getLocation() instanceof Europe){
        	moveUnitToAmerica(connection, unit);
            unit.setMovesLeft(0);
            return;
        }
        logger.finest("Privateer (" + unit.getId() + ") at " + unit.getTile() + " hunting");

        
        if(unit.getGoodsCount() > 0){
            state = PrivateerMissionState.TRANSPORTING;
            return;
        }
        
        final int MAX_TURNS_TO_TARGET = 1;
        PathNode pathToTarget = findTarget(MAX_TURNS_TO_TARGET);
        
        if (pathToTarget != null) {
        	target = pathToTarget.getLastNode().getTile();
            logger.finest("Privateer (" + unit.getId() + ") at " + unit.getTile() + " found target at " + target);
            
            pathToTarget = unit.findPath(target);
            Direction direction = moveTowards(connection, pathToTarget);
            if(direction == null){
            	
            	
            	
            	
            	logger.finest("Ending privateer (" + unit.getId() + ") turn, moves=" + unit.getMovesLeft());
            	unit.setMovesLeft(0);
                return;
            }
            
            if (unit.getMoveType(direction) == MoveType.ATTACK) {
            	logger.finest("Privateer (" + unit.getId() + ") at " + unit.getTile() + " attacking target");
                attack(connection, unit, direction);
            }
        } else {
            
            target = null;
            logger.finest("Privateer at " + unit.getTile() + " without target, wandering");
        	moveRandomly(connection);
        }
        
    	
    	
    	
    	unit.setMovesLeft(0);
    }
    
    private void gotoNearestPort(Connection connection){
        Unit unit = getUnit();
        
        if(isUnitInPort()){
            dumpCargoInPort(connection);
            state = PrivateerMissionState.HUNTING;
            return;
        }
        
        PathNode path = getValidPathForNearestPort();
        if(path == null){
            findNearestPort();
            if(nearestPort == null){
            	logger.finest("Failed to find port for goods");
                invalidateMission = true;
                return;
            }
            path = getValidPathForNearestPort();
            if(path == null){
            	logger.finest("Failed to deliver goods to " + nearestPort + ", no path");
            	invalidateMission = true;
                return;
            }
        }
        
        boolean moveToEurope = nearestPort instanceof Europe;
        
        Direction direction = moveTowards(connection, path);        
        
        if(direction == null){
            
        	
        	
        	
        	unit.setMovesLeft(0);
        	return;
        }
        
        if (moveToEurope && unit.getMoveType(direction) == MoveType.MOVE_HIGH_SEAS) {
        	moveUnitToEurope(connection, unit);
        	unit.setMovesLeft(0);
        	return;
        }
        
        if(unit.getMoveType(direction) == MoveType.MOVE){
        	Position unitPos = unit.getTile().getPosition();
        	Position ColPos = Map.getAdjacent(unitPos, direction);
        	Colony colony = getGame().getMap().getTile(ColPos).getColony();
        	if(colony == nearestPort){
        		move(connection, direction);
        		return;
        	}
        	else{
        		String errMsg = "Privateer (" + unit.getId() + ") with PrivateerMission trying to enter settlement";
        		throw new IllegalStateException(errMsg);
        	}
        }
        
        
    	
    	
    	
    	unit.setMovesLeft(0);
    }
        
    private PathNode getValidPathForNearestPort(){
        Unit unit = getUnit();
        Player player = unit.getOwner();
        
        if(nearestPort == null){
        	return null;
        }
        
        if(nearestPort instanceof Europe){
            if(player.getEurope() == null){
                nearestPort = null;
                return null;
            }       
            return getGame().getMap().findPathToEurope(unit,unit.getTile());
        }
        
        Colony nearestColony = (Colony) nearestPort;
        if(nearestColony == null 
        		|| nearestColony.isDisposed() 
        		|| nearestColony.getOwner() != player){
            nearestPort = null;
            return null;
        }

        return unit.findPath(nearestColony.getTile());
    }    
    
    private void findNearestPort(){        
        nearestPort = null;
        Unit unit = getUnit();
        
        PathNode path = findNearestColony(unit);
        if(path != null){
            nearestPort = path.getLastNode().getTile().getColony();
        }
        else{
            Europe europe = unit.getOwner().getEurope();
            if(europe != null){
                nearestPort = europe;
            }
        }
    }
    
    private boolean isUnitInPort(){
        if(nearestPort == null){
            return false;
        }
        
        Unit unit = getUnit();
        
        if(nearestPort instanceof Europe){
            return unit.getLocation() == nearestPort;
        }
        
        return unit.getTile() == nearestPort.getTile();   
    }
    
    private void dumpCargoInPort(Connection connection){
    	logger.finest("Dumping goods");
        Unit unit = getUnit();
        boolean inEurope = unit.getLocation() instanceof Europe; 
                
        List<Goods> goodsLst = new ArrayList<Goods>(unit.getGoodsList());
        for(Goods goods : goodsLst){
            if(inEurope){
            	logger.finest("Before dumping: money=" + unit.getOwner().getGold());
                sellCargoInEurope(connection, unit, goods);
            	logger.finest("After dumping: money=" + unit.getOwner().getGold());
            } else{
            	Colony colony = unit.getTile().getColony();
            	logger.finest("Before dumping: " +  colony.getGoodsCount(goods.getType()) + " " + goods.getType());
                unloadCargoInColony(connection, unit, goods);
            	logger.finest("After dumping: " +  colony.getGoodsCount(goods.getType()) + " " + goods.getType());
            }
        }
        
        List<Unit> unitLst = new ArrayList<Unit>(unit.getUnitList());
        for(Unit u : unitLst){
            unitLeavesShip(connection, u);
        }
    }
    
        
    public static boolean isValid(AIUnit aiUnit) {
        Unit unit = aiUnit.getUnit();
        if (unit == null || unit.isDisposed() || !unit.hasAbility("model.ability.piracy" )){
            return false;
        }
        
        if(unit.isNaval() && unit.isUnderRepair()){
        	return false;
        }
        
        if(!(aiUnit.getMission() instanceof PrivateerMission) 
        		&& (unit.getGoodsCount() > 0 || unit.getUnitCount() > 0)){
        	return false;
        }
        
        AIPlayer aiPlayer = (AIPlayer) aiUnit.getAIMain().getAIObject(unit.getOwner().getId());
        
        if(TransportMission.getPlayerNavalTransportMissionCount(aiPlayer, unit) == 0){
        	logger.finest("Player has no other naval units than this one");
            return false;
        }
        return true;
    }
    
    
    public boolean isValid() {
    	if(!isValid(getAIUnit())){
    		return false;
    	}
    	if(invalidateMission){
    		return false;
    	}
        return true;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        
        out.writeAttribute("unit", getUnit().getId());
        out.writeAttribute("state", state.toString());

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));
        state = PrivateerMissionState.valueOf(in.getAttributeValue(null, "state"));
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "privateerMission";
    }
    
    
    public String getDebuggingInfo() {
    	StringBuffer sb = new StringBuffer("State: " + state.name());
    	if(state == PrivateerMissionState.HUNTING && target != null){
    		Unit targetUnit = target.getDefendingUnit(getUnit());
    		if(targetUnit != null){
    			String coord = " (" + target.getX() + "," + target.getY() + ")";
    			sb.append(" target=" + targetUnit + coord);
    		}
    	}
        return sb.toString();
    }
    
    
    public static int getModifierValueForTarget(CombatModel combatModel, Unit attacker, Unit defender){
    	
    	int modifier = 100;
    	modifier += defender.getGoodsCount() * 200;  
    	modifier += defender.getUnitCount() * 100;
        
        
        if(defender.isOffensiveUnit()){
        	modifier -= combatModel.getDefencePower(attacker, defender) * 100;
        }
        
        return modifier;
    }
}
