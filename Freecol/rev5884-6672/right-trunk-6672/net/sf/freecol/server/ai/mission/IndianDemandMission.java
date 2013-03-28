

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
import net.sf.freecol.common.model.GoodsContainer;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Market;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.DeliverGiftMessage;
import net.sf.freecol.common.networking.LoadCargoMessage;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.ai.AIObject;
import net.sf.freecol.server.ai.AIUnit;

import org.w3c.dom.Element;


public class IndianDemandMission extends Mission {

    private static final Logger logger = Logger.getLogger(IndianDemandMission.class.getName());

    
    private Colony target;

    
    private boolean completed;


    
    public IndianDemandMission(AIMain aiMain, AIUnit aiUnit, Colony target) {
        super(aiMain, aiUnit);

        this.target = target;

        if (!getUnit().getOwner().isIndian() || !getUnit().canCarryGoods()) {
            logger.warning("Only an indian which can carry goods can be given the mission: IndianBringGiftMission");
            throw new IllegalArgumentException("Only an indian which can carry goods can be given the mission: IndianBringGiftMission");
        }
    }

    
    public IndianDemandMission(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
    }

    
    public IndianDemandMission(AIMain aiMain, XMLStreamReader in) throws XMLStreamException {
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
                
                ArrayList<Goods> goodsList = new ArrayList<Goods>();
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
            
            Unit unit = getUnit();
            Direction r = moveTowards(connection, target.getTile());
            if (r != null &&
                getGame().getMap().getNeighbourOrNull(r, unit.getTile()) == target.getTile()
                && unit.getMovesLeft() > 0) {
                
                Element demandElement = Message.createNewRootElement("indianDemand");
                demandElement.setAttribute("unit", unit.getId());
                demandElement.setAttribute("colony", target.getId());

                Player enemy = target.getOwner();
                Goods goods = selectGoods(target);
                if (goods == null) {
                    if (enemy.getGold() == 0) {
                        
                        completed = true;
                        return;
                    }
                    demandElement.setAttribute("gold", String.valueOf(enemy.getGold() / 20));
                } else {
                    demandElement.appendChild(goods.toXMLElement(null, demandElement.getOwnerDocument()));
                }
                if (!unit.isVisibleTo(enemy)) {
                    demandElement.appendChild(unit.toXMLElement(enemy, demandElement.getOwnerDocument()));
                }

                Element reply = null;
                try {
                    reply = connection.ask(demandElement);
                } catch (IOException e) {
                    logger.warning("Could not send \"demand\"-message!");
                }
                if (reply == null) {
                    completed = true;
                    return;
                }

                boolean accepted = Boolean.valueOf(reply.getAttribute("accepted")).booleanValue();
                int tension = 0;
                int unitTension = unit.getOwner().getTension(enemy).getValue();
                if (unit.getIndianSettlement() != null) {
                    unitTension += unit.getIndianSettlement().getOwner().getTension(enemy).getValue();
                }
                int difficulty = FreeCol.getSpecification().getIntegerOption("model.option.nativeDemands")
                    .getValue();
                if (accepted) {
                    
                    tension = -(5 - difficulty) * 50;
                    unit.getOwner().modifyTension(enemy, tension);
                    if (unitTension <= Tension.Level.HAPPY.getLimit() &&
                        (goods == null || goods.getType().isFoodType())) {
                        DeliverGiftMessage message = new DeliverGiftMessage(getUnit(), target, getUnit().getGoodsIterator().next());
                        try {
                            connection.sendAndWait(message.toXMLElement());
                        } catch (IOException e) {
                            logger.warning("Could not send \"deliverGift\"-message!");
                        }
                    }
                } else {
                    tension = (difficulty + 1) * 50;
                    unit.getOwner().modifyTension(enemy, tension);
                    if (unitTension >= Tension.Level.CONTENT.getLimit()) {
                        
                        attack(connection, unit, r);
                    }
                }
                completed = true;
            }
        }

        
        moveRandomly(connection);
    }

    
    public Goods selectGoods(Colony target) {
        Tension.Level tension = getUnit().getOwner().getTension(target.getOwner()).getLevel();
        int dx = FreeCol.getSpecification().getIntegerOption("model.option.nativeDemands")
            .getValue() + 1;
        GoodsType food = FreeCol.getSpecification().getGoodsType("model.goods.food");
        Goods goods = null;
        GoodsContainer warehouse = target.getGoodsContainer();
        if (tension.compareTo(Tension.Level.CONTENT) <= 0 &&
            warehouse.getGoodsCount(food) >= 100) {
            int amount = (warehouse.getGoodsCount(food) * dx) / 6;
            if (amount > 0) {
                return new Goods(getGame(), target, food, capAmount(amount, dx));
            }
        } else if (tension.compareTo(Tension.Level.DISPLEASED) <= 0) {
            Market market = target.getOwner().getMarket();
            int value = 0;
            List<Goods> warehouseGoods = warehouse.getCompactGoods();
            for (Goods currentGoods : warehouseGoods) {
                int goodsValue = market.getSalePrice(currentGoods);
                if (currentGoods.getType().isFoodType() ||
                    currentGoods.getType().isMilitaryGoods()) {
                    continue;
                } else if (goodsValue > value) {
                    value = goodsValue;
                    goods = currentGoods;
                }
            }
            if (goods != null) {
                goods.setAmount(capAmount(goods.getAmount(), dx));
                return goods;
            }
        } else {
            
            for (GoodsType preferred : FreeCol.getSpecification().getGoodsTypeList()) {
                if (preferred.isMilitaryGoods()) {
                    int amount = warehouse.getGoodsCount(preferred);
                    if (amount > 0) {
                        return new Goods(getGame(), target, preferred, capAmount(amount, dx));
                    }
                }
            }
            
            for (GoodsType preferred : FreeCol.getSpecification().getGoodsTypeList()) {
                if (preferred.isBuildingMaterial() && preferred.isStorable()) {
                    int amount = warehouse.getGoodsCount(preferred);
                    if (amount > 0) {
                        return new Goods(getGame(), target, preferred, capAmount(amount, dx));
                    }
                }
            }
            
            for (GoodsType preferred : FreeCol.getSpecification().getGoodsTypeList()) {
                if (preferred.isTradeGoods()) {
                    int amount = warehouse.getGoodsCount(preferred);
                    if (amount > 0) {
                        return new Goods(getGame(), target, preferred, capAmount(amount, dx));
                    }
                }
            }
            
            for (GoodsType preferred : FreeCol.getSpecification().getGoodsTypeList()) {
                if (preferred.isRefined() && preferred.isStorable()) {
                    int amount = warehouse.getGoodsCount(preferred);
                    if (amount > 0) {
                        return new Goods(getGame(), target, preferred, capAmount(amount, dx));
                    }
                }
            }
        }

        
        Market market = target.getOwner().getMarket();
        int value = 0;
        List<Goods> warehouseGoods = warehouse.getCompactGoods();
        for (Goods currentGoods : warehouseGoods) {
            int goodsValue = market.getSalePrice(currentGoods);
            if (goodsValue > value) {
                value = goodsValue;
                goods = currentGoods;
            }
        }
        if (goods != null) {
            goods.setAmount(capAmount(goods.getAmount(), dx));
        }
        return goods;
    }

    private int capAmount(int amount, int difficulty) {
        int finalAmount = Math.max((amount * difficulty) / 6, 1);
        
        finalAmount = Math.min(finalAmount, 100);
        return finalAmount;
    }

    
    private boolean hasGift() {
        return (getUnit().getSpaceLeft() == 0);
    }

    
    public boolean isValid() {
        
        
        return (!completed && target != null && !target.isDisposed() && target.getTile().getColony() == target &&
                getUnit().getIndianSettlement() != null);
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("unit", getUnit().getId());
        out.writeAttribute("target", target.getId());
        out.writeAttribute("completed", Boolean.toString(completed));

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setAIUnit((AIUnit) getAIMain().getAIObject(in.getAttributeValue(null, "unit")));

        target = (Colony) getGame().getFreeColGameObject(in.getAttributeValue(null, "target"));
        completed = Boolean.valueOf(in.getAttributeValue(null, "completed")).booleanValue();

        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "indianDemandMission";
    }

    
    public String getDebuggingInfo() {
        if (getUnit().getIndianSettlement() == null) {
            return "invalid";
        }
        final String targetName = (target != null) ? target.getName() : "null";
        if (!hasGift()) {
            return "[" + targetName + "] Getting gift: "
                    + getUnit().getIndianSettlement().getTile().getPosition();
        } else {
            return "[" + targetName + "] " + getUnit().getGoodsIterator().next().getNameKey();
        }
    }
}
