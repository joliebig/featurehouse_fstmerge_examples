

package net.sf.freecol.server.ai.mission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.GoodsContainer;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.DeliverGiftMessage;
import net.sf.freecol.common.networking.LoadCargoMessage;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;


public class IndianBringGiftMission extends Mission {
    private static final Logger logger = Logger.getLogger(IndianBringGiftMission.class.getName());




    
    private Colony target;

    
    private boolean giftDelivered;


    
    public IndianBringGiftMission(AIMain aiMain, AIUnit aiUnit, Colony target) {
        super(aiMain, aiUnit);

        this.target = target;
        this.giftDelivered = false;

        if (!getUnit().getOwner().isIndian() || !getUnit().canCarryGoods()) {
            logger.warning("Only an indian which can carry goods can be given the mission: IndianBringGiftMission");
            throw new IllegalArgumentException("Only an indian which can carry goods can be given the mission: IndianBringGiftMission");
        }
    }

    
    public IndianBringGiftMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public IndianBringGiftMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
        super(aiMain);
        readFromXML(in);
    }

    
    public void doMission(Connection connection) {
        
        if (!isValid()) {
            
            return;
        }
        
        if (!hasGift()) {
            if (getUnit().getTile() != getUnit().getIndianSettlement().getTile()) {
                
                Direction r = moveTowards(connection, getUnit().getIndianSettlement().getTile());
                moveButDontAttack(connection, r);
            } else {
                
                List<Goods> goodsList = new ArrayList<Goods>();
                GoodsContainer gc = getUnit().getIndianSettlement().getGoodsContainer();
                for (GoodsType goodsType : FreeCol.getSpecification().getNewWorldGoodsTypeList()) {
                    if (gc.getGoodsCount(goodsType) >= IndianSettlement.KEEP_RAW_MATERIAL + 25) {
                        goodsList.add(new Goods(getGame(), getUnit().getIndianSettlement(),
                                                goodsType,
                                                getRandom().nextInt(15) + 10));
                    }
                }

                if (goodsList.size() > 0) {
                    Goods goods = goodsList.get(getRandom().nextInt(goodsList.size()));
                    LoadCargoMessage message = new LoadCargoMessage(goods, getUnit());
                    try {
                        connection.sendAndWait(message.toXMLElement());
                    } catch (IOException e) {
                        logger.warning("Could not send \"loadCargo\"-message!");
                    }
                }
            }
        } else {
            
            Direction r = moveTowards(connection, target.getTile());
            if (r != null && 
                getGame().getMap().getNeighbourOrNull(r, getUnit().getTile()) == target.getTile()) {
                
                DeliverGiftMessage message = new DeliverGiftMessage(getUnit(), target, getUnit().getGoodsIterator().next());
                try {
                    connection.sendAndWait(message.toXMLElement());
                } catch (IOException e) {
                    logger.warning("Could not send \"deliverGift\"-message!");
                }

                giftDelivered = true;
                getUnit().getOwner().modifyTension(target.getOwner(), 1);
            }
        }

        
        moveRandomly(connection);
    }

    
    private boolean hasGift() {
        return (getUnit().getSpaceLeft() == 0);
    }

    
    public boolean isValid() {
        return target != null && !target.isDisposed() && target.getTile().getColony() == target && !giftDelivered
            && isValidMission(getUnit().getOwner(), target.getOwner()) && getUnit().getIndianSettlement() != null;
    }

    
    public static boolean isValidMission(Player owner, Player targetPlayer) {
        switch (owner.getStance(targetPlayer)) {
        case UNCONTACTED: case WAR: case CEASE_FIRE:
            break;
        case PEACE: case ALLIANCE:
            return owner.getTension(targetPlayer).getLevel() != null
                && owner.getTension(targetPlayer).getLevel().compareTo(Tension.Level.HAPPY) <= 0;
        }
        return false;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("unit", getUnit().getId());
        if (target!=null) {
            
            out.writeAttribute("target", target.getId());
        }
        out.writeAttribute("giftDelivered", Boolean.toString(giftDelivered));

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));

        target = (Colony) getGame().getFreeColGameObject(in.getAttributeValue(null, "target"));
        giftDelivered = Boolean.valueOf(in.getAttributeValue(null, "giftDelivered")).booleanValue();

        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "indianBringGiftMission";
    }

    
    public String getDebuggingInfo() {
        if (!hasGift()) {
            return "[" + target.getName() + "] Getting gift: "
                    + getUnit().getIndianSettlement().getTile().getPosition();
        } else {
            return "[" + target.getName() + "] " + getUnit().getGoodsIterator().next().getName();
        }
    }
}
