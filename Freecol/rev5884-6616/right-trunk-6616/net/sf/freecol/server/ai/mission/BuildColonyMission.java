

package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.networking.BuildColonyMessage;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.ai.AIColony;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;


public class BuildColonyMission extends Mission {

    private static final Logger logger = Logger.getLogger(BuildColonyMission.class.getName());

    
    private Tile target;

    
    private int colonyValue;

    
    private boolean doNotGiveUp = false;

    private boolean colonyBuilt = false;


    
    public BuildColonyMission(AIMain aiMain, AIUnit aiUnit, Tile target, int colonyValue) {
        super(aiMain, aiUnit);

        this.target = target;
        this.colonyValue = colonyValue;

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        if (!getUnit().isColonist()) {
            logger.warning("Only colonists can build a new Colony.");
            throw new IllegalArgumentException("Only colonists can build a new Colony.");
        }
    }

    
    public BuildColonyMission(AIMain aiMain, AIUnit aiUnit) {
        super(aiMain, aiUnit);

        this.target = null;
        this.colonyValue = -1;
        this.doNotGiveUp = true;

        if (!getUnit().isColonist()) {
            logger.warning("Only colonists can build a new Colony.");
            throw new IllegalArgumentException("Only colonists can build a new Colony.");
        }
    }

    
    public BuildColonyMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public BuildColonyMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }

    
    public void doMission(Connection connection) {
        Unit unit = getUnit();

        if (!isValid()) {
            return;
        }

        if (getUnit().getTile() == null) {
            return;
        }

        if (target == null || doNotGiveUp
            && (colonyValue != getUnit().getOwner().getColonyValue(target) || target.getSettlement() != null)) {
            target = findColonyLocation(getUnit());
            if (target == null) {
                doNotGiveUp = false;
                return;
            } else {
                colonyValue = getUnit().getOwner().getColonyValue(target);
            }
        }

        
        if (getUnit().getTile() != null) {
            if (target != getUnit().getTile()) {
                Direction r = moveTowards(connection, target);
                moveButDontAttack(connection, r);
            }
            if (getUnit().canBuildColony() && target == getUnit().getTile()
                && getUnit().getMovesLeft() > 0) {
                String name = Messages.getDefaultSettlementName(unit.getOwner(), false);
                Element reply = null;

                try {
                    reply = connection.ask(new BuildColonyMessage(name, unit).toXMLElement());
                } catch (IOException e) {
                    logger.warning("Could not send BuildColony message.");
                }
                if (reply != null) {
                    colonyBuilt = true;
                    Settlement settlement = unit.getTile().getSettlement();
                    AIColony aiColony = (AIColony) getAIMain().getAIObject(settlement);
                    getAIUnit().setMission(new WorkInsideColonyMission(getAIMain(), getAIUnit(), aiColony));
                } else {
                    logger.warning("Could not build an AI colony on tile "+getUnit().getTile().getPosition().toString());
                }
            }
        }
    }

    
    public Tile getTransportDestination() {
        if (target == null) {
            if (getUnit().isOnCarrier()) {
                return (Tile) ((Unit) getUnit().getLocation()).getEntryLocation();
            } else {
                return (Tile) getUnit().getOwner().getEntryLocation();
            }
        }

        if (getUnit().isOnCarrier()) {
            return target;
        } else if (getUnit().getLocation().getTile() == target) {
            return null;
        } else if (getUnit().getTile() == null) {
            return target;
        } else if (getUnit().findPath(target) == null) {
            return target;
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

    
    public static Tile findColonyLocation(Unit unit) {
        
        Game game = unit.getGame();
        
        Tile startTile = null;
        if (unit.isOnCarrier()) {
            Unit carrier = (Unit) unit.getLocation();
            startTile = carrier.getTile();
        } else if (unit.getLocation() instanceof Europe) {
            startTile = (Tile) unit.getEntryLocation();
        } else {
            startTile = unit.getTile();
        }
            
        if (startTile == null) {
            return null;
        }

        Tile bestTile = null;
        int highestColonyValue = 0;
        int maxNumberofTiles = 500;
        int tileCounter = 0;

        
        
        
        boolean gameStart = false;
        if (unit.getGame().getTurn().getNumber() < 10 && unit.isOnCarrier()) {
            gameStart = true;
        }
        
        Iterator<Position> it = game.getMap().getFloodFillIterator(startTile.getPosition());
        
        while (it.hasNext()) {
            Tile tile = game.getMap().getTile(it.next());
            int newColonyValue = -1;
            int tileColonyValue = unit.getOwner().getColonyValue(tile);
            
            if (tileColonyValue > 0
            	&& (tileColonyValue + 10000) > highestColonyValue) {
            	
                if (tile != startTile) {
                    PathNode path;

                    if (unit.isOnCarrier()) {
                        Unit carrier = (Unit) unit.getLocation();
                        path = game.getMap().findPath(unit, startTile, tile, carrier);
                    } else {
                        path = game.getMap().findPath(unit, startTile, tile);
                    }
                    if (path != null) {
                        newColonyValue = 10000
                            + tileColonyValue
                            - path.getTotalTurns()
                            * ((unit.getGame().getTurn().getNumber() < 10
                                && unit.isOnCarrier()) ? 25 : 4);
                    }
                } else {
                    newColonyValue = 10000 + tileColonyValue;
                }
                if (newColonyValue > highestColonyValue) {
                    highestColonyValue = newColonyValue;
                    bestTile = tile;
                }
            }
            
            
            
            if ((++tileCounter >= maxNumberofTiles)
                && (!gameStart || bestTile!= null)) break;
        }
        if (bestTile == null) {
            logger.info("Unit " + unit.getId() + " unsuccessfully searched for colony spot");
        }
        return bestTile;
    }

    
    public boolean isValid() {
        return (!colonyBuilt && (doNotGiveUp || target != null
                                 && target.getSettlement() == null
                                 && colonyValue <= getUnit().getOwner().getColonyValue(target)));
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("unit", getUnit().getId());
        if (target != null) {
            out.writeAttribute("target", target.getId());
        }
        out.writeAttribute("doNotGiveUp", Boolean.toString(doNotGiveUp));
        out.writeAttribute("colonyBuilt", Boolean.toString(colonyBuilt));

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));

        final String targetStr = in.getAttributeValue(null, "target");
        if (targetStr != null) {
            target = (Tile) getGame().getFreeColGameObject(targetStr);
        } else {
            target = null;
        }

        final String doNotGiveUpStr = in.getAttributeValue(null, "doNotGiveUp");
        if (doNotGiveUpStr != null) {
            doNotGiveUp = Boolean.valueOf(doNotGiveUpStr).booleanValue();
        } else {
            doNotGiveUp = false;
        }
        colonyBuilt = Boolean.valueOf(in.getAttributeValue(null, "colonyBuilt")).booleanValue();
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "buildColonyMission";
    }

    
    public String getDebuggingInfo() {
        final String targetName = (target != null) ? target.getPosition().toString() : "unassigned";
        return targetName + " " + colonyValue + (doNotGiveUp ? "!" : "");
    }
}
