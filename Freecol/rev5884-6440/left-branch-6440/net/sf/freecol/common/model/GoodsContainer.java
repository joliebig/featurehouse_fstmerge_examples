

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;

import org.w3c.dom.Element;


public class GoodsContainer extends FreeColGameObject {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(Location.class.getName());

    public static final int CARGO_SIZE = 100;
    public static final String STORED_GOODS_TAG = "storedGoods";
    public static final String OLD_STORED_GOODS_TAG = "oldStoredGoods";

    
    private Map<GoodsType, Integer> storedGoods = new HashMap<GoodsType, Integer>();

    
    private Map<GoodsType, Integer> oldStoredGoods = new HashMap<GoodsType, Integer>();

    
    private final Location parent;

    
    public GoodsContainer(Game game, Location parent) {
        super(game);

        if (parent == null) {
            throw new IllegalArgumentException("Location of GoodsContainer must not be null!");
        }

        this.parent = parent;
    }

    
    public GoodsContainer(Game game, Location parent, XMLStreamReader in) throws XMLStreamException {
        super(game, in);

        if (parent == null) {
            throw new IllegalArgumentException("Location of GoodsContainer must not be null!");
        }

        this.parent = parent;
        readFromXML(in);
    }

    
    public GoodsContainer(Game game, Location parent, Element e) {
        super(game, e);

        if (parent == null) {
            throw new IllegalArgumentException("Location of GoodsContainer must not be null!");
        }

        this.parent = parent;
        readFromXMLElement(e);
    }

    
    public void addGoods(AbstractGoods g) {
        addGoods(g.getType(), g.getAmount());
    }

    
    public void addGoods(GoodsType type, int amount) {
        int oldAmount = getGoodsCount(type);
        int newAmount = oldAmount + amount;

        if (newAmount < 0) {
            throw new IllegalStateException("Operation would leave " + (newAmount) + " goods of type " 
                                            + type + " in Location " + parent);
        } else if (newAmount == 0) {
            storedGoods.remove(type);
        } else {
            storedGoods.put(type, newAmount);
        }
        firePropertyChange(type.getId(), oldAmount, newAmount);
    }
    
    
    public Goods removeGoods(AbstractGoods g) {
        return removeGoods(g.getType(), g.getAmount());
    }

    public Goods removeGoods(GoodsType type) {
        return removeGoods(type, Integer.MAX_VALUE);
    }

    
    public Goods removeGoods(GoodsType type, int amount) {
        int oldAmount = getGoodsCount(type);
        int newAmount = oldAmount - amount;
        Goods removedGoods;
        if (newAmount > 0) {
            removedGoods = new Goods(getGame(), parent, type, amount);
            storedGoods.put(type, newAmount);
        } else {
            removedGoods = new Goods(getGame(), parent, type, oldAmount);
            storedGoods.remove(type);
        }
        firePropertyChange(type.getId(), oldAmount, newAmount);
        return removedGoods;
    }

    
    public void removeAbove(int newAmount) {
        for (GoodsType goodsType : storedGoods.keySet()) {
            if (goodsType.isStorable() && !goodsType.limitIgnored() && 
                storedGoods.get(goodsType) > newAmount) {
                setAmount(goodsType, newAmount);
            }
        }
    }

    private void setAmount(GoodsType goodsType, int newAmount) {
        int oldAmount = getGoodsCount(goodsType);
        if (newAmount == 0) {
            storedGoods.remove(goodsType);
        } else {
            storedGoods.put(goodsType, newAmount);
        }
        firePropertyChange(goodsType.getId(), oldAmount, newAmount);
    }        

    
    public void removeAll() {
        storedGoods.clear();
    }

    
    
    public boolean hasReachedCapacity(int amount) {
        for (GoodsType goodsType : storedGoods.keySet()) {
            if (goodsType.isStorable() && !goodsType.limitIgnored() && 
                storedGoods.get(goodsType) > amount) {
                return true;
            }
        }
        return false;
    }

    
    public boolean contains(Goods g) {
        throw new UnsupportedOperationException();
    }

    
    public int getGoodsCount(GoodsType type) {
        if (storedGoods.containsKey(type)) {
            return storedGoods.get(type).intValue();
        } else {
            return 0;
        }
    }

    
    public int getOldGoodsCount(GoodsType type) {
        if (oldStoredGoods.containsKey(type)) {
            return oldStoredGoods.get(type).intValue();
        } else {
            return 0;
        }
    }
    
    public Goods getGoods(GoodsType goodsType) {
        return new Goods(getGame(), parent, goodsType, getGoodsCount(goodsType));
    }


    
    public int getGoodsCount() {
        int count = 0;
        for (Integer amount : storedGoods.values()) {
            if (amount % CARGO_SIZE == 0) {
                count += amount/CARGO_SIZE;
            } else {
                count += amount/CARGO_SIZE + 1;
            }
        }
        return count;
    }


    
    public Iterator<Goods> getGoodsIterator() {
        return getGoods().iterator();
    }

    
    public List<Goods> getGoods() {
        ArrayList<Goods> totalGoods = new ArrayList<Goods>();

        for (GoodsType goodsType : storedGoods.keySet()) {
            int amount = storedGoods.get(goodsType).intValue();
            while (amount > 0) {
                totalGoods.add(new Goods(getGame(), parent, goodsType, (amount >= CARGO_SIZE ? CARGO_SIZE : amount)));
                amount -= CARGO_SIZE;
            }
        }

        return totalGoods;
    }

    
    
    public List<Goods> getCompactGoods() {
        ArrayList<Goods> totalGoods = new ArrayList<Goods>();

        for (Entry<GoodsType, Integer> entry : storedGoods.entrySet()) {
            if (entry.getValue() > 0) {
                totalGoods.add(new Goods(getGame(), parent, entry.getKey(), entry.getValue()));
            }
        }

        return totalGoods;
    }

    
    public List<Goods> getFullGoods() {
        ArrayList<Goods> totalGoods = new ArrayList<Goods>();

        for (GoodsType goodsType : storedGoods.keySet()) {
            totalGoods.add(new Goods(getGame(), parent, goodsType, storedGoods.get(goodsType)));
        }

        return totalGoods;
    }

    
    public void saveState() {
        oldStoredGoods.clear();
        for (Map.Entry<GoodsType, Integer> entry : storedGoods.entrySet()) {
            oldStoredGoods.put(entry.getKey(), new Integer(entry.getValue().intValue()));
        }
    }

    
    public void cleanAndReport() {
        if (!(parent instanceof Colony)) {
            return;
        }
        Colony colony = (Colony) parent;
        int limit = colony.getWarehouseCapacity();
        int adjustment = limit / CARGO_SIZE;

        for (GoodsType goodsType : storedGoods.keySet()) {
        	boolean ignoreLimits = !goodsType.isFoodType() && goodsType.limitIgnored(); 
            if (ignoreLimits || !goodsType.isStorable()) {
                continue;
            }
            ExportData exportData = colony.getExportData(goodsType);
            int low = exportData.getLowLevel() * adjustment;
            int high = exportData.getHighLevel() * adjustment;
            int amount = storedGoods.get(goodsType).intValue();
            int oldAmount = getOldGoodsCount(goodsType);
            if (amount > limit) {
                
                int waste = amount - limit;
                setAmount(goodsType, limit);
                addModelMessage(colony, ModelMessage.MessageType.WAREHOUSE_CAPACITY, goodsType,
                                "model.building.warehouseWaste",
                                "%goods%", Messages.getName(goodsType),
                                "%waste%", String.valueOf(waste),
                                "%colony%", colony.getName());
            } else if (amount == limit && oldAmount < limit) {
                
                addModelMessage(colony, ModelMessage.MessageType.WAREHOUSE_CAPACITY, goodsType,
                                "model.building.warehouseOverfull",
                                "%goods%", Messages.getName(goodsType),
                                "%colony%", colony.getName());
            } else if (amount > high && oldAmount <= high) {
                addModelMessage(colony, ModelMessage.MessageType.WAREHOUSE_CAPACITY, goodsType,
                                "model.building.warehouseFull",
                                "%goods%", Messages.getName(goodsType),
                                "%level%", String.valueOf(high),
                                "%colony%", colony.getName());
            } else if (amount < low && oldAmount >= low) {
                addModelMessage(colony, ModelMessage.MessageType.WAREHOUSE_CAPACITY, goodsType,
                                "model.building.warehouseEmpty",
                                "%goods%", Messages.getName(goodsType),
                                "%level%", String.valueOf(low),
                                "%colony%", colony.getName());
            }
        }

    }

    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        if (!storedGoods.isEmpty()) {
            out.writeStartElement(STORED_GOODS_TAG);
            for (Map.Entry<GoodsType, Integer> entry : storedGoods.entrySet()) {
                out.writeStartElement(Goods.getXMLElementTagName());
                out.writeAttribute("type", entry.getKey().getId());
                out.writeAttribute("amount", entry.getValue().toString());
                out.writeEndElement();
            }
            out.writeEndElement();
        }
        if (!oldStoredGoods.isEmpty()) {
            out.writeStartElement(OLD_STORED_GOODS_TAG);
            for (Map.Entry<GoodsType, Integer> entry : oldStoredGoods.entrySet()) {
                out.writeStartElement(Goods.getXMLElementTagName());
                out.writeAttribute("type", entry.getKey().getId());
                out.writeAttribute("amount", entry.getValue().toString());
                out.writeEndElement();
            }
            out.writeEndElement();
        }
        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        storedGoods.clear();
        oldStoredGoods.clear();

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(STORED_GOODS_TAG)) {
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    if (in.getLocalName().equals(Goods.getXMLElementTagName())) {
                        GoodsType goodsType = Specification.getSpecification().getGoodsType(in.getAttributeValue(null, "type"));
                        Integer amount = new Integer(in.getAttributeValue(null, "amount"));
                        storedGoods.put(goodsType, amount);
                    }
                    in.nextTag();
                }
            } else if (in.getLocalName().equals(OLD_STORED_GOODS_TAG)) {
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    if (in.getLocalName().equals(Goods.getXMLElementTagName())) {
                        GoodsType goodsType = Specification.getSpecification().getGoodsType(in.getAttributeValue(null, "type"));
                        Integer amount = new Integer(in.getAttributeValue(null, "amount"));
                        oldStoredGoods.put(goodsType, amount);
                    }
                    in.nextTag();
                }
            }
        }
    }


    
    public static String getXMLElementTagName() {
        return "goodsContainer";
    }
    
    
    
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("GoodsContainer with: ");
        for (Map.Entry<GoodsType, Integer> entry : storedGoods.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + ", ");
        }
        return sb.toString();
    }

}
